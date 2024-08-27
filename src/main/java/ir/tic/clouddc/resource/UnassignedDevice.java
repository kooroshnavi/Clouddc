package ir.tic.clouddc.resource;

import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
@Getter
@Setter
public final class UnassignedDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UnassignedDeviceGenerator")
    @SequenceGenerator(name = "UnassignedDeviceGenerator", sequenceName = "UnassignedDevice_SEQ", allocationSize = 1, schema = "Resource", initialValue = 1000)
    @Column(name = "UnassignedDeviceID")
    private Integer id;

    @Column(name = "SerialNumber", unique = true, nullable = false)
    private String serialNumber;

    @Column(name = "RemovalDate", nullable = false)
    private LocalDate removalDate;

    @Transient
    private String persianRemovalDate;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "DeviceCategoryID")
    private DeviceCategory deviceCategory;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "PersistenceID")
    private Persistence persistence;
}