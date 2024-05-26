package ir.tic.clouddc.pm;

import ir.tic.clouddc.log.WorkFlow;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public class TaskDetail extends WorkFlow {

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @Column
    private boolean active;

    @Column
    private LocalDateTime finishedTime;

    @Column
    private int delay;

    @Transient
    private String persianRegisterDate;

    @Transient
    private String persianFinishedDate;

    @Transient
    private String assignedPerson;

    public String getAssignedPerson() {
        return assignedPerson;
    }

    public void setAssignedPerson(String assignedPerson) {
        this.assignedPerson = assignedPerson;
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

    public String getPersianFinishedDate() {
        return persianFinishedDate;
    }

    public void setPersianFinishedDate(String persianFinishedDate) {
        this.persianFinishedDate = persianFinishedDate;
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

    public Task getTask() {
        return task;
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

