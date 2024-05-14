package ir.tic.clouddc.center;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.tic.clouddc.pm.Pm;
import ir.tic.clouddc.pm.Task;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
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
    @Nationalized
    private String name;

    @ManyToOne
    @JoinColumn(name = "datacenter_id")
    private DataCenter dataCenter;

    @OneToMany(mappedBy = "salon_id")
    private List<Task> taskList;

    private Map<Pm, LocalDate> pmDueMap;

    @ElementCollection
    @CollectionTable(name = "date_temperature_mapping",
            schema = "Center",
            joinColumns = {@JoinColumn(name = "salon_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "date")
    @Column(name = "average_temperature")
    private Map<LocalDate, Float> averageTemperature;

    @JsonIgnore
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

    public Map<LocalDate, Float> getAverageTemperature() {
        return averageTemperature;
    }

    public void setAverageTemperature(Map<LocalDate, Float> averageTemperature) {

        this.averageTemperature = averageTemperature;
    }
}
