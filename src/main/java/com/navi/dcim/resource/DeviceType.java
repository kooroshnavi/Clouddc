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
    private String provider; // hp cisco fortigate huawei

    @Column
    private String model;   // DL,380,G10 - 2960 - 1100,E

    @Column
    private String factor;   // 24,SFF - 48,4

    @Column
    private String partNumber;

    @OneToMany(mappedBy = "deviceType")
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

    public void setType(String type) {
        this.type = type;
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    @Override
    public String toString() {
        return "DeviceType{" +
                "id=" + id +
                ", name='" + type + '\'' +
                ", deviceList=" + deviceList +
                '}';
    }
}
