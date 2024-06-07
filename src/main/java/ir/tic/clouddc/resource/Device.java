package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.event.Event;
import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private int id;

    @Column
    private String serialNumber;   /// DeviceForm

    @Column
    private String name;    /// DeviceForm

    @Column
    private boolean dualPower;   /// DeviceFailureEvent

    @Column
    private boolean powerOn;    /// DeviceFailureEvent

    @Column
    private boolean fanOk;

    @Column
    private boolean connectivityOk;

    @Column
    private boolean greenStat; /// DeviceFailureEvent

    @Column
    private boolean priorityDevice;    /// DeviceForm

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "device_category_id")
    private DeviceCategory deviceCategory;   /// DeviceForm

    @ManyToOne
    @JoinColumn(name = "utilizer_id")
    private Utilizer utilizer;    /// DeviceUtilizerEvent

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;   /// DeviceMovementEvent

    @OneToMany(mappedBy = "device")
    private List<Event> eventList;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "persistence_id")
    private Persistence persistence;


    public boolean isFanOk() {
        return fanOk;
    }

    public void setFanOk(boolean fanOk) {
        this.fanOk = fanOk;
    }

    public boolean isConnectivityOk() {
        return connectivityOk;
    }

    public void setConnectivityOk(boolean connectivityOk) {
        this.connectivityOk = connectivityOk;
    }

    public boolean isPriorityDevice() {
        return priorityDevice;
    }

    public void setPriorityDevice(boolean priorityDevice) {
        this.priorityDevice = priorityDevice;
    }

    public Persistence getPersistence() {
        return persistence;
    }

    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
    }

    public boolean isPowerOn() {
        return powerOn;
    }

    public void setPowerOn(boolean powerOn) {
        this.powerOn = powerOn;
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

    public boolean isGreenStat() {
        return greenStat;
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setGreenStat(boolean greenStat) {
        this.greenStat = greenStat;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
