package ir.tic.clouddc.center;

import ir.tic.clouddc.resource.Utilizer;
import ir.tic.clouddc.resource.Device;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Entity
@Table(schema = "center")
@NoArgsConstructor
public final class Rack extends Location {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @ManyToOne
    @JoinColumn(name = "utilizer_id")
    private Utilizer utilizer;

    @OneToMany(mappedBy = "location")
    private List<Device> deviceList;

    @Column
    private int deviceListSize;

    @Column
    @Nationalized
    private String description;

    @ElementCollection
    @CollectionTable(name = "date_temperature_mapping",
            schema = "Center",
            joinColumns = {@JoinColumn(name = "rack_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "date")
    @Column(name = "average_temperature")
    private Map<LocalDate, Float> averageTemperature;


    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Utilizer getUtilizer() {
        return utilizer;
    }

    public void setUtilizer(Utilizer utilizer) {
        this.utilizer = utilizer;
    }

    public int getDeviceListSize() {
        return deviceListSize;
    }

    public void setDeviceListSize(int deviceListSize) {
        this.deviceListSize = deviceListSize;
    }

}
