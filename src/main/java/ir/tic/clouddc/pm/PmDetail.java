package ir.tic.clouddc.pm;

import ir.tic.clouddc.log.Workflow;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class PmDetail extends Workflow {  // Common fields in Pm-related details

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pm_id")
    private Pm pm;

    @Column
    private boolean active;

    @Column
    private LocalDateTime finishedDateTime;

    @Column
    private int delay;

    @Transient
    private String persianRegisterDate;

    @Transient
    private String persianFinishedDate;

    @Transient
    private String assignedPerson;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getFinishedDateTime() {
        return finishedDateTime;
    }

    public void setFinishedDateTime(LocalDateTime finishedDateTime) {
        this.finishedDateTime = finishedDateTime;
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

    public Pm getPm() {
        return pm;
    }

    public void setPm(Pm pm) {
        this.pm = pm;
    }
}
