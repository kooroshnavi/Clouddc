package ir.tic.clouddc.pm;

import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
@Data
public final class PmInterface {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column
    private String name;

    @Column
    private int period;

    @Column
    private boolean enabled;

    @Column
    private boolean generalPm;

    @Column
    private boolean statelessRecurring;

    @Column
    private String description;

    @Column
    private String category;

    @Column
    private int categoryId; // 1. GeneralLocation - 2. GeneralDevice

    @Column
    private int target;  //  Hall - Rack - Room - Location - server - sw - fw - enc - device

    @OneToMany(mappedBy = "pmInterface")
    private List<PmInterfaceCatalog> pmInterfaceCatalogList;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PersistenceID")
    private Persistence persistence;

    public PmInterface(Integer id) {
        this.id = id;
    }
}
