package ir.tic.clouddc.log;

import ir.tic.clouddc.person.Person;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(schema = "Log", name = "history")
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

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "persistence_id")
    private Persistence persistence;

    @Column
    private char actionCode;

    @Column
    private boolean last;

    @Transient
    private String message;

    public LogHistory(LocalDate date, LocalTime time, Person person, char actionCode, Persistence persistence, boolean last) {
        this.date = date;
        this.time = time;
        this.person = person;
        this.actionCode = actionCode;
        this.persistence = persistence;
        this.last = last;
    }

    public void setActionCode(char actionCode) {
        this.actionCode = actionCode;
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

    public void setMessage(String message) {
        this.message = message;
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

    public char getActionCode() {
        return actionCode;
    }

    public String getMessage() {
        return message;
    }
}
