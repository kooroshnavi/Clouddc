package com.navi.dcim.resource;

import com.navi.dcim.center.Location;
import com.navi.dcim.person.Utilizer;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
@NoArgsConstructor
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;   //388

    @Column
    private LocalDate installationDate;  //2023-11-18

    @Column
    private LocalDateTime lastUpdated;   //2024-05-20 10:15

    @Transient
    private String persianInstallationDate;

    @Transient
    private String persianLastUpdated;

    @Column
    private boolean active;

    @Column
    private String serialNumber;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "device_type_id")
    private DeviceType deviceType; //1: server...

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne
    @JoinColumn(name = "utilizer_id")
    private Utilizer utilizer;

    @OneToMany(mappedBy = "connectedDevice")
    private List<Module> moduleList;

    @Column
    private boolean healthy;

    @Column
    private boolean redundantPower;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(LocalDate installationDate) {
        this.installationDate = installationDate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Utilizer getUtilizer() {
        return utilizer;
    }

    public void setUtilizer(Utilizer utilizer) {
        this.utilizer = utilizer;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", dateAdded=" + installationDate +
                ", dateUpdated=" + lastUpdated +
                ", active=" + active +
                ", serialNumber='" + serialNumber + '\'' +
                ", details='" + description + '\'' +
                ", deviceType=" + deviceType +
                ", location=" + location +
                ", utilizer=" + utilizer +
                '}';
    }
}
