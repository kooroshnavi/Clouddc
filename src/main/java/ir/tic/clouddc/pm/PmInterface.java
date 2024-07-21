package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.LocationPmCatalog;
import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table(schema = "pm")
@NoArgsConstructor
@Data
public final class PmInterface {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column
    @Nationalized
    private String name;

    @Column
    private int period;

    @Column
    private boolean active;

    @Column
    private boolean enabled;

    @Column
    private boolean generalPm;

    @Column
    private boolean statelessRecurring;

    @Column
    @Nationalized
    private String description;

    @Column
    private String category;

    @OneToMany(mappedBy = "pmInterface")
    private List<Pm> pmList;

    @OneToMany(mappedBy = "pmInterface")
    private List<LocationPmCatalog> locationPmCatalogList;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "persistence_id")
    private Persistence persistence;


    public PmInterface(Integer id) {
        this.id = id;
    }
}
