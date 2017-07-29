package sha.mpoos.agentsmith.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by amin on 7/17/17.
 */
@Entity
@Table(name = "agent_session_action")
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
    private long responseTime;

    @PrePersist
    private void init(){
        this.initDate = new Date();
    }

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

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    @Override
    public String toString() {
        return "AgentSessionAction{" +
                "id=" + id +
                ", initDate=" + initDate +
                ", target=" + (target == null ? "''" : target.getId()) +
                ", proxy=" + (proxy == null ? "''" : proxy.getId()) +
                ", session=" + (session == null ? "''" : session.getId()) +
                ", statusCode=" + statusCode +
                ", responseTime=" + responseTime +
                '}';
    }
}
