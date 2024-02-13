package ir.tic.clouddc.resource;

import ir.tic.clouddc.etisalat.Link;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name="Port")
public abstract class Port {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id; // unique identifier

    @Column
    private String visiblePortNumber;   // 1-10G or 10 or B7-11

    @Column
    private boolean up;

    @OneToOne
    @JoinColumn(name = "link_id")
    private Link link;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }
}
