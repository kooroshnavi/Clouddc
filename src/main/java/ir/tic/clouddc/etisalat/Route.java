package ir.tic.clouddc.etisalat;

import ir.tic.clouddc.resource.Device;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Etisalat")
@NoArgsConstructor
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "source_device_id")
    private Device source;

    @ManyToOne
    @JoinColumn(name = "destination_device_id")
    private Device destination;

    @OneToMany(mappedBy = "route")
    private List<Link> linkList;

    @Column
    private int bandwidth; // Gbps

    @Column
    private boolean up;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Device getSource() {
        return source;
    }

    public void setSource(Device source) {
        this.source = source;
    }

    public Device getDestination() {
        return destination;
    }

    public void setDestination(Device destination) {
        this.destination = destination;
    }

    public List<Link> getLinkList() {
        return linkList;
    }

    public void setLinkList(List<Link> linkList) {
        this.linkList = linkList;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }
}
