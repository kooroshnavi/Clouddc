package ir.tic.clouddc.center;

import ir.tic.clouddc.event.Event;
import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private int id;

    @Column
    @Nationalized
    private String name;

    @ManyToOne
    @JoinColumn(name = "center_id")
    private Center center;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_type_id")
    private LocationCategory locationCategory;  // Salon - Rack - Room

    @OneToMany(mappedBy = "location")
    private List<Event> eventList;

    @OneToMany(mappedBy = "location")
    private List<LocationStatus> locationStatusList;

    @OneToMany(mappedBy = "location")
    private List<LocationPmCatalog> locationPmCatalogList;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "persistence_id")
    private Persistence persistence;


    public List<LocationStatus> getLocationStatusList() {
        return locationStatusList;
    }

    public void setLocationStatusList(List<LocationStatus> locationStatusList) {
        this.locationStatusList = locationStatusList;
    }

    public List<LocationPmCatalog> getLocationPmCatalogList() {
        return locationPmCatalogList;
    }

    public void setLocationPmCatalogList(List<LocationPmCatalog> locationPmCatalogList) {
        this.locationPmCatalogList = locationPmCatalogList;
    }

    public Center getCenter() {
        return center;
    }

    public LocationCategory getLocationCategory() {
        return locationCategory;
    }

    public void setLocationCategory(LocationCategory locationCategory) {
        this.locationCategory = locationCategory;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    public Persistence getPersistence() {
        return persistence;
    }

    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    public long getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocationCategory getLocationType() {
        return locationCategory;
    }

    public void setLocationType(LocationCategory locationCategory) {
        this.locationCategory = locationCategory;
    }
}
