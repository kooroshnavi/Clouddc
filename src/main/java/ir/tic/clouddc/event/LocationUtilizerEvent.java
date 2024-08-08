package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.resource.Utilizer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
@Getter
@Setter
public class LocationUtilizerEvent extends Event {

    @ManyToOne
    @JoinColumn(name = "OldUtilizerID")
    private Utilizer oldUtilizer;

    @ManyToOne
    @JoinColumn(name = "NewUtilizerID")
    private Utilizer newUtilizer;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "LocationID")
    private Location location;
}
