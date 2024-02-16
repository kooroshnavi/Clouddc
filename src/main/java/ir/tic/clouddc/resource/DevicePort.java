package ir.tic.clouddc.resource;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

public abstract class DevicePort extends Port {
    private static final String TYPE = "Port-Device";

    @Column
    private String address;

    @OneToOne
    @JoinColumn(name = "module_id")
    private TransceiverModule transceiverModule;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;
}
