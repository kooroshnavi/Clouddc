package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.report.DailyReport;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private boolean active;

    @Column
    private int delay;

    @Column
    private LocalDate dueDate;

    @Column
    private LocalTime finishedTime;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "finished_report_id")
    private DailyReport dailyReport;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pm_id")
    private GeneralPm generalPm;

    @OneToMany(mappedBy = "task")
    private List<GeneralPmDetail> generalPmDetailList;

    @Transient
    private String persianDueDate;

    @Transient
    private String persianFinishedDate;

    @Transient
    private String name;

    @Transient
    private String currentAssignedPerson;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Task(boolean active, int delay, GeneralPm generalPm, LocalDate dueDate) {
        this.active = active;
        this.delay = delay;
        this.generalPm = generalPm;
        this.dueDate = dueDate;
    }

    public String getCurrentAssignedPerson() {
        return currentAssignedPerson;
    }

    public void setCurrentAssignedPerson(String currentAssignedPerson) {
        this.currentAssignedPerson = currentAssignedPerson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalTime getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(LocalTime finishedTime) {
        this.finishedTime = finishedTime;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setPersianDueDate(String persianDueDate) {
        this.persianDueDate = persianDueDate;
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

    public void setGeneralPmDetailList(List<GeneralPmDetail> generalPmDetailList) {
        this.generalPmDetailList = generalPmDetailList;
    }

    public boolean isActive() {
        return active;
    }

    public int getDelay() {
        return delay;
    }

    public Pm getGeneralPm() {
        return generalPm;
    }


    public List<GeneralPmDetail> getGeneralPmDetailList() {
        return generalPmDetailList;
    }

    public String getPersianDueDate() {
        return persianDueDate;
    }

    public String getPersianFinishedDate() {
        return persianFinishedDate;
    }

    public DailyReport getDailyReport() {
        return dailyReport;
    }

    public void setDailyReport(DailyReport dailyReport) {
        this.dailyReport = dailyReport;
    }
}
