package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.resource.Device;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "installation", schema = "Event")
public class InstallationEvent extends Event {

    @Transient
    private final String EVENT_TYPE = "نصب";

    @OneToMany(mappedBy = "installation")
    private List<Device> deviceList;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;


    public String getEVENT_TYPE() {
        return EVENT_TYPE;
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public void registerEvent() {

    }

    @Override
    public void updateEvent() {

    }

    @Override
    public void endEvent() {

    }
}
