package sha.mpoos.agentsmith.entity;

import org.apache.http.HttpHost;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by amin on 6/30/17.
 */
@Entity
@Table(name = "proxy")
public class Proxy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "host", unique = true, updatable = false, nullable = false)
    private String host;
    @Column(name = "source")
    private String source;
    @Column(name = "creation_date")
    private Date creationDate;
    @Column(name = "last_successful_use")
    private Date lastSuccessfulUse;
    @Column(name = "success_count")
    private int successCount;
    @Column(name = "failed_count")
    private int failedCount;
    @Column(name = "last_fetch_date")
    private Date lastFetchDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public HttpHost getHost() {
        String[] hostParts = host.split(":");
        return new HttpHost(hostParts[0], Integer.parseInt(hostParts[1]));
    }

    public void setHost(HttpHost host) {
        this.host = host.toHostString();
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastSuccessfulUse() {
        return lastSuccessfulUse;
    }

    public void setLastSuccessfulUse(Date lastSuccessfulUse) {
        this.lastSuccessfulUse = lastSuccessfulUse;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public void increaseSuccessCount() {
        this.successCount++;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public void increaseFailedCount() {
        this.failedCount++;
    }

    public void updateStatsByTestResult(boolean testIsSuccessful){
        if(testIsSuccessful){
            this.increaseSuccessCount();
            this.setLastSuccessfulUse(new Date());
        } else {
            this.increaseFailedCount();
        }
    }

    public Date getLastFetchDate() {
        return lastFetchDate;
    }

    public void setLastFetchDate(Date lastFetchDate) {
        this.lastFetchDate = lastFetchDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Proxy)) return false;

        Proxy proxy = (Proxy) o;

        if (!host.equals(proxy.host)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return host.hashCode();
    }
}
