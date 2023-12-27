package com.navi.dcim.resource;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String name;

    @OneToMany(mappedBy = "provider")
    private List<DeviceType> deviceTypeList;

    @OneToMany(mappedBy = "provider")
    private List<ModuleType> moduleTypeList;

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

    public List<DeviceType> getDeviceTypeList() {
        return deviceTypeList;
    }

    public void setDeviceTypeList(List<DeviceType> deviceTypeList) {
        this.deviceTypeList = deviceTypeList;
    }

    public List<ModuleType> getModuleTypeList() {
        return moduleTypeList;
    }

    public void setModuleTypeList(List<ModuleType> moduleTypeList) {
        this.moduleTypeList = moduleTypeList;
    }
    @Override
    public String toString() {
        return "Provider{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", deviceTypeList=" + deviceTypeList +
                ", moduleTypeList=" + moduleTypeList +
                '}';
    }
}
