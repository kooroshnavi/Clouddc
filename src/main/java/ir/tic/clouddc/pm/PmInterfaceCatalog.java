package ir.tic.clouddc.pm;

import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.person.Person;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor
@Data
public abstract class PmInterfaceCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CatalogGenerator")
    @SequenceGenerator(name = "CatalogGenerator", sequenceName = "CatalogSequence", allocationSize = 1, schema = "Pm")
    @Column(name = "CatalogID")
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "PmInterfaceID")
    private PmInterface pmInterface;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "DefaultPersonID")
    private Person defaultPerson;

    @Column(name = "LastFinishedDate")
    private LocalDate lastFinishedDate;

    @Column(name = "LastFinishedTime")
    private LocalTime lastFinishedTime;

    @Column(name = "LastPmId")
    private Long lastPmId;

    @Column(name = "nextDueDate")
    private LocalDate nextDueDate;

    @Column(name = "Enabled")
    private boolean enabled;

    @Column(name = "Active")
    private boolean active;

    @Column(name = "History")
    private boolean history;

    @OneToMany(mappedBy = "pmInterfaceCatalog")
    private List<Pm> pmList;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PersistenceID")
    private Persistence persistence;

    @Transient
    private String persianLastFinishedDate;

    @Transient
    private String persianLastFinishedDayTime;

    @Transient
    private String persianNextDue;
}
