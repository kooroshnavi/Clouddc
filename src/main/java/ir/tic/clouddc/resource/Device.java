package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.event.DeviceMovementEvent;
import ir.tic.clouddc.event.DeviceStatusEvent;
import ir.tic.clouddc.event.DeviceUtilizerEvent;
import ir.tic.clouddc.event.Event;
import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
public abstract class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "device_generator")
    @SequenceGenerator(name="device_generator", sequenceName="device_sequence")
    private Long id;

    @Column
    private String serialNumber;   /// DeviceForm

    @Column
    private boolean priorityDevice;    /// DeviceForm

    @OneToMany(mappedBy = "device")
    private List<DeviceStatus> deviceStatusList;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "device_category_id")
    private DeviceCategory deviceCategory;   /// DeviceForm

    @ManyToOne
    @JoinColumn(name = "utilizer_id")
    private Utilizer utilizer;    /// DeviceUtilizerEvent

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;   /// DeviceMovementEvent

    @OneToMany(mappedBy = "device")
    private List<DeviceMovementEvent> deviceMovementEventList;

    @OneToMany(mappedBy = "device")
    private List<DeviceUtilizerEvent> deviceUtilizerEventList;

    @OneToMany(mappedBy = "device")
    private List<DeviceStatusEvent> deviceStatusEventList;

}
