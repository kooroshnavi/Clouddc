package ir.tic.clouddc.pm;

import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public class TaskDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    @Nationalized
    private String description;

    @Column
    private LocalDateTime registerDate;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "persistence_id")
    private Persistence persistence;

    @Column
    private boolean active;

    @Column
    private int delay;

    @Transient
    private String persianRegisterDate;

    public TaskDetail(String description, Task task, Persistence persistence, boolean active, int delay) {
        this.description = description;
        this.task = task;
        this.persistence = persistence;
        this.active = active;
        this.delay = delay;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Task getTask() {
        return task;
    }

    public Persistence getPersistence() {
        return persistence;
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
