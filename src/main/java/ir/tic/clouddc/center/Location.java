package ir.tic.clouddc.center;

import ir.tic.clouddc.event.Event;
import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    private LocationType locationType;

    @OneToMany(mappedBy = "location")
    private List<Event> eventList;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "persistence_id")
    private Persistence persistence;

    @ElementCollection
    @CollectionTable(name = "Pm_Due_mapping",
            joinColumns = {@JoinColumn(name = "location_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "pmInterfaceId_id")
    @Column(name = "due_date")
    private Map<Integer, LocalDate> pmDueMap;    /// <pmInterfaceId, DueDate>

    public Center getCenter() {
        return center;
    }

    public Map<Integer, LocalDate> getPmDueMap() {
        return pmDueMap;
    }

    public void setPmDueMap(Map<Integer, LocalDate> pmDueMap) {
        this.pmDueMap = pmDueMap;
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

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }
}
