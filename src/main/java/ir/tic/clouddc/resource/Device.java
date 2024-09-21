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

    @Column(name = "SerialNumber", unique = true, nullable = false)
    private String serialNumber;   /// DeviceForm

    @Column(name = "Priority", nullable = false)
    private boolean priorityDevice;    /// DeviceForm

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "DeviceCategoryID", nullable = false)
    private DeviceCategory deviceCategory;   /// DeviceForm

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "UtilizerID", nullable = false)
    private Utilizer utilizer;    /// DeviceUtilizerEvent

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "LocationID", nullable = false)
    private Location location;   /// DeviceMovementEvent

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "SupplierID", nullable = false)
    private Supplier supplier;

    @OneToMany(mappedBy = "device")
    private List<DevicePmCatalog> devicePmCatalogList;

    @OneToMany(mappedBy = "device", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    private List<ModulePack> modulePackList;

    @ManyToMany(mappedBy = "deviceList")
    private List<Event> deviceEventList;


}
