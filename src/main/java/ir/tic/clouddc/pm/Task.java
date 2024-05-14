package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.Salon;
import ir.tic.clouddc.report.DailyReport;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    @JoinColumn(name = "pm_id")
    private Pm pm;

    @ManyToOne
    @JoinColumn(name = "salon_id")
    private Salon salon;

    @ManyToOne
    @JoinColumn(name = "due_report_id")
    private DailyReport dailyReport;

    @Column
    private LocalDateTime completed;

    @OneToMany(mappedBy = "task", cascade = {CascadeType.ALL})
    private List<TaskDetail> taskDetailList;

    @Transient
    private String dueDatePersian;

    @Transient
    private String successDatePersian;

    @Transient
    private String namePersian;

    public LocalDateTime getCompleted() {
        return completed;
    }

    public void setCompleted(LocalDateTime completed) {
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Pm getPm() {
        return pm;
    }

    public void setPm(Pm pm) {
        this.pm = pm;
    }

    public Salon getSalon() {
        return salon;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }

    public DailyReport getDailyReport() {
        return dailyReport;
    }

    public void setDailyReport(DailyReport dailyReport) {
        this.dailyReport = dailyReport;
    }

    public List<TaskDetail> getTaskDetailList() {
        return taskDetailList;
    }

    public void setTaskDetailList(List<TaskDetail> taskDetailList) {
        this.taskDetailList = taskDetailList;
    }

    public String getDueDatePersian() {
        return dueDatePersian;
    }

    public void setDueDatePersian(String dueDatePersian) {
        this.dueDatePersian = dueDatePersian;
    }

    public String getSuccessDatePersian() {
        return successDatePersian;
    }

    public void setSuccessDatePersian(String successDatePersian) {
        this.successDatePersian = successDatePersian;
    }

    public String getNamePersian() {
        return namePersian;
    }

    public void setNamePersian(String namePersian) {
        this.namePersian = namePersian;
    }
}
