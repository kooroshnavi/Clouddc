package ir.tic.clouddc.pm;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public class Pm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    @Nationalized
    private String name;

    @Column
    @Nationalized
    private String description;

    @Column
    private int period;

    @Column
    private boolean enabled;

    @Column
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private PmType type;

    @OneToMany(mappedBy = "taskStatus", cascade = CascadeType.ALL)
    private List<Task> taskList;

    @Transient
    private String nextDuePersian;

    @Transient
    private String lastSuccessfulPersian;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public PmType getType() {
        return type;
    }

    public void setType(PmType type) {
        this.type = type;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public String getNextDuePersian() {
        return nextDuePersian;
    }

    public void setNextDuePersian(String nextDuePersian) {
        this.nextDuePersian = nextDuePersian;
    }

    public String getLastSuccessfulPersian() {
        return lastSuccessfulPersian;
    }

    public void setLastSuccessfulPersian(String lastSuccessfulPersian) {
        this.lastSuccessfulPersian = lastSuccessfulPersian;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
