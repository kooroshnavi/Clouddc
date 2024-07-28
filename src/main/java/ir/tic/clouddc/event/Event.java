package ir.tic.clouddc.event;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
public abstract class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "EventID")
    private Long id;

    @Column(name = "Active")
    private boolean active;

    @Column(name = "RegisterDate")
    private LocalDate registerDate;

    @Column(name = "RegisterTime")
    private LocalTime registerTime;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "EventCategoryID")
    private EventCategory eventCategory;

    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "EventDetailID")
    private EventDetail eventDetail;

    @Transient
    private String persianRegisterDate;

    @Transient
    private String persianRegisterDayTime;
}

