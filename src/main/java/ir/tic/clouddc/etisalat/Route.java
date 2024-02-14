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
    @JoinColumn(name = "device_A_id")
    private Device deviceA;

    @ManyToOne
    @JoinColumn(name = "device_B_id")
    private Device deviceB;

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

    public Device getDeviceA() {
        return deviceA;
    }

    public void setDeviceA(Device source) {
        this.deviceA = source;
    }

    public Device getDeviceB() {
        return deviceB;
    }

    public void setDeviceB(Device deviceB) {
        this.deviceB = deviceB;
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

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", deviceA=" + deviceA +
                ", deviceB=" + deviceB +
                ", linkList=" + linkList +
                ", bandwidth=" + bandwidth +
                ", up=" + up +
                '}';
    }
}
