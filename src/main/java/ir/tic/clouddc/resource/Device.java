package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.event.Event;
import ir.tic.clouddc.event.InstallationEvent;
import ir.tic.clouddc.person.Utilizer;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private long id;

    @Column
    private String serialNumber;

    @Column
    private boolean dualPower;

    @Column
    private boolean failure;

    @Column
    private boolean active;

    @Column
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "device_category_id")
    private DeviceCategory deviceCategory;

    @ManyToOne
    @JoinColumn(name = "utilizer_id")
    private Utilizer utilizer;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(mappedBy = "device")
    private List<Event> eventList;

    @ManyToOne
    @JoinColumn(name = "installation_event_id")
    private InstallationEvent installationEvent;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeviceCategory getDeviceCategory() {
        return deviceCategory;
    }

    public void setDeviceCategory(DeviceCategory deviceCategory) {
        this.deviceCategory = deviceCategory;
    }

    public boolean isFailure() {
        return failure;
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setFailure(boolean failure) {
        this.failure = failure;
    }

    public Utilizer getUtilizer() {
        return utilizer;
    }

    public void setUtilizer(Utilizer utilizer) {
        this.utilizer = utilizer;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    public InstallationEvent getInstallationEvent() {
        return installationEvent;
    }

    public void setInstallationEvent(InstallationEvent installationEvent) {
        this.installationEvent = installationEvent;
    }

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

    public boolean isDualPower() {
        return dualPower;
    }

    public void setDualPower(boolean dualPower) {
        this.dualPower = dualPower;
    }
}
