package ir.tic.clouddc.pm;

import ir.tic.clouddc.log.Workflow;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public class TemperaturePmDetail extends Workflow {

    @ManyToOne
    @JoinColumn(name = "pm_id")
    private TemperaturePm temperaturePm;

    @Column
    private float value;    /// Specific for TemperaturePmDetail

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

    public TemperaturePm getTemperaturePm() {
        return temperaturePm;
    }

    public void setTemperaturePm(TemperaturePm temperaturePm) {
        this.temperaturePm = temperaturePm;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(LocalDateTime finishedTime) {
        this.finishedTime = finishedTime;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getPersianRegisterDate() {
        return persianRegisterDate;
    }

    public void setPersianRegisterDate(String persianRegisterDate) {
        this.persianRegisterDate = persianRegisterDate;
    }

    public String getPersianFinishedDate() {
        return persianFinishedDate;
    }

    public void setPersianFinishedDate(String persianFinishedDate) {
        this.persianFinishedDate = persianFinishedDate;
    }

    public String getAssignedPerson() {
        return assignedPerson;
    }

    public void setAssignedPerson(String assignedPerson) {
        this.assignedPerson = assignedPerson;
    }
}
