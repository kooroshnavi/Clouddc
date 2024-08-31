package ir.tic.clouddc.center;

import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.Utilizer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
@Getter
@Setter
public final class Rack extends Location {

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "HallID")
    private Hall hall;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "UtilizerID")
    private Utilizer utilizer;

    @Column(name = "Description")
    private String description;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "RackPositionDeviceMap", schema = "Center",
            joinColumns = {@JoinColumn(name = "RackID", referencedColumnName = "LocationID")},
            inverseJoinColumns = {@JoinColumn(name = "DeviceID", referencedColumnName = "DeviceID")})
    @MapKeyColumn(name = "Position")
    private Map<Integer, Device> devicePositionMap;

    @OneToMany(mappedBy = "location", fetch = FetchType.LAZY)
    private List<Device> deviceList;
}
