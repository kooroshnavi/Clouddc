package ir.tic.clouddc.center;

import ir.tic.clouddc.individual.Person;
import ir.tic.clouddc.pm.Pm;
import ir.tic.clouddc.pm.PmInterface;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(schema = "center")
@NoArgsConstructor
@Data
public final class LocationPmCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne
    @JoinColumn(name = "pmInterface_id")
    private PmInterface pmInterface;

    @ManyToOne
    @JoinColumn(name = "default_workspace_id")
    private Person defaultPerson;

    @OneToOne
    @JoinColumn(name = "last_pm_id")
    private Pm lastFinishedPm;

    @Column
    private LocalDate nextDueDate;

    @Column
    private boolean enabled;

    @Column
    private boolean history;

    @Transient
    private String persianFinishedDate;

    @Transient
    private String persianFinishedDayTime;

    @Transient
    private String persianNextDue;
}
