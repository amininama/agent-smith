package sha.mpoos.agentsmith;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sha.mpoos.agentsmith.client.Client;
import sha.mpoos.agentsmith.config.SmithConfig;
import sha.mpoos.agentsmith.crawler.entity.Proxy;
import sha.mpoos.agentsmith.random.Randomizer;
import sha.mpoos.agentsmith.reader.AgentReader;
import sha.mpoos.agentsmith.reader.ProxyReader;
import sha.mpoos.agentsmith.reader.TargetListReader;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
    private ProxyReader proxyReader;

    public TheSmith() {
    }

    @PostConstruct
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
            Client client = new Client();
            String agent = agentReader.randomAgent();
            Proxy proxy = proxyReader.testAndReturnRandom();
            for (URI target : targetListReader.shuffleList()) {
                HttpResponse response = null;
                try {
                    response = client.sendGet(agent, target, proxy);
                } catch (Exception e) {
                    log.warning("Error in sending GET: " + e.getMessage());
                }
                proxyReader.updateProxyStatistics(response, proxy);
                try {
                    Thread.sleep(smithConfig.getSleepTimeMillis());
                } catch (InterruptedException e) {
                    log.warning("Error in sleeping: " + e.getMessage());
                }
            }
        };
    }
}
