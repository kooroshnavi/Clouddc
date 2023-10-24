package com.navi.dcim.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
@NoArgsConstructor
public class TaskStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String name;

    @Column
    private String namePersian;

    @Column
    private int period;

    @Column
    private LocalDateTime lastSuccessful;

    @Column
    private LocalDate nextDue;

    @Transient
    private String nextDuePersian;
    @Transient
    private String lastSuccessfulPersian;

    @OneToMany(mappedBy = "taskStatus")
    private List<Task> tasks;

    public String getNextDuePersian() {
        return nextDuePersian;
    }

    public void setNextDuePersian(String nextDuePersian) {
        this.nextDuePersian = nextDuePersian;
    }

    public String getLastSuccessfulPersian() {
        return lastSuccessfulPersian;
    }

    public void setLastSuccessfulPersian(String lastSuccessfulPersian) {
        this.lastSuccessfulPersian = lastSuccessfulPersian;
    }

    @JsonIgnore
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamePersian() {
        return namePersian;
    }

    public void setNamePersian(String namePersian) {
        this.namePersian = namePersian;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    @JsonIgnore
    public LocalDateTime getLastSuccessful() {
        return lastSuccessful;
    }

    public void setLastSuccessful(LocalDateTime lastSuccessful) {
        this.lastSuccessful = lastSuccessful;
    }

    @JsonIgnore
    public LocalDate getNextDue() {
        return nextDue;
    }

    public void setNextDue(LocalDate nextDue) {
        this.nextDue = nextDue;
    }

    @JsonIgnore
    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
