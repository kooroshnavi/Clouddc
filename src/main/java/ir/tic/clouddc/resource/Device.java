package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.Rack;
import ir.tic.clouddc.etisalat.Link;
import ir.tic.clouddc.etisalat.Route;
import jakarta.persistence.*;

import java.util.List;

@MappedSuperclass
public abstract class Device {
    @Column
    private String serialNumber;

    @Column
    private String name;

    @Column
    private String model;

    @Column
    private String utilizer;

    @Column
    private String vendor;

    @Column
    private boolean dualPower;

    @OneToMany(mappedBy = "device")
    private List<Port> portList;

    private List<Route> routeList;

    private List<Link> linkList;

    @ManyToOne
    @JoinColumn(name = "rack_id")
    private Rack rack;
}
