package ir.tic.clouddc.pm;

import ir.tic.clouddc.log.Workflow;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor
@Getter
@Setter
public abstract class PmDetail extends Workflow {  // Common fields in Pm-related details

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "PmID")
    private Pm pm;

    @Column(name = "Active")
    private boolean active;

    @Column(name = "FinishedDate")
    private LocalDate finishedDate;

    @Column(name = "FinishedTime")
    private LocalTime finishedTime;

    @Column(name = "Delay")
    private int delay;

    @Transient
    private String persianFinishedDate;

    @Transient
    private String persianFinishedDayTime;
}
