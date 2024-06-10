package ir.tic.clouddc.event;

import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.DeviceStatus;
import ir.tic.clouddc.resource.ResourceService;
import ir.tic.clouddc.utils.UtilService;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
public class DeviceCheckListEvent extends Event {

    private ResourceService resourceService;

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

    @OneToOne
    @JoinColumn(name = "device_health_id")
    private DeviceStatus deviceStatus;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "device_id")
    private Device device;

    public ResourceService getResourceService() {
        return resourceService;
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

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


    @Override
    public void registerEvent(EventRegisterForm eventRegisterForm) {
        Device device = resourceService.validateFormDevice(eventRegisterForm);
        this.setRegisterDate(UtilService.getDATE());
        this.setRegisterTime(UtilService.getTime());
        this.setActive(false);
        this.setTitle(eventRegisterForm.getTitle());
        this.setDevice(device);

        var currentDeviceHealthStatus = resourceService.getCurrentDeviceHealthStatus();
        if (currentDeviceHealthStatus.isPresent()) {
            this.setDeviceStatus(currentDeviceHealthStatus.get());
            DeviceCheckListForm deviceCheckListForm = new DeviceCheckListForm();

        }
        else {

        }

    }


    @Override
    public void updateEvent(EventRegisterForm eventRegisterForm) {

    }

    @Override
    public void endEvent(EventRegisterForm eventRegisterForm) {

    }
}
