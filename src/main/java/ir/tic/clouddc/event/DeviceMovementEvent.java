package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.resource.Device;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public class DeviceMovementEvent extends Event {

    @OneToOne
    @JoinColumn(name = "source_location_id")
    private Location source;

    @OneToOne
    @JoinColumn(name = "destination_location_id")
    private Location destination;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    @Override
    public void registerEvent() {

    }

    @Override
    public void updateEvent() {

    }

    @Override
    public void endEvent() {

    }
}
