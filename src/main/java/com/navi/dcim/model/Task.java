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
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private boolean status;

    @Column
    private LocalDate dueDate;

    @Column
    private LocalDateTime successDate;

    @Column
    private int delay;

    @Transient
    private String dueDatePersian;

    @Transient
    private String successDatePersian;

    @Transient
    private String namePersian;

    @ManyToOne
    @JoinColumn(name = "task_status_id")
    private TaskStatus taskStatus;

    @OneToOne
    @JoinColumn(name = "center_id")
    private Center center;

    @OneToMany(mappedBy = "task", cascade = {CascadeType.ALL})
    private List<TaskDetail> taskDetailList;


    public List<TaskDetail> getTaskDetailList() {
        return taskDetailList;
    }

    public void setTaskDetailList(TaskDetail taskDetail) {
        this.taskDetailList.add(taskDetail);
    }

    public boolean isStatus() {
        return status;
    }

    public String getNamePersian() {
        return namePersian;
    }

    public void setNamePersian(String namePersian) {
        this.namePersian = namePersian;
    }

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


    @JsonIgnore
    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }
}
