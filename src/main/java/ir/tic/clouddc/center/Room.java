package ir.tic.clouddc.center;

import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.Utilizer;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public final class Room extends Location {

    @OneToMany(mappedBy = "location")
    private List<Device> deviceList;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "UtilizerID")
    private Utilizer utilizer;


    public Utilizer getUtilizer() {
        return utilizer;
    }

    public void setUtilizer(Utilizer utilizer) {
        this.utilizer = utilizer;
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }
}
