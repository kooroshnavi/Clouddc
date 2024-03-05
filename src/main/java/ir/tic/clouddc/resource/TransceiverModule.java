package ir.tic.clouddc.resource;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
public abstract class TransceiverModule extends Module {

    @Column
    private int bandwidth; // 1, 10, 40, 100  - Gbps

    @Column
    private String range; // LR, SR-BD, ...

    @OneToOne
    @JoinColumn(name = "current_port_id")
    private DevicePort devicePort;
}
