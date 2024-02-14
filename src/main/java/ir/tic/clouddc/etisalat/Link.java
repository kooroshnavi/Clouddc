package ir.tic.clouddc.etisalat;

import ir.tic.clouddc.resource.Port;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Etisalat")
@NoArgsConstructor
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String core;

    @Column
    private String connector;

    @Column
    private float length;

    @Column
    private boolean connected;

    @OneToMany
    @JoinColumn(name = "port_A_id")
    private Port portA; // devicePort or patchPanelPort

    @OneToMany
    @JoinColumn(name = "port_B_id")
    private Port portB; // devicePort or patchPanelPort

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCore() {
        return core;
    }

    public void setCore(String core) {
        this.core = core;
    }

    public String getConnector() {
        return connector;
    }

    public void setConnector(String connector) {
        this.connector = connector;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public Port getPortA() {
        return portA;
    }

    public void setPortA(Port portA) {
        this.portA = portA;
    }

    public Port getPortB() {
        return portB;
    }

    public void setPortB(Port portB) {
        this.portB = portB;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return "Link{" +
                "id=" + id +
                ", core='" + core + '\'' +
                ", connector='" + connector + '\'' +
                ", length=" + length +
                ", connected=" + connected +
                ", portA=" + portA +
                ", portB=" + portB +
                ", route=" + route +
                '}';
    }
}



