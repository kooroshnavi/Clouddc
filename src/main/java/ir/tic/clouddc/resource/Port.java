package ir.tic.clouddc.resource;

import ir.tic.clouddc.etisalat.Link;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(schema = "Resource")
public abstract class Port {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id; // unique db identifier

    @Column
    private String visiblePortNumber;   // 10G 1 or 7 for devices // B7-41 patch panel port offset 41 inside rack B7

    @Column
    private boolean connected;

    @ManyToOne
    @JoinColumn(name = "current_link_id")
    private Link link;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVisiblePortNumber() {
        return visiblePortNumber;
    }

    public void setVisiblePortNumber(String visiblePortNumber) {
        this.visiblePortNumber = visiblePortNumber;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }
}
