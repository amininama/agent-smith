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
public class HideMyNameCrawler extends ProxyCrawler {
    private static final Logger log = Logger.getLogger("HideMyNameCrawler");

    public HideMyNameCrawler() {
        this.sourcePage = "https://hidemy.name/en/proxy-list/";
    }

    public List<String> pageList() throws IOException {
        List<String> pages = new LinkedList<>();
        pages.add(this.sourcePage);
        Document document = Jsoup.connect(this.sourcePage).get();
        Element paginationElement = document.getElementsByClass("proxy__pagination").first();
        Elements pageElements = paginationElement.getElementsByTag("a");
        for (Element a : pageElements) {
            String href = a.attr("href");
            pages.add(this.sourcePage.replace("/en/proxy-list/", href));
        }
        return pages;
    }

    @Override
    public Set<Proxy> fetchProxyInfo() throws IOException {
        Set<Proxy> proxyList = new HashSet<>();
        log.info("Loading proxy-list from \'" + this.sourcePage + "\'");
        List<String> pages = pageList();
        for(String page : pages)
            proxyList.addAll(crawlPage(page));
        log.info("Finished crawling '" + this.sourcePage + "'");
        return proxyList;
    }

    private List<Proxy> crawlPage(String page) throws IOException {
        List<Proxy> proxiesInPage = new LinkedList<>();
        Document document = Jsoup.connect(page).get();
//        Document document = Jsoup.parse(html);
        Element proxyListTable = document.getElementsByClass("proxy__t").first();
        Elements rows = proxyListTable.getElementsByTag("tr");
        for (Element row : rows) {
            try {
                Elements cells = row.getElementsByTag("td");
                String address = cells.get(0).html();
                String port = cells.get(1).html();
                String type = cells.get(4).html();
                if (InetAddresses.isInetAddress(address) && StringUtils.isNumeric(port) && type.contains("HTTP")) {
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
