package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.ResourceLocation;
import ir.tic.clouddc.etisalat.Link;
import ir.tic.clouddc.etisalat.Route;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name="Device")
public abstract class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

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

    @Column
    private boolean active;

    @OneToMany(mappedBy = "device")
    private List<Port> portList;

    @OneToMany(mappedBy = "device")
    private List<Route> routeList;

    @OneToMany(mappedBy = "device")
    private List<Link> linkList;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private ResourceLocation location;
}
