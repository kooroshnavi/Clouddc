package ir.tic.clouddc.resource;

import ir.tic.clouddc.person.Person;
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
public abstract class InventoryUpdateDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "InventoryDetailGenerator")
    @SequenceGenerator(name = "InventoryDetailGenerator", sequenceName = "InventoryUpdate_SEQ", allocationSize = 1, schema = "Resource", initialValue = 1000)
    @Column(name = "InventoryUpdateDetailID")
    private Long id;

    @Column(name = "Changes")
    private int changes;

    @Column(name = "Date", nullable = false)
    private LocalDate date;

    @Column(name = "Time", nullable = false)
    private LocalTime time;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "UpdatedBy", nullable = false)
    private Person person;

    @Column(name = "Description", nullable = false)
    private String description;

    @Transient
    private String persianDateTime;

}
