package ir.tic.clouddc.resource;

import ir.tic.clouddc.event.Event;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
@Data
public final class DeviceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DeviceStatusID")
    private Long id;

    @Column(name = "DualPowerOK")
    private boolean dualPower;  // order 0

    @Column(name = "StatsOK")
    private boolean sts; // order 1

    @Column(name = "FanOK")
    private boolean fan; // order 2

    @Column(name = "ModuleOK")
    private boolean module; // order 3

    @Column(name = "StorageOK")
    private boolean storage; // order 4

    @Column(name = "PortOK")
    private boolean port; // order 5

    @Column(name = "Active")
    private boolean active;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "DeviceID")
    private Device device;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "EventID")
    private Event event;

    @Transient
    private String persianCheckDate;

    @Transient
    private String persianCheckDayTime;
}
