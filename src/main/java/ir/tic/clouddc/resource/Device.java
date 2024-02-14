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
    private List<DevicePort> portList;

    @OneToMany(mappedBy = "device")
    private List<Route> routeList;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private ResourceLocation location;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getUtilizer() {
        return utilizer;
    }

    public void setUtilizer(String utilizer) {
        this.utilizer = utilizer;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public boolean isDualPower() {
        return dualPower;
    }

    public void setDualPower(boolean dualPower) {
        this.dualPower = dualPower;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Route> getRouteList() {
        return routeList;
    }

    public void setRouteList(List<Route> routeList) {
        this.routeList = routeList;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public void setLocation(ResourceLocation location) {
        this.location = location;
    }

    public List<DevicePort> getPortList() {
        return portList;
    }

    public void setPortList(List<DevicePort> portList) {
        this.portList = portList;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", serialNumber='" + serialNumber + '\'' +
                ", name='" + name + '\'' +
                ", model='" + model + '\'' +
                ", utilizer='" + utilizer + '\'' +
                ", vendor='" + vendor + '\'' +
                ", dualPower=" + dualPower +
                ", active=" + active +
                ", portList=" + portList +
                ", routeList=" + routeList +
                ", location=" + location +
                '}';
    }
}
