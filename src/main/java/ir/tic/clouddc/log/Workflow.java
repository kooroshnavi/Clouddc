package ir.tic.clouddc.log;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
public abstract class Workflow {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private LocalDate registerDate;  // Register or assign registerDate

    @Column
    private LocalTime registerTime;    // Register or assign registerTime

    @Column
    @Nationalized
    private String description;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "persistence_id")
    private Persistence persistence;

    @Transient
    private String persianRegisterDate;

    @Transient
    private String persianRegisterDayTime;
}
