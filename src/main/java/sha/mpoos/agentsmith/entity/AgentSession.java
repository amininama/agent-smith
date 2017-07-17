package sha.mpoos.agentsmith.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Created by amin on 7/17/17.
 */
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

    public void setSessionActions(Set<AgentSessionAction> sessionActions) {
        this.sessionActions = sessionActions;
    }
}
