package ir.tic.clouddc.resource;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "resource")
@NoArgsConstructor
public class DeviceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String type;  // server - switch - firewall

    @Column
    private String vendor; // hpe - cisco

    @Column
    private String model;   //

    @Column
    private String serverFactor;

    @Column
    private String serverFactorSize;

    @OneToMany(mappedBy = "deviceCategory")
    private List<Device> deviceList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String name) {
        this.type = name;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    public String getServerFactor() {
        return serverFactor;
    }

    public void setServerFactor(String serverFactor) {
        this.serverFactor = serverFactor;
    }

    public String getServerFactorSize() {
        return serverFactorSize;
    }

    public void setServerFactorSize(String serverFactorSize) {
        this.serverFactorSize = serverFactorSize;
    }
}
