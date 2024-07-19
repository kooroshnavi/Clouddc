package ir.tic.clouddc.log;

import ir.tic.clouddc.individual.Person;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(schema = "log", name = "history")
@NoArgsConstructor
public class LogHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private LocalDate date;

    @Column
    private LocalTime time;

    @Column
    private boolean last;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "persistence_id")
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

    public void setId(long id) {
        this.id = id;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }


    public long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public Person getPerson() {
        return person;
    }

    public Persistence getPersistence() {
        return persistence;
    }

}
