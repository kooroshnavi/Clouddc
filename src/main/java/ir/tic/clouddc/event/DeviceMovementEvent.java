package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.resource.Device;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
@Getter
@Setter
public final class DeviceMovementEvent extends Event {

    @ManyToOne
    @JoinColumn(name = "SourceLocationID")
    private Location source;

    @ManyToOne
    @JoinColumn(name = "DestinationLocationID")
    private Location destination;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "MovementEventDevice", schema = "Event",
            joinColumns = {@JoinColumn(name = "EventID")},
            inverseJoinColumns = {@JoinColumn(name = "DeviceID")})
    private List<Device> deviceList;
}
