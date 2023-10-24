package com.navi.dcim.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String status;

    @Column
    private String statusPersian;

    @Column
    private LocalDate dueDate;

    @Transient
    private String dueDatePersian;

    @Transient
    private String successDatePersian;

    @Column
    private LocalDateTime successDate;

    @Column
    private int delay;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "task_status_id")
    private TaskStatus taskStatus;

    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @OneToOne
    @JoinColumn(name = "center_id")
    private Center center;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDueDatePersian() {
        return dueDatePersian;
    }

    public void setDueDatePersian(String dueDatePersian) {
        this.dueDatePersian = dueDatePersian;
    }

    public String getSuccessDatePersian() {
        return successDatePersian;
    }

    public void setSuccessDatePersian(String successDatePersian) {
        this.successDatePersian = successDatePersian;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonIgnore
    public String getStatusPersian() {
        return statusPersian;
    }

    public void setStatusPersian(String statusPersian) {
        this.statusPersian = statusPersian;
    }


    @JsonIgnore
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @JsonIgnore
    public LocalDateTime getSuccessDate() {
        return successDate;
    }

    public void setSuccessDate(LocalDateTime successDate) {
        this.successDate = successDate;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }


    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }
}
