package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.Rack;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Entity
@Table(schema = "resource")
@NoArgsConstructor
public final class Utilizer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    @Nationalized
    private String name;

    @Column
    private boolean messenger;

    @OneToMany(mappedBy = "utilizer")
    private List<Rack> rackList;

    @OneToMany(mappedBy = "utilizer")
    private List<Device> deviceList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMessenger() {
        return messenger;
    }

    public void setMessenger(boolean messenger) {
        this.messenger = messenger;
    }

    public List<Rack> getRackList() {
        return rackList;
    }

    public void setRackList(List<Rack> rackList) {
        this.rackList = rackList;
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }
}
