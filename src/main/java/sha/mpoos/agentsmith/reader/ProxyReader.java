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
import sha.mpoos.agentsmith.crawler.ProxyManager;
import sha.mpoos.agentsmith.dao.ProxyDao;
import sha.mpoos.agentsmith.entity.Proxy;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

@Service
public class ProxyReader {
    private
    @Value("${proxies.load.on.boot}")
    boolean refreshListOnBootup;
    @Autowired
    private ProxyManager proxyManager;

    @PostConstruct
    public void init() throws IOException {
        if (refreshListOnBootup)
            proxyManager.refreshProxyList();
    }

    public Proxy testAndReturnRandom() {
        return proxyManager.testAndReturnRandom();
    }

    public void updateProxyStatistics(HttpResponse response, Proxy proxy) {
        proxyManager.updateProxyStatistics(response, proxy);
    }

    public Proxy returnRandom(){
        return proxyManager.returnRandom();
    }
}
