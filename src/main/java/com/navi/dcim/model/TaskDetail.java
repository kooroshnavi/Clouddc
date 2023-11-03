package com.navi.dcim.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table
@NoArgsConstructor
public class TaskDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String description;

    @Column
    private LocalDateTime updateDate;

    @Transient
    private String persianDate;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @JsonIgnore
    public int getId() {
        return id;
    }

    public String getPersianDate() {
        return persianDate;
    }

    public void setPersianDate(String persianDate) {
        this.persianDate = persianDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @JsonIgnore
    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
