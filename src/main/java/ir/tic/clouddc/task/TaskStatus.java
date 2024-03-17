package ir.tic.clouddc.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "Task")
@NoArgsConstructor
public class TaskStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    @Nationalized
    private String name;

    @Column
    private String description;

    @Column
    private int period;

    @Column
    private boolean active;

    @Column
    private LocalDateTime lastSuccessful;

    @Column
    private LocalDate nextDue;

    @OneToMany(mappedBy = "taskStatus", cascade = CascadeType.ALL)
    private List<Task> tasks;

    @Transient
    private String nextDuePersian;

    @Transient
    private String lastSuccessfulPersian;

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

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setTasks(Task task) {
        if (this.tasks == null) {
            this.tasks = new ArrayList<>();
        }
        this.tasks.add(task);
    }
}
