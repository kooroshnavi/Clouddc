package ir.tic.clouddc.pm;

import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.person.Person;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
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
    @JoinColumn(name = "PmInterfaceID", nullable = false)
    private PmInterface pmInterface;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "DefaultPersonID", nullable = false)
    private Person defaultPerson;

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

    @OneToMany(mappedBy = "pmInterfaceCatalog", cascade = CascadeType.ALL)
    private List<Pm> pmList;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PersistenceID")
    private Persistence persistence;

    @Transient
    private String persianNextDue;
}
