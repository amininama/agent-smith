package sha.mpoos.agentsmith.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by amin on 7/17/17.
 */
public class AgentSessionAction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "init_date", nullable = false)
    private Date initDate;
    @OneToOne
    private Target target;
    @OneToOne
    private Proxy proxy;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = AgentSession.class)
    private AgentSession session;
    @Column(name = "status_code")
    private int statusCode;
    @Column(name = "response_time")
    private int responseTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getInitDate() {
        return initDate;
    }

    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public AgentSession getSession() {
        return session;
    }

    public void setSession(AgentSession session) {
        this.session = session;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }
}
