package ir.tic.clouddc.resource;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
public class DevicePort extends Port {

    private static final String TYPE = "Device-->Port";

    @OneToOne
    @JoinColumn(name = "module_id")
    private Transceiver transceiver;

    @Column
    private String type;  // 1G 10G 40G 100G - iLo - Ethernet - Management, ...

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

}