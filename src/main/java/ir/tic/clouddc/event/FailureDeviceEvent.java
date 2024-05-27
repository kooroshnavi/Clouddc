package ir.tic.clouddc.event;

import ir.tic.clouddc.resource.Device;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
public class FailureDeviceEvent extends Event {

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device failedDevice;

    public Device getFailedDevice() {
        return failedDevice;
    }

    public void setFailedDevice(Device failedDevice) {
        this.failedDevice = failedDevice;
    }
}
