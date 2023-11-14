package com.navi.dcim.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private boolean active;

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

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinColumn(name = "daily_report")
    private DailyReport dailyReport;


    public void setTaskDetailList(List<TaskDetail> taskDetailList) {
        this.taskDetailList = taskDetailList;
    }

    public DailyReport getDailyReport() {
        return dailyReport;
    }

    public void setDailyReport(DailyReport dailyReport) {
        this.dailyReport = dailyReport;
    }

    public List<TaskDetail> getTaskDetailList() {
        return taskDetailList;
    }

    public void setTaskDetailList(TaskDetail taskDetail) {
        this.taskDetailList = new ArrayList<>();
        this.taskDetailList.add(taskDetail);
    }

    @JsonIgnore
    public boolean isActive() {
        return active;
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
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
