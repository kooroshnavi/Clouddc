package ir.tic.clouddc.pm;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
public abstract class Pm {    // new Task style

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PmGenerator")
    @SequenceGenerator(name = "PmGenerator", sequenceName = "Pm_SEQ", allocationSize = 1, schema = "Pm", initialValue = 1000)
    @Column(name = "PmID")
    private Long id;

    @Column(name = "Active")
    private boolean active;

    @Column(name = "Delay")
    private int delay;

    @Column(name = "DueDate")
    private LocalDate dueDate;

    @Column(name = "RegisterTime")
    private LocalTime registerTime;

    @Column(name = "FinishedDate")
    private LocalDate finishedDate;

    @Column(name = "FinishedTime")
    private LocalTime finishedTime;

    @OneToMany(mappedBy = "pm")
    private List<PmDetail> pmDetailList;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "CatalogID")
    private PmInterfaceCatalog pmInterfaceCatalog;

    @Transient
    private String persianDueDate;

    @Transient
    private String persianFinishedDate;

    @Transient
    private String persianFinishedDayTime;

    @Transient
    private String activePersonName;

}
