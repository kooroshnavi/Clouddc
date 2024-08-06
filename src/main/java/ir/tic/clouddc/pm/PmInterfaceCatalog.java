package ir.tic.clouddc.pm;

import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.person.Person;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor
@Getter
@Setter
public abstract class PmInterfaceCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CatalogGenerator")
    @SequenceGenerator(name = "CatalogGenerator", sequenceName = "Catalog_SEQ", allocationSize = 1, schema = "Pm", initialValue = 1000)
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

    @Column(name = "NextDueDate")
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
