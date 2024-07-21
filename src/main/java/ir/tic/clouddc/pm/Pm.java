package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.Location;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private int id;

    @Column
    private boolean active;

    @Column
    private int delay;

    @Column
    private LocalDate dueDate;

    @Column
    private LocalDate finishedDate;

    @Column
    private LocalTime finishedTime;

    @OneToMany(mappedBy = "pm")
    private List<PmDetail> pmDetailList;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "pmInterface_id")
    private PmInterface pmInterface;

    @ManyToOne
    @JoinColumn(name = "location_id")  // Pm locate: Salon, Rack, Room
    private Location location;

    @Transient
    private String persianDueDate;

    @Transient
    private String persianFinishedDate;

    @Transient
    private String persianFinishedDayTime;

    @Transient
    private String activePersonName;

}
