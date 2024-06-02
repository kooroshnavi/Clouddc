package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.Location;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public class TemperaturePm extends Pm {

    @Column
    private float averageDailyValue;

    @OneToMany(mappedBy = "temperaturePm")
    private List<TemperaturePmDetail> temperaturePmDetailList;

    @ManyToOne
    @JoinColumn(name = "location_id")  // Pm locate: Salon, Rack, Room
    private Location location;


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public float getAverageDailyValue() {
        return averageDailyValue;
    }

    public void setAverageDailyValue(float averageDailyValue) {
        this.averageDailyValue = averageDailyValue;
    }

    public List<TemperaturePmDetail> getTemperaturePmDetailList() {
        return temperaturePmDetailList;
    }

    public void setTemperaturePmDetailList(List<TemperaturePmDetail> temperaturePmDetailList) {
        this.temperaturePmDetailList = temperaturePmDetailList;
    }

}
