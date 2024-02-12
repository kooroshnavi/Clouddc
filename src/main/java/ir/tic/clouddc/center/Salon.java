package ir.tic.clouddc.center;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.tic.clouddc.task.Pm;
import ir.tic.clouddc.task.Task;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private List<Task> taskList;

    @OneToMany(mappedBy = "salon")
    private Map<Pm, LocalDate> pmLocalDateMap;

    @OneToMany(mappedBy = "salon")
    private Map<Character, List<Rack>> rackMap;

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

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public Map<Pm, LocalDate> getPmLocalDateMap() {
        return pmLocalDateMap;
    }

    public void setPmLocalDateMap(Map<Pm, LocalDate> pmLocalDateMap) {
        this.pmLocalDateMap = pmLocalDateMap;
    }

    public Map<Character, List<Rack>> getRackMap() {
        return rackMap;
    }

    public void setRackMap(Map<Character, List<Rack>> rackMap) {
        this.rackMap = rackMap;
    }
}
