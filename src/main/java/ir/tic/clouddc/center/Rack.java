package ir.tic.clouddc.center;

import ir.tic.clouddc.resource.Device;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.util.Map;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public class Rack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    @Nationalized
    private String name;

    @ManyToOne
    @JoinColumn(name = "salon_id")
    private Salon salon;

    @ElementCollection
    @CollectionTable(name = "device_unit_map",
            schema = "Center",
            joinColumns = {@JoinColumn(name = "device_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "date")
    @Column(name = "unit")
    private Map<Device, Integer> deviceLocateMap;
}
