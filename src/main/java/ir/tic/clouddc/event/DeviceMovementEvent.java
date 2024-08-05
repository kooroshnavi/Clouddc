package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.Utilizer;
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

    @OneToOne
    @JoinColumn(name = "SourceLocationID")
    private Location source;

    @OneToOne
    @JoinColumn(name = "DestinationLocationID")
    private Location destination;

    @Column(name = "MovementDate")
    private LocalDate movementDate;

    @ManyToMany
    private List<Device> deviceList;

    @ElementCollection
    @CollectionTable(name = "DeviceMovementBalance", schema = "Event",
            joinColumns = {@JoinColumn(name = "EventID", referencedColumnName = "id")})
    @MapKeyColumn(name = "UtilizerID")
    @Column(name = "Balance")
    private Map<Integer, Integer> utilizerBalance;
}
