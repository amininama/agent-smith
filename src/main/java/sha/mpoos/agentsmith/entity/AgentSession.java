package sha.mpoos.agentsmith.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by amin on 7/17/17.
 */
@Entity
@Table(name = "agent_session")
public class AgentSession {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "init_date")
    private Date initDate;
    @Column(name = "finish_date")
    private Date finishDate;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = TargetCollection.class)
    private TargetCollection targetCollection;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = AgentSessionAction.class, mappedBy = "session")
    private Set<AgentSessionAction> sessionActions;
    @Column(name = "total_req_count", nullable = false)
    private int totalRequestCount;
    @Column(name = "concurrent_req_count", nullable = false)
    private int concurrentRequestCount;
    @Column(name = "sleep_time_millis", nullable = false)
    private int sleepTimeMillis;

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

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public TargetCollection getTargetCollection() {
        return targetCollection;
    }

    public void setTargetCollection(TargetCollection targetCollection) {
        this.targetCollection = targetCollection;
    }

    public Set<AgentSessionAction> getSessionActions() {
        return sessionActions;
    }

    public void addSessionAction(AgentSessionAction agentSessionAction){
        if(this.sessionActions == null) this.sessionActions = new HashSet<>();
        this.sessionActions.add(agentSessionAction);
    }

    public void setSessionActions(Set<AgentSessionAction> sessionActions) {
        this.sessionActions = sessionActions;
    }

    public int getTotalRequestCount() {
        return totalRequestCount;
    }

    public void setTotalRequestCount(int totalRequestCount) {
        this.totalRequestCount = totalRequestCount;
    }

    public int getConcurrentRequestCount() {
        return concurrentRequestCount;
    }

    public void setConcurrentRequestCount(int concurrentRequestCount) {
        this.concurrentRequestCount = concurrentRequestCount;
    }

    public int getSleepTimeMillis() {
        return sleepTimeMillis;
    }

    public void setSleepTimeMillis(int sleepTimeMillis) {
        this.sleepTimeMillis = sleepTimeMillis;
    }
}
