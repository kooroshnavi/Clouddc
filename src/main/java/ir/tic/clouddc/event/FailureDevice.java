package ir.tic.clouddc.event;

import ir.tic.clouddc.resource.Device;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
public class FailureDevice extends Event {

    @Transient
    private final String EVENT_TYPE = "خرابی تجهیز";

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device failedDevice;

    public String getEVENT_TYPE() {
        return EVENT_TYPE;
    }

    public Device getFailedDevice() {
        return failedDevice;
    }

    public void setFailedDevice(Device failedDevice) {
        this.failedDevice = failedDevice;
    }
}
