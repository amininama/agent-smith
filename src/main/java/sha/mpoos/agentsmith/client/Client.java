package sha.mpoos.agentsmith.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sha.mpoos.agentsmith.config.ClientConfig;
import sha.mpoos.agentsmith.entity.Proxy;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

@Service
public class Client {
    private static final Logger log = Logger.getLogger("Client");

    @Autowired
    private ClientConfig clientConfig;
    private CloseableHttpClient client;

    public Client() {
    }

    @PostConstruct
    public void init(){
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(clientConfig.getMaxTotalPoolConnections());
        cm.setValidateAfterInactivity(1000 * clientConfig.getValidateAfterInactivity());
        cm.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(1000 * clientConfig.getDefaultSocketTimeout()).build());
        this.client = HttpClients.custom()
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setConnectionManager(cm)
                .build();
    }

    public void sendGet(String userAgent, URI target) throws Exception {
        HttpGet request = new HttpGet(target);
        // add request header
        request.addHeader("User-Agent", userAgent);
        HttpResponse response = client.execute(request);
        log.info("Sent 'GET' request to URL : " + target.toString() + ", Response Code : " + response.getStatusLine().getStatusCode());
    }

    public int sendGet(String userAgent, URI target, Proxy proxy, int timeoutSecs) throws Exception {
        return this.sendGet(userAgent, target, proxy, timeoutSecs, false);
    }

    public int sendGet(String userAgent, URI target, Proxy proxy, int timeoutSecs, boolean isTest) throws Exception {
        HttpGet request = new HttpGet(target);
        // setup proxy and timeout
        RequestConfig config = RequestConfig.custom()
                .setProxy(proxy.getHost())
                .setConnectTimeout(timeoutSecs * 1000)
                .setSocketTimeout(timeoutSecs * 1000)
                .setConnectionRequestTimeout(timeoutSecs * 1000)
                .build();
        request.setConfig(config);
        // add request header
        if (StringUtils.isNotBlank(userAgent))
            request.addHeader("User-Agent", userAgent);
        long before = System.currentTimeMillis();
        try (CloseableHttpResponse response = client.execute(request)) {
            long rt = System.currentTimeMillis() - before;
            if(!isTest)
            log.info("GET, proxy:" + proxy.getId() + "\', URL: \'" + target.toString() +
                    "\', Resp: " + response.getStatusLine().getStatusCode() + ", rt: " + rt);
            return response.getStatusLine().getStatusCode();
        }
    }

    public void sendGet(String userAgent, URI target, String clientIP) throws Exception {
        HttpGet request = new HttpGet(target);
        // add request user-agent header
        request.addHeader("User-Agent", userAgent);
        //add client-ip header
        request.addHeader("X-Forwarded-For", clientIP);
        request.addHeader("Client-IP", clientIP);
        request.addHeader("X-Client-IP", clientIP);
        request.addHeader("X-Real-IP", clientIP);

        HttpResponse response = client.execute(request);
        log.info(clientIP + " 'GET' \'" + target.toString() + "\', \'" + userAgent + "\' -> " + response.getStatusLine().getStatusCode());
    }

    public void sendPost(String userAgent, URI target, List<NameValuePair> urlParameters) throws Exception {
        HttpPost post = new HttpPost(target);
        // add header
        post.setHeader("User-Agent", userAgent);
        /*
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("sn", "C02G8416DRJM"));
        urlParameters.add(new BasicNameValuePair("cn", ""));
        urlParameters.add(new BasicNameValuePair("locale", ""));
        urlParameters.add(new BasicNameValuePair("caller", ""));
        urlParameters.add(new BasicNameValuePair("num", "12345"));
        */
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = client.execute(post);
        log.info("POST, target: \'" + target + "\', params: \'" + post.getEntity() + "\', response: " +
                response.getStatusLine().getStatusCode());
        /*
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));
        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        System.out.println(result.toString());
        */
    }
}
