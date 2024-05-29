package ir.tic.clouddc.center;

import ir.tic.clouddc.person.Utilizer;
import ir.tic.clouddc.resource.Device;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public class Rack extends Location {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "salon_id")
    private Salon salon;

    @ManyToOne
    @JoinColumn(name = "utilizer_id")
    private Utilizer utilizer;

    @OneToMany(mappedBy = "location")
    private List<Device> deviceList;

    @OneToMany(mappedBy = "location")
    private List<Temperature> temperatureList;

    @ElementCollection
    @CollectionTable(name = "date_temperature_mapping",
            schema = "Center",
            joinColumns = {@JoinColumn(name = "rack_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "date")
    @Column(name = "average_temperature")
    private Map<LocalDate, Float> averageTemperature;


    public List<Temperature> getTemperatureList() {
        return temperatureList;
    }

    public void setTemperatureList(List<Temperature> temperatureList) {
        this.temperatureList = temperatureList;
    }

    public Salon getSalon() {
        return salon;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    public Map<LocalDate, Float> getAverageTemperature() {
        return averageTemperature;
    }

    public void setAverageTemperature(Map<LocalDate, Float> averageTemperature) {
        this.averageTemperature = averageTemperature;
    }

    public Utilizer getUtilizer() {
        return utilizer;
    }

    public void setUtilizer(Utilizer utilizer) {
        this.utilizer = utilizer;
    }
}
