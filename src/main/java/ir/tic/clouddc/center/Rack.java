package ir.tic.clouddc.center;

import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.Utilizer;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public final class Rack extends Location {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "HallID")
    private Hall hall;

    @ManyToOne
    @JoinColumn(name = "UtilizerID")
    private Utilizer utilizer;

    @OneToMany(mappedBy = "location")
    private List<Device> deviceList;

    @Column
    private String description;


    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Utilizer getUtilizer() {
        return utilizer;
    }

    public void setUtilizer(Utilizer utilizer) {
        this.utilizer = utilizer;
    }

}
