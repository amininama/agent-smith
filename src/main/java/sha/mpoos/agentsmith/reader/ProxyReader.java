package sha.mpoos.agentsmith.reader;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sha.mpoos.agentsmith.crawler.ProxyCrawler;
import sha.mpoos.agentsmith.crawler.dao.ProxyDao;
import sha.mpoos.agentsmith.entity.Proxy;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

@Service
public class ProxyReader {
    private static final Logger log = Logger.getLogger("ProxyReader");
    private Stack<Proxy> proxyList;
    private
    @Value("${proxies.source}")
    String address;
    private
    @Value("${proxies.load.on.boot}")
    boolean refreshListOnBootup;
    private
    @Value("${proxies.timeout.secs}")
    int tolerableProxyTimeout;
    @Autowired
    private ProxyDao proxyDao;
    @Autowired
    private ProxyCrawler[] crawlers;

    public ProxyReader() {
        proxyList = new Stack<>();
    }

    @PostConstruct
    public void init() throws IOException {
        if (refreshListOnBootup)
            refreshProxyList();
        loadFromDB();
    }

    @Deprecated
    private void loadFromFile() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(address)));
        String[] info = content.trim().split("\\n");
        for (String proxyString : info) {
            try {
                String[] proxyInfo = proxyString.split(":");
                String proxyAddr = proxyInfo[0];
                int proxyPort = Integer.parseInt(proxyInfo[1]);
                HttpHost host = new HttpHost(proxyInfo[0], proxyPort);
                Proxy proxy = new Proxy();
                proxy.setHost(host);
                proxyList.add(proxy);
            } catch (Exception e) {
                log.warning("Invalid proxy: \'" + proxyString + "\'");
            }
        }
        log.info("Loaded " + proxyList.size() + " proxies from file");
    }

    private void loadFromDB() {
        proxyList.addAll((Collection<? extends Proxy>) proxyDao.findAll());
        log.info("Loaded " + proxyList.size() + " proxies from DB");
    }

    private void refreshProxyList() {
        Set<Proxy> freshList = new HashSet<>();
        for (ProxyCrawler crawler : crawlers) {
            try {
                Set<Proxy> newOnes = crawler.fetchProxyInfo();
                freshList.addAll(newOnes);
            } catch (Throwable t) {
                log.warning("Error while refreshing proxy in \'" + crawler.getClass().getName() + "\' : " + t.getMessage());
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
        } catch (Throwable e) {
            //do nothing
        }
        log.info("Refreshed internal proxy-list. added " + newOnes.size() + " new ones.");
    }

    public List<Proxy> getProxyList() {
        return proxyList;
    }

    public void setProxyList(Stack<Proxy> targets) {
        this.proxyList = targets;
    }

    @Deprecated
    public HttpHost randomProxy() {
        return this.proxyList.pop().getHost();
    }

    private boolean isSuccessful(HttpResponse response) {
        return response.getStatusLine().getStatusCode() / 100 == 2;
    }

    private boolean test(Proxy proxy) throws IOException {
        HttpClient client = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
        HttpGet request = new HttpGet("https://google.com");
        RequestConfig config = RequestConfig.custom()
                .setProxy(proxy.getHost())
                .setConnectTimeout(tolerableProxyTimeout * 1000)
                .setSocketTimeout(tolerableProxyTimeout * 1000)
                .build();
        request.setConfig(config);
        HttpResponse response = client.execute(request);
        boolean successful = isSuccessful(response);
        if (successful)
            proxy.increaseSuccessCount();
        else
            proxy.increaseFailedCount();
        proxyDao.save(proxy);
        return successful;
    }

    public Proxy testAndReturnRandom() {
        Random random = new Random();
        while (true) {
            Proxy proxy = this.proxyList.get(random.nextInt(proxyList.size()));
            log.info("Testing proxy: \'" + proxy.getId() + "\' before use...");
            try {
                if (test(proxy)) return proxy;
            } catch (IOException e) {
                log.info("Proxy: \'" + proxy.getId() + "\' is not functional");
            }
        }
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
}
