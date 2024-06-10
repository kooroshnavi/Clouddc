package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.Location;
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
    private boolean priorityDevice;    /// DeviceForm

    @OneToMany(mappedBy = "device")
    private List<DeviceStatus> deviceStatusList;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "device_category_id")
    private DeviceCategory deviceCategory;   /// DeviceForm

    @ManyToOne
    @JoinColumn(name = "utilizer_id")
    private Utilizer utilizer;    /// DeviceUtilizerEvent

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;   /// DeviceMovementEvent

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "persistence_id")
    private Persistence persistence;


    public List<DeviceStatus> getDeviceStatusList() {
        return deviceStatusList;
    }

    public void setDeviceStatusList(List<DeviceStatus> deviceStatusList) {
        this.deviceStatusList = deviceStatusList;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Utilizer getUtilizer() {
        return utilizer;
    }

    public void setUtilizer(Utilizer utilizer) {
        this.utilizer = utilizer;
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
}
