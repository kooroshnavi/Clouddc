package com.navi.dcim.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@NoArgsConstructor
public class DailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private LocalDateTime dateTime;

    @Column
    private boolean active;

    @OneToMany(mappedBy = "dailyReport")
    private List<Task> taskList;

    @ManyToMany()
    @JoinTable(name = "report_event", joinColumns = @JoinColumn(name = "report_id"), inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> eventList;


    public void setId(int id) {
        this.id = id;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public void setEventList(Event event) {
        if (this.eventList == null) {
            this.eventList = new ArrayList<>();
        }
        this.eventList.add(event);
    }

    public List<Event> getEventList() {
        return eventList;
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

    @Override
    public String toString() {
        return "DailyReport{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", active=" + active +
                ", taskList=" + taskList +
                ", eventList=" + eventList +
                '}';
    }
}
