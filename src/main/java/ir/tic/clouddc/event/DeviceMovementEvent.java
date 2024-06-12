package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.resource.Device;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public final class DeviceMovementEvent extends Event {

    @OneToOne
    @JoinColumn(name = "source_location_id")
    private Location source;

    @OneToOne
    @JoinColumn(name = "destination_location_id")
    private Location destination;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "device_id")
    private Device device;

    public Location getSource() {
        return source;
    }

    public void setSource(Location source) {
        this.source = source;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public EventDetail registerEvent(EventRegisterForm eventRegisterForm) {
        this.setDevice(eventRegisterForm.getDevice());
        this.setSource(eventRegisterForm.getDevice().getLocation());
        this.setActive(false);

        EventDetail eventDetail = new EventDetail();
        eventDetail.setEvent(this);
        eventDetail.setDescription(eventRegisterForm.getDescription());
        eventDetail.setRegisterDate(this.getRegisterDate());
        eventDetail.setRegisterTime(this.getRegisterTime());

        return eventDetail;
    }
}
