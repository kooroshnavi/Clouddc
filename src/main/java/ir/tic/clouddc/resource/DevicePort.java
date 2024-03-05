package ir.tic.clouddc.resource;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

public abstract class DevicePort extends Port {
    private static final String TYPE = "Device-->Port";

    @OneToOne
    @JoinColumn(name = "module_id")
    private TransceiverModule transceiverModule;

    @Column
    private String type;  // 1G 10G 40G 100G - iLo - Ethernet - Management, ...

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;
}
