package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.ResourceLocation;
import ir.tic.clouddc.etisalat.Route;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(schema = "Resource")
public abstract class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id; // persistence identifier for each device

    @Column
    private String serialNumber;

    @Column
    private String name;  // SW-EDGE-P1-01 - utilizer convention

    @Column
    private String model; // DL380-G10 - 2348UPQ

    @Column
    private String utilizer; // Gap

    @Column
    private String vendor; // HPE

    @Column
    private boolean dualPower;

    @Column
    private boolean active;

    @Column
    private boolean ok; // for problematic devices

    @OneToMany(mappedBy = "device")
    private List<DevicePort> devicePortList;

    @OneToMany(mappedBy = "device")
    private List<Route> routeList;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private ResourceLocation location;

}
