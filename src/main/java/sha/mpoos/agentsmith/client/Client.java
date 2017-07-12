package sha.mpoos.agentsmith.client;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import sha.mpoos.agentsmith.crawler.entity.Proxy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Logger;

public class Client {
    private static final Logger log = Logger.getLogger("Client");

    public Client() {

    }

    public void sendGet(String userAgent, URI target) throws Exception {
        HttpClient client = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
        HttpGet request = new HttpGet(target);
        // add request header
        request.addHeader("User-Agent", userAgent);
        HttpResponse response = client.execute(request);
        log.info("Sent 'GET' request to URL : " + target.toString() + ", Response Code : " + response.getStatusLine().getStatusCode());
        //writing response
        /*
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));
        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        log.info(result.toString());
        */
    }

    public HttpResponse sendGet(String userAgent, URI target, Proxy proxy) throws Exception {
        HttpClient client = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
        HttpGet request = new HttpGet(target);
        // setup proxy
        RequestConfig config = RequestConfig.custom().setProxy(proxy.getHost()).build();
        request.setConfig(config);
        // add request header
        request.addHeader("User-Agent", userAgent);
        long before = System.currentTimeMillis();
        HttpResponse response = client.execute(request);
        long rt = System.currentTimeMillis() - before;
        log.info("GET, proxy:" + proxy.getId() + "\', URL: \'" + target.toString() +
                "\', Resp: " + response.getStatusLine().getStatusCode() + ", rt: " + rt);
        return response;
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

    public void sendGet(String userAgent, URI target, String clientIP) throws Exception {
        HttpClient client = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
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
/*
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));
        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        log.info(result.toString());
*/
    }

    public void sendPost(String userAgent, URI target, List<NameValuePair> urlParameters) throws Exception {
        HttpClient client = HttpClients.custom().setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
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

    public static void main(String[] args) throws Exception {
        System.setProperty("sun.net.spi.nameservice.nameservers", "85.214.73.63,85.214.73.63,85.214.73.63,85.214.73.63");
        System.setProperty("socksProxyHost", "127.0.0.1");
        System.setProperty("socksProxyPort", "1234");
        Client client = new Client();
        client.sendGet("Mozilla/5.0 (BeOS; U; BeOS BePC; en-US; rv:1.8.1.6) Gecko/20070731 BonEcho/2.0.0.6",
                new URI("https://free-proxy-list.net"));
    }
}
