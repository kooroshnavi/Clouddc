package ir.tic.clouddc.center;

import ir.tic.clouddc.event.Event;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor
@Getter
@Setter
public abstract class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LocationGenerator")
    @SequenceGenerator(name = "LocationGenerator", sequenceName = "Location_SEQ", allocationSize = 1, schema = "Center", initialValue = 10000)
    @Column(name = "LocationID")
    private Long id;

    @Column(name = "Name")
    private String name;

    @Column(name = "Assignable")
    private boolean assignable;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "LocationCategoryID")
    private LocationCategory locationCategory;  // Hall - Rack - Room

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "CenterID")
    private Center center;
    
    @OneToMany(mappedBy = "location")
    private List<LocationPmCatalog> locationPmCatalogList;

    @ManyToMany(mappedBy = "locationList")
    private List<Event> eventList;
}
