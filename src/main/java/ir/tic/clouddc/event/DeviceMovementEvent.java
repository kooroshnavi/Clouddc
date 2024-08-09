package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Location;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
