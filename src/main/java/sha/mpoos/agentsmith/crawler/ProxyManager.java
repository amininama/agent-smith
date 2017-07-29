package sha.mpoos.agentsmith.crawler;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sha.mpoos.agentsmith.dao.ProxyDao;
import sha.mpoos.agentsmith.entity.Proxy;

import javax.annotation.PostConstruct;
import java.io.IOException;
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

    public void updateProxyStatistics(HttpResponse response, Proxy proxy) {
        if (response == null || !isSuccessful(response)) {
            proxy.increaseFailedCount();
        } else {
            proxy.increaseSuccessCount();
            proxy.setLastSuccessfulUse(new Date());
        }
        proxyDao.save(proxy);
    }

    private boolean isSuccessful(HttpResponse response) {
        return response.getStatusLine().getStatusCode() / 100 == 2;
    }

    private boolean test(Proxy proxy) {
        boolean successful;
        HttpClient client = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
        HttpGet request = new HttpGet("https://google.com");
        RequestConfig config = RequestConfig.custom()
                .setProxy(proxy.getHost())
                .setConnectTimeout(tolerableProxyTimeout * 1000)
                .setSocketTimeout(tolerableProxyTimeout)
                .build();
        request.setConfig(config);
        try {
            HttpResponse response = client.execute(request);
            successful = isSuccessful(response);
            if (successful)
                proxy.increaseSuccessCount();
            else
                proxy.increaseFailedCount();
        } catch (Throwable t) {
            proxy.increaseFailedCount();
            successful = false;
        }
        proxyDao.save(proxy);
        return successful;
    }

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
}
