package ir.tic.clouddc.pm;

import ir.tic.clouddc.report.DailyReport;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

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
    private LocalTime finishedTime;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "finished_report_id")
    private DailyReport dailyReport;

    @ManyToOne
    @JoinColumn(name = "pmInterface_id")
    private PmInterface pmInterface;


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

    public LocalTime getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(LocalTime finishedTime) {
        this.finishedTime = finishedTime;
    }

    public PmInterface getPmInterface() {
        return pmInterface;
    }

    public void setPmInterface(PmInterface pmInterface) {
        this.pmInterface = pmInterface;
    }

    public DailyReport getDailyReport() {
        return dailyReport;
    }

    public void setDailyReport(DailyReport dailyReport) {
        this.dailyReport = dailyReport;
    }
}
