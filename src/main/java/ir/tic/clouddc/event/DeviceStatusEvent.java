package ir.tic.clouddc.event;

import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.DeviceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(schema = "Event")
@NoArgsConstructor
public final class DeviceStatusEvent extends Event {

    @Column(name = "DualPowerChanged")
    private boolean dualPowerChanged;  // order 0

    @Column(name = "StsChanged")
    private boolean stsChanged; // order 1

    @Column(name = "FanChanged")
    private boolean fanChanged; // order 2

    @Column(name = "ModuleChanged")
    private boolean moduleChanged; // order 3

    @Column(name = "StorageChanged")
    private boolean storageChanged; // order 4

    @Column(name = "PortChanged")
    private boolean portChanged; // order 5

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "DeviceStatusID")
    private DeviceStatus deviceStatus;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "DeviceID")
    private Device device;

}
