package ir.tic.clouddc.log;

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
public abstract class Workflow {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WorkflowGenerator")
    @SequenceGenerator(name = "WorkflowGenerator", sequenceName = "Workflow_SEQ", allocationSize = 1, schema = "Log", initialValue = 1000)
    @Column(name = "WorkflowID")
    private Long id;

    @Column(name = "RegisterDate")
    private LocalDate registerDate;  // Register or assign registerDate

    @Column(name = "RegisterTime")
    private LocalTime registerTime;    // Register or assign registerTime

    @Column(name = "Description")
    private String description;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "PersistenceID")
    private Persistence persistence;

    @Transient
    private String persianRegisterDate;

    @Transient
    private String persianRegisterDayTime;
}
