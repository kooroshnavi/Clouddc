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
    @Column
    private Long id;

    @Column
    private boolean dualPower;  // order 0

    @Column
    private boolean sts; // order 1

    @Column
    private boolean fan; // order 2

    @Column
    private boolean module; // order 3

    @Column
    private boolean storage; // order 4

    @Column
    private boolean port; // order 5

    @Column
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
