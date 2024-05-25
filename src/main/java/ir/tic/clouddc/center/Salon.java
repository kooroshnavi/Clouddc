package ir.tic.clouddc.center;

import ir.tic.clouddc.pm.Task;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public class Salon extends Location {

    @ManyToOne
    @JoinColumn(name = "datacenter_id")
    private DataCenter dataCenter;

    @OneToMany(mappedBy = "salon")
    private List<Task> taskList;

    @ElementCollection
    @CollectionTable(name = "Pm_Due_mapping",
            joinColumns = {@JoinColumn(name = "salon_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "Pm_id")
    @Column(name = "due_date")
    private Map<Integer, LocalDate> pmDueMap;


    @ElementCollection
    @CollectionTable(name = "date_temperature_mapping",
            schema = "Center",
            joinColumns = {@JoinColumn(name = "salon_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "date")
    @Column(name = "average_temperature")
    private Map<LocalDate, Float> averageTemperature;


    public DataCenter getDataCenter() {
        return dataCenter;
    }

    public void setDataCenter(DataCenter dataCenter) {
        this.dataCenter = dataCenter;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public Map<Integer, LocalDate> getPmDueMap() {
        return pmDueMap;
    }

    public void setPmDueMap(Map<Integer, LocalDate> pmDueMap) {
        this.pmDueMap = pmDueMap;
    }

    public Map<LocalDate, Float> getAverageTemperature() {
        return averageTemperature;
    }

    public void setAverageTemperature(Map<LocalDate, Float> averageTemperature) {
        this.averageTemperature = averageTemperature;
    }
}
