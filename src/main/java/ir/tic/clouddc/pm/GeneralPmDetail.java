package ir.tic.clouddc.pm;

import ir.tic.clouddc.log.Workflow;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public class GeneralPmDetail extends Workflow {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pm_id")
    private GeneralPm generalPm;

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


    public GeneralPm getGeneralPm() {
        return generalPm;
    }

    public void setGeneralPm(GeneralPm generalPm) {
        this.generalPm = generalPm;
    }

    public String getAssignedPerson() {
        return assignedPerson;
    }

    public void setAssignedPerson(String assignedPerson) {
        this.assignedPerson = assignedPerson;
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

