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
    private char messageId;

    @Column
    private boolean active;

    @Transient
    private String message;

    public LogHistory(LocalDate date, LocalTime time, Person person, char messageId, Persistence persistence, boolean active) {
        this.date = date;
        this.time = time;
        this.person = person;
        this.messageId = messageId;
        this.persistence = persistence;
        this.active = active;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public char getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }
}
