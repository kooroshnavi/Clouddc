package ir.tic.clouddc.person;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.util.Map;

@Entity
@Table(schema = "Person")
@NoArgsConstructor
public class Utilizer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    @Nationalized
    private String name;

    @Column
    private boolean messenger;

    @ElementCollection
    @CollectionTable(name = "Rack_DeliveryDate_mapping",
            joinColumns = {@JoinColumn(name = "utilizer_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "rack_id")
    @Column(name = "delivery_date")  /// DeliveryDate
    private Map<Integer, LocalDate> rackLocalDateMap;

    @ElementCollection
    @CollectionTable(name = "device_DeliveryDate_mapping",
            joinColumns = {@JoinColumn(name = "utilizer_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "device_id")
    @Column(name = "delivery_date")
    private Map<Integer, LocalDate> deviceLocalDateMap;


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

    public boolean isMessenger() {
        return messenger;
    }

    public void setMessenger(boolean messenger) {
        this.messenger = messenger;
    }

    public Map<Integer, LocalDate> getRackLocalDateMap() {
        return rackLocalDateMap;
    }

    public void setRackLocalDateMap(Map<Integer, LocalDate> rackLocalDateMap) {
        this.rackLocalDateMap = rackLocalDateMap;
    }

    public Map<Integer, LocalDate> getDeviceLocalDateMap() {
        return deviceLocalDateMap;
    }

    public void setDeviceLocalDateMap(Map<Integer, LocalDate> deviceLocalDateMap) {
        this.deviceLocalDateMap = deviceLocalDateMap;
    }
}
