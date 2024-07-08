package ir.tic.clouddc.center;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public final class Hall extends Location {

    @OneToMany(mappedBy = "hall")
    private List<Rack> rackList;

    @ManyToOne
    @JoinColumn(name = "center_id")
    private Center center;

    @ElementCollection
    @CollectionTable(name = "date_temperature_mapping",
            schema = "Center",
            joinColumns = {@JoinColumn(name = "salon_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "date")
    @Column(name = "average_temperature")
    private Map<LocalDate, Float> averageTemperature;


    @Override
    public Center getCenter() {
        return center;
    }

    @Override
    public void setCenter(Center center) {
        this.center = center;
    }

    public List<Rack> getRackList() {
        return rackList;
    }

    public void setRackList(List<Rack> rackList) {
        this.rackList = rackList;
    }

    public Map<LocalDate, Float> getAverageTemperature() {
        return averageTemperature;
    }

    public void setAverageTemperature(Map<LocalDate, Float> averageTemperature) {
        this.averageTemperature = averageTemperature;
    }
}
