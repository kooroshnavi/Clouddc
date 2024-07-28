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
    @Column(name = "PmInterfaceID")
    private Integer id;

    @Column(name = "Title")
    private String title;

    @Column(name = "Period")
    private int period;

    @Column(name = "Enabled")
    private boolean enabled;

    @Column(name = "GeneralPm")
    private boolean generalPm;

    @Column(name = "StatelessRecurring")
    private boolean statelessRecurring;

    @Column(name = "Description")
    private String description;

    @Column(name = "Category")
    private String category;

    @Column(name = "CategoryId")
    private int categoryId; // 1. GeneralLocation - 2. GeneralDevice

    @Column(name = "targetId")
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
