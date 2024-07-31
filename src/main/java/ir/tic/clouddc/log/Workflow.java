package ir.tic.clouddc.log;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
public abstract class Workflow {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WorkflowGenerator")
    @SequenceGenerator(name = "WorkflowGenerator", sequenceName = "Workflow_SEQ", allocationSize = 1, schema = "Log")
    @Column(name = "WorkflowID")
    private Long id;

    @Column(name = "RegisterDate")
    private LocalDate registerDate;  // Register or assign registerDate

    @Column(name = "RegisterTime")
    private LocalTime registerTime;    // Register or assign registerTime

    @Column(name = "Description")
    private String description;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "PersistenceID")
    private Persistence persistence;

    @Transient
    private String persianRegisterDate;

    @Transient
    private String persianRegisterDayTime;
}
