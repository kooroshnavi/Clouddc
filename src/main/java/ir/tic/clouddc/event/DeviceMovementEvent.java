package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.resource.Device;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(schema = "Event")
@NoArgsConstructor
public final class DeviceMovementEvent extends Event {

    @ManyToOne
    @JoinColumn(name = "SourceLocationID")
    private Location source;

    @ManyToOne
    @JoinColumn(name = "DestinationLocationID")
    private Location destination;

    @Column(name = "MovementDate")
    private LocalDate movementDate;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "MovementEventDevice", schema = "Event",
            joinColumns = {@JoinColumn(name = "EventID")},
            inverseJoinColumns = {@JoinColumn(name = "DeviceID")})
    private List<Device> deviceList;

    @ElementCollection
    @CollectionTable(name = "DeviceMovementBalance", schema = "Event",
            joinColumns = {@JoinColumn(name = "EventID", referencedColumnName = "EventID")})
    @MapKeyColumn(name = "UtilizerID")
    @Column(name = "Balance")
    private Map<Integer, Integer> utilizerBalance;
}
