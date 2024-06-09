package ir.tic.clouddc.resource;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
public class DeviceHealthCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private boolean dualPower;

    @Column
    private boolean stsOk;

    @Column
    private boolean fanOk;

    @Column
    private boolean moduleOk;

    @Column
    private boolean storageOk;

    @Column
    private boolean portOk;

    @Column
    private boolean last;

    @Column
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    @Column
    private LocalTime time;

    @Transient
    private String persianCheckDate;

    @Transient
    private String persianCheckDayTime;
}
