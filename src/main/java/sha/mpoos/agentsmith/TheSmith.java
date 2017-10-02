package sha.mpoos.agentsmith;

import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sha.mpoos.agentsmith.client.Client;
import sha.mpoos.agentsmith.config.SmithConfig;
import sha.mpoos.agentsmith.dao.AgentSessionActionDao;
import sha.mpoos.agentsmith.dao.AgentSessionDao;
import sha.mpoos.agentsmith.entity.AgentSession;
import sha.mpoos.agentsmith.entity.AgentSessionAction;
import sha.mpoos.agentsmith.entity.Proxy;
import sha.mpoos.agentsmith.entity.Target;
import sha.mpoos.agentsmith.proxy.manager.ProxyManager;
import sha.mpoos.agentsmith.reader.AgentReader;
import sha.mpoos.agentsmith.reader.TargetListReader;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TheSmith {
    private static final Logger log = Logger.getLogger("TheSmith");
    @Autowired
    private AgentReader agentReader;
    @Autowired
    private TargetListReader targetListReader;
    @Autowired
    private SmithConfig smithConfig;
    @Autowired
    private ProxyManager proxyManager;
    @Autowired
    private AgentSessionActionDao agentSessionActionDao;
    @Autowired
    private AgentSessionDao agentSessionDao;
    @Autowired
    private Client client;

    public TheSmith() {
    }

//    @PostConstruct
    public void wakeUp() throws InterruptedException {
        int concurrentSessions = Math.max(Runtime.getRuntime().availableProcessors(), smithConfig.getConcurrentUserCount());
        log.info("Launching the smith with " + smithConfig.getThreadCount() + " total agents and " + concurrentSessions + " concurrent ones");
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentSessions);
        List<Callable<Object>> todo = new ArrayList<>(smithConfig.getThreadCount());
        for (int counter = 0; counter < smithConfig.getThreadCount(); counter++)
            todo.add(Executors.callable(buildJob()));
        List<Future<Object>> answers = executorService.invokeAll(todo);
        log.info("Finished!");
        executorService.shutdown();
    }

    private Runnable buildJob() {
        return () -> {
            String agent = agentReader.randomAgent();
            Proxy proxy = proxyManager.returnBest();
            for (URI target : targetListReader.shuffleList()) {
                int response = 0;
                try {
                    response = client.sendGet(agent, target, proxy, smithConfig.getClientTimeoutSecs());
                } catch (Exception e) {
                    log.warning("Error in sending GET: " + e.getMessage());
                }
                proxyManager.updateProxyStatistics(response, proxy);
                try {
                    Thread.sleep(smithConfig.getSleepTimeMillis());
                } catch (InterruptedException e) {
                    log.warning("Error in sleeping: " + e.getMessage());
                }
            }
        };
    }

    @Async
    public void launchSession(AgentSession agentSession) throws InterruptedException {
        int concurrentSessions = Math.max(Runtime.getRuntime().availableProcessors(), agentSession.getConcurrentRequestCount());
        log.info("Launching the smith with " + agentSession.getTotalRequestCount() + " total agents and " + concurrentSessions + " concurrent ones");
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentSessions);
        List<Callable<Object>> todo = new ArrayList<>(agentSession.getTotalRequestCount());
        for (int counter = 0; counter < agentSession.getTotalRequestCount(); counter++)
            todo.add(Executors.callable(buildJob(agentSession)));
        List<Future<Object>> answers = executorService.invokeAll(todo);
        executorService.shutdown();
        agentSession.setFinishDate(new Date());
        agentSessionDao.save(agentSession);
    }

    private Runnable buildJob(final AgentSession agentSession) {
        return () -> {
            String agent = agentReader.randomAgent();
            Proxy proxy = proxyManager.returnBest();
            for (Target target : agentSession.getTargetCollection().shuffleTargets()) {
                log.info("Firing req' for target: \'" + target.getAddress() + "\', proxy: " + proxy.getId());
                AgentSessionAction action = new AgentSessionAction();
                action.setProxy(proxy);
                action.setTarget(target);
                int response = 0;
                long responseTime = -1;
                try {
                    long before = System.currentTimeMillis();
                    response = client.sendGet(agent, target.getAddressAsURI(), proxy, smithConfig.getClientTimeoutSecs());
                    responseTime = System.currentTimeMillis() - before;
                } catch (Exception e) {
                    log.warning("Error in sending GET: " + e.getMessage());
                }
                action.setResponseTime(responseTime);
                action.setStatusCode(response);
                proxyManager.updateProxyStatistics(response, proxy);
                log.info("Inserting session-action: " + action);
                try {
//                    AgentSession saveSubject = agentSessionDao.findOne(agentSession.getId());
//                    action.setSession(saveSubject);
//                    saveSubject.addSessionAction(action);
//                    agentSessionDao.save(saveSubject);
                    //todo: make it work!!!
                    log.info("Inserted session-action: " + action);
                } catch (Throwable t){
                    log.log(Level.WARNING, "WTF?!", t);
                }
                /*try {
                    Thread.sleep(smithConfig.getSleepTimeMillis());
                } catch (InterruptedException e) {
                    log.warning("Error in sleeping: " + e.getMessage());
                }*/
            }
        };
    }
}
