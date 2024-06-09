package ir.tic.clouddc.event;

import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.ResourceService;
import ir.tic.clouddc.utils.UtilService;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
public class FailureDeviceEvent extends Event {

    private ResourceService resourceService;

    private boolean connectivityIssue;

    private boolean notOperable;

    private boolean fanFailure;

    private boolean dualPowerFailure;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "device_id")
    private Device failedDevice;

    public Device getFailedDevice() {
        return failedDevice;
    }
    public void setFailedDevice(Device failedDevice) {
        this.failedDevice = failedDevice;
    }




    @Override
    public void registerEvent(EventRegisterForm eventRegisterForm) {
        Device device = resourceService.validateFormDevice(eventRegisterForm);
        device.setGreenStat(eventRegisterForm.isActive());
        this.setRegisterDate(UtilService.getDATE());
        this.setRegisterTime(UtilService.getTime());
        this.setActive(eventRegisterForm.isActive());
        this.setTitle(eventRegisterForm.getTitle());
        this.setFailedDevice(device);
    }

    @Override
    public void updateEvent(EventRegisterForm eventRegisterForm) {

    }

    @Override
    public void endEvent(EventRegisterForm eventRegisterForm) {

    }
}
