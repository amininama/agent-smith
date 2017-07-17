package sha.mpoos.agentsmith.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * Created by amin on 7/17/17.
 */
@Entity
@Table(name = "target")
public class Target {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "address", nullable = false, unique = false, updatable = true, length = 2000)
    private String address;
    @ManyToOne(cascade = CascadeType.ALL, targetEntity = TargetCollection.class, fetch = FetchType.EAGER)
    @JsonIgnore
    private TargetCollection collection;
    //todo: support POST


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public TargetCollection getCollection() {
        return collection;
    }

    public void setCollection(TargetCollection collection) {
        this.collection = collection;
    }
}
