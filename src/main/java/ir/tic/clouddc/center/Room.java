package ir.tic.clouddc.center;

import ir.tic.clouddc.resource.Device;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public class Room extends Location {

    @ManyToOne
    @JoinColumn(name = "datacenter_id")
    private Center center;

    @OneToMany(mappedBy = "location")
    private List<Device> deviceList;

    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }
}
