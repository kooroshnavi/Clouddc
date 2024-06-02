package ir.tic.clouddc.pm;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Pm {    // new Task style

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private int id;

    @Column
    private boolean active;

    @Column
    private int delay;

    @Column
    private LocalDate dueDate;

    @Column
    private LocalDate finishedDate;

    @Column
    private LocalTime finishedTime;

    @OneToMany(mappedBy = "pm")
    private List<PmDetail> pmDetailList;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pmInterface_id")
    private PmInterface pmInterface;

    @Transient
    private String persianDueDate;

    @Transient
    private String persianFinishedDate;

    @Transient
    private String persianFinishedDayTime;

    @Transient
    private String activePersonName;


    public String getPersianFinishedDayTime() {
        return persianFinishedDayTime;
    }

    public void setPersianFinishedDayTime(String persianFinishedDayTime) {
        this.persianFinishedDayTime = persianFinishedDayTime;
    }

    public LocalTime getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(LocalTime finishedTime) {
        this.finishedTime = finishedTime;
    }

    public String getPersianDueDate() {
        return persianDueDate;
    }

    public void setPersianDueDate(String persianDueDate) {
        this.persianDueDate = persianDueDate;
    }

    public String getPersianFinishedDate() {
        return persianFinishedDate;
    }

    public void setPersianFinishedDate(String persianFinishedDate) {
        this.persianFinishedDate = persianFinishedDate;
    }

    public String getActivePersonName() {
        return activePersonName;
    }

    public void setActivePersonName(String activePersonName) {
        this.activePersonName = activePersonName;
    }

    public List<PmDetail> getPmDetailList() {
        return pmDetailList;
    }

    public void setPmDetailList(List<PmDetail> pmDetailList) {
        this.pmDetailList = pmDetailList;
    }

    public int getId() {
        return id;
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

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(LocalDate finishedDate) {
        this.finishedDate = finishedDate;
    }

    public PmInterface getPmInterface() {
        return pmInterface;
    }

    public void setPmInterface(PmInterface pmInterface) {
        this.pmInterface = pmInterface;
    }

}
