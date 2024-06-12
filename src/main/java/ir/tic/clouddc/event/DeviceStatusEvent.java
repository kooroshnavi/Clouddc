package ir.tic.clouddc.event;

import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.DeviceStatus;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
public class DeviceStatusEvent extends Event {

    @Column
    private boolean dualPowerChanged;  // order 0

    @Column
    private boolean stsChanged; // order 1

    @Column
    private boolean fanChanged; // order 2

    @Column
    private boolean moduleChanged; // order 3

    @Column
    private boolean storageChanged; // order 4

    @Column
    private boolean portChanged; // order 5

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "related_status_id")
    private DeviceStatus deviceStatus;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "device_id")
    private Device device;

    public boolean isDualPowerChanged() {
        return dualPowerChanged;
    }

    public void setDualPowerChanged(boolean dualPowerChanged) {
        this.dualPowerChanged = dualPowerChanged;
    }

    public boolean isStsChanged() {
        return stsChanged;
    }

    public void setStsChanged(boolean stsChanged) {
        this.stsChanged = stsChanged;
    }

    public boolean isFanChanged() {
        return fanChanged;
    }

    public void setFanChanged(boolean fanChanged) {
        this.fanChanged = fanChanged;
    }

    public boolean isModuleChanged() {
        return moduleChanged;
    }

    public void setModuleChanged(boolean moduleChanged) {
        this.moduleChanged = moduleChanged;
    }

    public boolean isStorageChanged() {
        return storageChanged;
    }

    public void setStorageChanged(boolean storageChanged) {
        this.storageChanged = storageChanged;
    }

    public boolean isPortChanged() {
        return portChanged;
    }

    public void setPortChanged(boolean portChanged) {
        this.portChanged = portChanged;
    }

    public DeviceStatus getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(DeviceStatus deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }


    public EventDetail registerEvent(DeviceStatusForm deviceStatusForm) {
        var currentStatus = deviceStatusForm.getDevice().getDeviceStatusList().stream().filter(DeviceStatus::isCurrent).findFirst();
        if (currentStatus.isPresent()) {
            this.setDeviceStatus(currentStatus.get());
            this.setDualPowerChanged(currentStatus.get().isDualPower() != deviceStatusForm.isDualPower());
            this.setStsChanged(currentStatus.get().isSts() != deviceStatusForm.isSts());
            this.setFanChanged(currentStatus.get().isFan() != deviceStatusForm.isFan());
            this.setModuleChanged(currentStatus.get().isModule() != deviceStatusForm.isModule());
            this.setStorageChanged(currentStatus.get().isStorage() != deviceStatusForm.isStorage());
            this.setPortChanged(currentStatus.get().isPort() != deviceStatusForm.isPort());
        } else {
            this.setDualPowerChanged(!deviceStatusForm.isDualPower());
            this.setStsChanged(!deviceStatusForm.isSts());
            this.setFanChanged(!deviceStatusForm.isFan());
            this.setModuleChanged(!deviceStatusForm.isModule());
            this.setStorageChanged(!deviceStatusForm.isStorage());
            this.setPortChanged(!deviceStatusForm.isPort());
        }
        this.setDevice(deviceStatusForm.getDevice());
        this.setActive(false);

        EventDetail eventDetail = new EventDetail();
        eventDetail.setEvent(this);
        eventDetail.setDescription(deviceStatusForm.getDescription());
        eventDetail.setRegisterDate(this.getRegisterDate());
        eventDetail.setRegisterTime(this.getRegisterTime());

        return eventDetail;
    }

}
