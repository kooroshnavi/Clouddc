package ir.tic.clouddc.resource;

import ir.tic.clouddc.individual.Person;
import ir.tic.clouddc.pm.Pm;
import ir.tic.clouddc.pm.PmInterface;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(schema = "resource")
@NoArgsConstructor
@Data
public final class DevicePmCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "pmInterface_id")
    private PmInterface pmInterface;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "default_workspace_id")
    private Person defaultPerson;

    @Column
    private LocalDate lastFinishedDate;

    @Column
    private LocalTime lastFinishedTime;

    @Column
    private Long lastPmId;

    @OneToMany
    private List<Pm> pmList;

    @Column
    private LocalDate nextDueDate;

    @Column
    private boolean enabled;

    @Column
    private boolean active;

    @Column
    private boolean history;

    @Transient
    private String persianLastFinishedDate;

    @Transient
    private String persianLastFinishedDayTime;

    @Transient
    private String persianNextDue;
}
