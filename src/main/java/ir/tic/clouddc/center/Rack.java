package ir.tic.clouddc.center;

import ir.tic.clouddc.person.Utilizer;
import ir.tic.clouddc.resource.Device;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public class Rack extends Location {


    @ManyToOne
    @JoinColumn(name = "salon_id")
    private Salon salon;

    @ElementCollection
    @CollectionTable(name = "device_unit_map",
            schema = "Center",
            joinColumns = {@JoinColumn(name = "rack_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "device_id")
    @Column(name = "unit_offset")
    private Map<Integer, Device> deviceLocateMap;

    @ElementCollection
    @CollectionTable(name = "date_temperature_mapping",
            schema = "Center",
            joinColumns = {@JoinColumn(name = "rack_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "date")
    @Column(name = "average_temperature")
    private Map<LocalDate, Float> averageTemperature;

    @ManyToOne
    @JoinColumn(name = "utilizer_id")
    private Utilizer utilizer;


}
