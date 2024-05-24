package ir.tic.clouddc.pm;

import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public class TaskDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    @Nationalized
    private String description;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "persistence_id")
    private Persistence persistence;

    @Column
    private boolean active;

    @Column
    private LocalDateTime assignedTime;

    @Column
    private LocalDateTime finishedTime;

    @Column
    private int delay;

    @Transient
    private String persianRegisterDate;

    @Transient
    private String persianFinishedDate;


    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public TaskDetail(Task task) {
        this.task = task;
    }

    public LocalDateTime getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(LocalDateTime finishedTime) {
        this.finishedTime = finishedTime;
    }

    public LocalDateTime getAssignedTime() {
        return assignedTime;
    }

    public void setAssignedTime(LocalDateTime assignedTime) {
        this.assignedTime = assignedTime;
    }

    public String getPersianFinishedDate() {
        return persianFinishedDate;
    }

    public void setPersianFinishedDate(String persianFinishedDate) {
        this.persianFinishedDate = persianFinishedDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setPersianRegisterDate(String persianRegisterDate) {
        this.persianRegisterDate = persianRegisterDate;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Task getTask() {
        return task;
    }

    public Persistence getPersistence() {
        return persistence;
    }

    public boolean isActive() {
        return active;
    }

    public int getDelay() {
        return delay;
    }

    public String getPersianRegisterDate() {
        return persianRegisterDate;
    }
}
