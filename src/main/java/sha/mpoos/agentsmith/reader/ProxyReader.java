package sha.mpoos.agentsmith.reader;

import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sha.mpoos.agentsmith.proxy.manager.ProxyManager;
import sha.mpoos.agentsmith.entity.Proxy;

import javax.annotation.PostConstruct;
import java.io.IOException;

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
