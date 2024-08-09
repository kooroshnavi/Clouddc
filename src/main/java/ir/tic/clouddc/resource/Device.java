package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.event.Event;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor
@Getter
@Setter
public abstract class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DeviceGenerator")
    @SequenceGenerator(name = "DeviceGenerator", sequenceName = "Device_SEQ", allocationSize = 1, schema = "Resource", initialValue = 1000)
    @Column(name = "DeviceID")
    private Long id;

    @Column(name = "SerialNumber")
    private String serialNumber;   /// DeviceForm

    @Column(name = "Priority")
    private boolean priorityDevice;    /// DeviceForm

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "DeviceCategoryID")
    private DeviceCategory deviceCategory;   /// DeviceForm

    @ManyToOne
    @JoinColumn(name = "UtilizerID")
    private Utilizer utilizer;    /// DeviceUtilizerEvent

    @ManyToOne
    @JoinColumn(name = "LocationID")
    private Location location;   /// DeviceMovementEvent

    @ManyToOne
    @JoinColumn(name = "SupplierID")
    private Supplier supplier;

    @OneToMany(mappedBy = "device")
    private List<DevicePmCatalog> devicePmCatalogList;

    @ManyToMany(mappedBy = "deviceList")
    private List<Event> deviceEventList;
}
