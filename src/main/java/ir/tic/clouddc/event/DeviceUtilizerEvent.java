package ir.tic.clouddc.event;

import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.Utilizer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
@Getter
@Setter
public final class DeviceUtilizerEvent extends Event {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OldUtilizerID")
    private Utilizer oldUtilizer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NewUtilizerID")
    private Utilizer newUtilizer;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "DeviceID")
    private Device device;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "UtilizerDeviceBalance", schema = "Event",
            joinColumns = {@JoinColumn(name = "EventID", referencedColumnName = "EventID")})
    @MapKeyColumn(name = "UtilizerID")
    @Column(name = "Balance")
    private Map<Integer, Integer> utilizerBalance;

}
