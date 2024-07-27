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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column
    private Long id;

    @Column
    private boolean active;

    @Column
    private int delay;

    @Column
    private LocalDate dueDate;

    @Column
    private LocalTime registerTime;

    @Column
    private LocalDate finishedDate;

    @Column
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
