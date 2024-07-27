package ir.tic.clouddc.event;

import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.Utilizer;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
public final class DeviceUtilizerEvent extends Event {

    @OneToOne
    @JoinColumn(name = "old_utilize_id")
    private Utilizer oldUtilizer;

    @OneToOne
    @JoinColumn(name = "new_utilize_id")
    private Utilizer newUtilizer;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "device_id")
    private Device device;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Utilizer getOldUtilizer() {
        return oldUtilizer;
    }

    public void setOldUtilizer(Utilizer oldUtilizer) {
        this.oldUtilizer = oldUtilizer;
    }

    public Utilizer getNewUtilizer() {
        return newUtilizer;
    }

    public void setNewUtilizer(Utilizer newUtilizer) {
        this.newUtilizer = newUtilizer;
    }
}
