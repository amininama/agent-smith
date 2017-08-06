package sha.mpoos.agentsmith.proxy.crawler;

import com.google.common.net.InetAddresses;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import sha.mpoos.agentsmith.entity.Proxy;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by amin on 7/9/17.
 */

@Component
public class ProxyDBCrawler extends ProxyCrawler {
    private static final Logger log = Logger.getLogger("ProxyDBCrawler");

    public ProxyDBCrawler() {
        this.sourcePage = "http://proxydb.net/?protocol=http&protocol=https&availability=30&offset=0";
    }

    @Override
    public Set<Proxy> fetchProxyInfo() throws IOException {
        Set<Proxy> proxyList = new HashSet<>();
        log.info("Loading proxy-list from \'" + this.sourcePage + "\'");
        List<String> pages = pageList();
        for (int counter = 0; counter < pages.size(); counter++) {
            String page = pages.get(counter);
            log.info("Crawling page: " + (counter + 1) + "/" + pages.size());
            proxyList.addAll(crawlPage(page));
        }
        log.info("Finished crawling '" + this.sourcePage + "'");
        return proxyList;
    }

    public List<String> pageList() throws IOException {
        List<String> pages = new LinkedList<>();
        Document document = Jsoup.connect(this.sourcePage).get();
        String info = document.getElementsByClass("text-muted").get(1).html();
        String[] infoParts = info.split(" ");
        int total = Integer.parseInt(infoParts[1]);
        for (int counter = 0; counter < total / 15; counter++) {
            pages.add(this.sourcePage.replace("offset=0", "offset=".concat(String.valueOf(15 * counter))));
        }
        return pages;
    }

    private List<Proxy> crawlPage(String page) throws IOException {
        List<Proxy> proxiesInPage = new LinkedList<>();
        Document document = Jsoup.connect(page).get();
        Element proxyListTable = document.getElementsByClass("table-sm").first();
        if (proxyListTable == null) {
            return proxiesInPage;
        }
        Elements rows = proxyListTable.getElementsByTag("tr");
        for (Element row : rows) {
            try {
                Elements cells = row.getElementsByTag("td");
                String hostInfo = cells.get(0).getElementsByTag("a").html();
                String[] hostInfoParts = hostInfo.split(":");
                String port = hostInfoParts[1];
                String address = hostInfoParts[0];
                if (InetAddresses.isInetAddress(address) && StringUtils.isNumeric(port)) {
                    Proxy proxy = new Proxy();
                    proxy.setCreationDate(new Date());
                    proxy.setSuccessCount(0);
                    proxy.setFailedCount(0);
                    proxy.setHost(new HttpHost(address, Integer.parseInt(port)));
                    proxy.setSource(sourcePage);
                    proxiesInPage.add(proxy);
                } else
                    log.log(Level.FINE, "skipped a row: \'" + row.html() + "\'");
            } catch (Throwable t) {
                log.log(Level.FINE, "skipped a row: \'" + row.html() + "\'");
            }
        }
        return proxiesInPage;
    }

}
