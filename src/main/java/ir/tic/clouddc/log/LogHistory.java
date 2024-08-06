package ir.tic.clouddc.log;

import ir.tic.clouddc.person.Person;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(schema = "Log", name = "History")
@NoArgsConstructor
@Getter
@Setter
public final class LogHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LogHistoryID")
    private Long id;

    @Column(name = "Date")
    private LocalDate date;

    @Column(name = "Time")
    private LocalTime time;

    @Column(name = "Last")
    private boolean last;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "PersonID")
    private Person person;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "PersistenceID")
    private Persistence persistence;

    @Column(name = "LogMessage")
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
