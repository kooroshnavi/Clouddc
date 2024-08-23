package ir.tic.clouddc.pm;

import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
@Getter
@Setter
public final class PmInterface {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PmInterfaceID")
    private Integer id;

    @Column(name = "Title", unique = true, nullable = false)
    private String title;

    @Column(name = "Period", nullable = false)
    private int period;

    @Column(name = "Enabled", nullable = false)
    private boolean enabled;

    @Column(name = "GeneralPm", nullable = false)
    private boolean generalPm;

    @Column(name = "StatelessRecurring", nullable = false)
    private boolean statelessRecurring;

    @Column(name = "Description")
    private String description;

    @Column(name = "Category")
    private String category;

    @Column(name = "CategoryId")
    private int categoryId; // 1. GeneralLocation - 2. GeneralDevice

    @Column(name = "TargetId")
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
