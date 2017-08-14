package sha.mpoos.agentsmith.proxy.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sha.mpoos.agentsmith.client.Client;
import sha.mpoos.agentsmith.dao.ProxyDao;
import sha.mpoos.agentsmith.entity.Proxy;
import sha.mpoos.agentsmith.proxy.crawler.ProxyCrawler;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by amin on 7/17/17.
 */
@Component
public class ProxyManager {
    private static final Logger log = Logger.getLogger("ProxyManager");

    @Autowired
    private ProxyCrawler[] crawlers;
    @Value("${proxies.timeout.secs}")
    int tolerableProxyTimeout;
    private List<Proxy> proxyList;
    @Autowired
    private ProxyDao proxyDao;
    @Autowired
    private Client client;

    public ProxyManager() {
        this.proxyList = new ArrayList<>();
    }

    @PostConstruct
    public void init() throws IOException {
        loadFromDB();
    }

    public void loadFromDB() {
        proxyList.addAll((Collection<? extends Proxy>) proxyDao.findAll());
        log.info("Loaded " + proxyList.size() + " proxies from DB");
    }

    public Proxy testAndReturnRandom() {
        Random random = new Random();
        while (true) {
            Proxy proxy = this.proxyList.get(random.nextInt(proxyList.size()));
            boolean isFunctional = test(proxy);
            log.info("Tested proxy: \'" + proxy.getId() + "\', active: " + isFunctional);
            if (isFunctional) return proxy;
        }
    }

    public Proxy returnRandom() {
        Random random = new Random();
        return this.proxyList.get(random.nextInt(proxyList.size()));
    }

    public Proxy returnBest() {
        List<Proxy> list = proxyDao.findBest(new PageRequest(0, 1));
        Proxy best = list.get(0);
        best.setLastFetchDate(new Date());
        proxyDao.save(best);
        return best;
    }

    public void updateProxyStatistics(int response, Proxy proxy) {
        proxy.updateStatsByTestResult(isSuccessful(response));
        proxyDao.save(proxy);
    }

    private boolean isSuccessful(int response) {
        return response / 100 == 2;
    }

    private boolean test(Proxy proxy) {
        boolean successful;
        try {
            int response = client.sendGet(null, new URI("https://google.com"), proxy, tolerableProxyTimeout, true);
            successful = isSuccessful(response);
        } catch (Throwable t) {
            successful = false;
        }
        proxy.updateStatsByTestResult(successful);
        proxyDao.save(proxy);
        return successful;
    }

    @Async
    public void refreshProxyList() {
        Set<Proxy> freshList = new HashSet<>();
        for (ProxyCrawler crawler : crawlers) {
            try {
                Set<Proxy> newOnes = crawler.fetchProxyInfo();
                freshList.addAll(newOnes);
            } catch (Throwable e) {
                log.warning("Error while refreshing proxy list for \'" + crawler.getClass().getName() + "\': " + e.getMessage());
            }
        }
        persist(freshList);
    }

    public void persist(Set<Proxy> proxies) {
        List<Proxy> newOnes = new LinkedList<>();
        for (Proxy proxy : proxies) {
            if (proxyDao.findByHost(proxy.getHost().toHostString()) == null)
                newOnes.add(proxy);
        }
        try {
            proxyDao.save(newOnes);
            this.proxyList.addAll(newOnes);
        } catch (Throwable e) {
            //do nothing
        }
        log.info("Refreshed internal proxy-list. added " + newOnes.size() + " new ones.");
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void healthCheck() {
        log.info("Starting proxy health check");
        Iterable<Proxy> all = proxyDao.findAll();
        for (Proxy proxy : all) {
            try {
                long before = System.currentTimeMillis();
                boolean success = test(proxy);
                log.info("tested proxy: " + proxy.getId() + ", success: " + success + ", rt: " + (System.currentTimeMillis() - before));
            } catch (Throwable t) {
                log.info("Shit happened! " + t.getMessage());
            }
        }
        log.info("finished proxy health check");
    }
}
