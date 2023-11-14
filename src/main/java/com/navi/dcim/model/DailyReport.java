package com.navi.dcim.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
public class DailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private LocalDateTime dateTime;

    @OneToMany(mappedBy = "dailyReport")
    private List<Task> taskList;


    public DailyReport(int id, LocalDateTime dateTime, List<Task> taskList) {
        this.id = id;
        this.dateTime = dateTime;
        this.taskList = taskList;
    }



    public int getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public List<Task> getTaskList() {
        return taskList;
    }
}
