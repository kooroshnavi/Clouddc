package ir.tic.clouddc.report;

import ir.tic.clouddc.event.Event;
import ir.tic.clouddc.task.Task;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "Report")
@NoArgsConstructor
public class DailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private LocalDate date;

    @Column
    private boolean active;

    @OneToMany(mappedBy = "dailyReport")
    private List<Task> taskList;

    @ManyToMany
    @JoinTable(name = "report_event", schema = "Report", joinColumns = @JoinColumn(name = "report_id"), inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> eventList;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setTaskList(Task task) {
        if (this.taskList == null){
            this.taskList = new ArrayList<>();
        }
        this.taskList.add(task);
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

    public List<Task> getTaskList() {
        return taskList;
    }

    @Override
    public String toString() {
        return "DailyReport{" +
                "id=" + id +
                ", date=" + date +
                ", active=" + active +
                ", taskList=" + taskList +
                ", eventList=" + eventList +
                '}';
    }
}
