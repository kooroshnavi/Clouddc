package ir.tic.clouddc.center;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private long id;

    @Column
    @Nationalized
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_type_id")
    private LocationType locationType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
