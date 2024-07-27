package ir.tic.clouddc.pm;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "PmInterfaceID")
    private PmInterface pmInterface;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "DefaultWorkspaceID")
    private Person defaultPerson;

    @Column
    private LocalDate lastFinishedDate;

    @Column
    private LocalTime lastFinishedTime;

    @Column
    private Long lastPmId;

    @Column
    private LocalDate nextDueDate;

    @Column
    private boolean enabled;

    @Column
    private boolean active;

    @Column
    private boolean history;

    @OneToMany(mappedBy = "pmInterfaceCatalog")
    private List<Pm> pmList;

    @Transient
    private String persianLastFinishedDate;

    @Transient
    private String persianLastFinishedDayTime;

    @Transient
    private String persianNextDue;
}
