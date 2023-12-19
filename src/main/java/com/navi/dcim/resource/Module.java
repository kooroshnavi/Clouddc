package com.navi.dcim.resource;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table
@NoArgsConstructor
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;
    @Column
    private boolean active;

    @Column
    private boolean healthy;

    @Column
    private String serialNumber;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device connectedDevice;

    @ManyToOne
    @JoinColumn(name = "module_type_id")
    private ModuleType moduleType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Device getConnectedDevice() {
        return connectedDevice;
    }

    public void setConnectedDevice(Device connectedDevice) {
        this.connectedDevice = connectedDevice;
    }

    public ModuleType getModuleType() {
        return moduleType;
    }

    public void setModuleType(ModuleType moduleType) {
        this.moduleType = moduleType;
    }

    @Override
    public String toString() {
        return "Module{" +
                "id=" + id +
                ", active=" + active +
                ", healthy=" + healthy +
                ", serialNumber='" + serialNumber + '\'' +
                ", description='" + description + '\'' +
                ", connectedDevice=" + connectedDevice +
                ", moduleType=" + moduleType +
                '}';
    }
}
