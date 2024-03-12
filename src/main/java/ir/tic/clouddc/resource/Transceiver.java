package ir.tic.clouddc.resource;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
public abstract class Transceiver extends Module {

    @OneToOne
    @JoinColumn(name = "current_port_id")
    private DevicePort devicePort;

}
