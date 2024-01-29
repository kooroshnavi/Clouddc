package ir.tic.clouddc.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.tic.clouddc.center.SalonPmDue;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "Task")
@NoArgsConstructor
public class Pm { // Preventative_Maintenance
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private int period;

    @Column
    private boolean active;

    @Column
    private LocalDateTime lastSuccessful;

    @OneToMany(mappedBy = "pm", cascade = CascadeType.ALL)
    private List<Task> taskList;

    @OneToMany(mappedBy = "pm", cascade = CascadeType.ALL)
    private List<SalonPmDue> salonPmDueList;

    @Transient
    private String lastSuccessfulPersian;

    public List<SalonPmDue> getSalonPmDueList() {
        return salonPmDueList;
    }

    public void setSalonPmDueList(SalonPmDue salonPmDueRecord) {
        if (this.salonPmDueList == null) {
            this.salonPmDueList = new ArrayList<>();
        }
        this.salonPmDueList.add(salonPmDueRecord);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void addTask(List<Task> taskList) {
        this.taskList = taskList;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
    public List<Task> getTaskList() {
        return taskList;
    }

    public void addTask(Task task) {
        if (this.taskList == null) {
            this.taskList = new ArrayList<>();
        }
        this.taskList.add(task);
    }
}
