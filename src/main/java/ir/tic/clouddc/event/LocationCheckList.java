package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.center.LocationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
@Getter
@Setter
public final class LocationCheckList extends Event {

    @Column(name = "DoorChanged")
    private boolean doorChanged;  // order 0

    @Column(name = "VentilationChanged")
    private boolean ventilationChanged; // order 1

    @Column(name = "PowerChanged")
    private boolean powerChanged; // order 2

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "LocationStatusID")
    private LocationStatus locationStatus;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "LocationID")
    private Location location;
}
