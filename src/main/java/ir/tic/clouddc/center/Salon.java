package ir.tic.clouddc.center;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.tic.clouddc.task.Task;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public class Salon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "center_id")
    private DataCenter dataCenter;

    @OneToMany(mappedBy = "salon")
    private List<SalonPmDue> salonPmDueList;

    @OneToMany(mappedBy = "salon")
    private List<Task> taskList;

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(Task task) {
        if (this.taskList == null) {
            this.taskList = new ArrayList<>();
        }
        this.taskList.add(task);
    }

    public DataCenter getDataCenter() {
        return dataCenter;
    }

    public void setDataCenter(DataCenter dataCenter) {
        this.dataCenter = dataCenter;
    }

    public List<SalonPmDue> getSalonPmDueList() {
        return salonPmDueList;
    }

    public void setSalonPmDueList(List<SalonPmDue> salonPmDueList) {
        this.salonPmDueList = salonPmDueList;
    }

    @JsonIgnore
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
