package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Rack;
import ir.tic.clouddc.resource.Device;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "installation", schema = "Event")
public class Installation extends Event {

    @Transient
    private final String EVENT_TYPE = "نصب";

    @OneToMany(mappedBy = "installation")
    private List<Device> deviceList;

    @ManyToOne
    @JoinColumn(name = "rack_id")
    private Rack rack;


    public String getEVENT_TYPE() {
        return EVENT_TYPE;
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    public Rack getRack() {
        return rack;
    }

    public void setRack(Rack rack) {
        this.rack = rack;
    }
}
