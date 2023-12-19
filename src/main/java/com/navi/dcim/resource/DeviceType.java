package com.navi.dcim.resource;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table
@NoArgsConstructor
public class DeviceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id; // 1 2 3 4 5

    @Column
    private String type; //1. server 2. enclosure 3. sw  4.fw

    @Column
    private String model;   // DL,380,G10 - 2960 - 1100,E

    @Column
    private String factor;   // 24,SFF - 48,4

    @Column
    private String partNumber;

    @OneToMany(mappedBy = "deviceType")
    private List<Device> deviceList;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider; // hp cisco fortigate huawei



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFactor() {
        return factor;
    }

    public void setFactor(String factor) {
        this.factor = factor;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    @Override
    public String toString() {
        return "DeviceType{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", model='" + model + '\'' +
                ", factor='" + factor + '\'' +
                ", partNumber='" + partNumber + '\'' +
                ", deviceList=" + deviceList +
                ", provider=" + provider +
                '}';
    }
}
