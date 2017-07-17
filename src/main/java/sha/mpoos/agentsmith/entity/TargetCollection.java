package sha.mpoos.agentsmith.entity;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by amin on 7/17/17.
 */
@Entity
@Table(name = "target_collection")
public class TargetCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;
    @Column(name = "name", unique = true, nullable = false, updatable = false)
    private String name;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = Target.class, mappedBy = "collection")
    private Set<Target> targets;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Target> getTargets() {
        return targets;
    }

    public void setTargets(Set<Target> targets) {
        this.targets = targets;
    }
}
