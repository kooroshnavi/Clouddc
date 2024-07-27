package ir.tic.clouddc.log;

import ir.tic.clouddc.person.Person;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(schema = "Log", name = "History")
@NoArgsConstructor
@Data
public final class LogHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private LocalDate date;

    @Column
    private LocalTime time;

    @Column
    private boolean last;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "PersonID")
    private Person person;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "PersistenceID")
    private Persistence persistence;

    @Column
    private String logMessage;

    public LogHistory(LocalDate date, LocalTime time, Person person, Persistence persistence, String logMessage, boolean last) {
        this.date = date;
        this.time = time;
        this.person = person;
        this.persistence = persistence;
        this.last = last;
        this.logMessage = logMessage;
    }
}
