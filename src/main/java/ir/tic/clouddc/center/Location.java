package ir.tic.clouddc.center;

import ir.tic.clouddc.event.LocationStatusEvent;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor
@Data
public abstract class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column
    private Long id;

    @Column
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_category_id")
    private LocationCategory locationCategory;  // Hall - Rack - Room

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "center_id")
    private Center center;

    @OneToMany(mappedBy = "location")
    private List<LocationStatusEvent> eventList;

    @OneToMany(mappedBy = "location")
    private List<LocationStatus> locationStatusList;

    @OneToMany(mappedBy = "location")
    private List<LocationPmCatalog> locationPmCatalogList;
}
