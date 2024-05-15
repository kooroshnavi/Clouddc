package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.Salon;
import ir.tic.clouddc.report.DailyReport;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private boolean active;

    @Column
    private int delay;

    @ManyToOne
    @JoinColumn(name = "report_id")
    private DailyReport dailyReport;

    @ManyToOne
    @JoinColumn(name = "pm_id")
    private Pm pm;

    @ManyToOne
    @JoinColumn(name = "salon_id")
    private Salon salon;

    @OneToMany(mappedBy = "task", cascade = {CascadeType.ALL})
    private List<TaskDetail> taskDetailList;

    @Transient
    private String persianDueDate;

    @Transient
    private String persianFinishedDate;

    @Transient
    private String persianName;

    public Task(boolean active, int delay, Pm pm, Salon salon, List<TaskDetail> taskDetailList) {
        this.active = active;
        this.delay = delay;
        this.pm = pm;
        this.salon = salon;
        this.taskDetailList = taskDetailList;
    }

    public void setPersianDueDate(String persianDueDate) {
        this.persianDueDate = persianDueDate;
    }

    public void setPersianFinishedDate(String persianFinishedDate) {
        this.persianFinishedDate = persianFinishedDate;
    }

    public void setPersianName(String persianName) {
        this.persianName = persianName;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setTaskDetailList(List<TaskDetail> taskDetailList) {
        this.taskDetailList = taskDetailList;
    }

    public Long getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public int getDelay() {
        return delay;
    }

    public Pm getPm() {
        return pm;
    }

    public Salon getSalon() {
        return salon;
    }

    public List<TaskDetail> getTaskDetailList() {
        return taskDetailList;
    }

    public String getPersianDueDate() {
        return persianDueDate;
    }

    public String getPersianFinishedDate() {
        return persianFinishedDate;
    }

    public String getPersianName() {
        return persianName;
    }

    public DailyReport getDailyReport() {
        return dailyReport;
    }

    public void setDailyReport(DailyReport dailyReport) {
        this.dailyReport = dailyReport;
    }
}
