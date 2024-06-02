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

    @OneToMany(mappedBy = "salon")
    private List<Task> taskList;

    @OneToMany(mappedBy = "location")
    private List<Temperature> temperatureList;

    @OneToMany(mappedBy = "salon")
    private List<Rack> rackList;

    @ElementCollection
    @CollectionTable(name = "date_temperature_mapping",
            schema = "Center",
            joinColumns = {@JoinColumn(name = "salon_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "date")
    @Column(name = "average_temperature")
    private Map<LocalDate, Float> averageTemperature;



    public List<Rack> getRackList() {
        return rackList;
    }

    public void setRackList(List<Rack> rackList) {
        this.rackList = rackList;
    }

    public List<Temperature> getTemperatureList() {
        return temperatureList;
    }

    public void setTemperatureList(List<Temperature> temperatureList) {
        this.temperatureList = temperatureList;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public Map<LocalDate, Float> getAverageTemperature() {
        return averageTemperature;
    }

    public void setAverageTemperature(Map<LocalDate, Float> averageTemperature) {
        this.averageTemperature = averageTemperature;
    }
}
