package ir.tic.clouddc.pm;

import ir.tic.clouddc.log.Workflow;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class PmDetail extends Workflow {  // Common fields in Pm-related details

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "PmID")
    private Pm pm;

    @Column(name = "Active")
    private boolean active;

    @Column(name = "FinishedDate")
    private LocalDate finishedDate;

    @Column(name = "FinishedTime")
    private LocalTime finishedTime;

    @Column(name = "Delay")
    private int delay;

    @Transient
    private String persianFinishedDate;

    @Transient
    private String persianFinishedDayTime;


    public String getPersianFinishedDayTime() {
        return persianFinishedDayTime;
    }

    public void setPersianFinishedDayTime(String persianFinishedDayTime) {
        this.persianFinishedDayTime = persianFinishedDayTime;
    }

    public LocalDate getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(LocalDate finishedDate) {
        this.finishedDate = finishedDate;
    }

    public LocalTime getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(LocalTime finishedTime) {
        this.finishedTime = finishedTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getPersianFinishedDate() {
        return persianFinishedDate;
    }

    public void setPersianFinishedDate(String persianFinishedDate) {
        this.persianFinishedDate = persianFinishedDate;
    }


    public Pm getPm() {
        return pm;
    }

    public void setPm(Pm pm) {
        this.pm = pm;
    }
}
