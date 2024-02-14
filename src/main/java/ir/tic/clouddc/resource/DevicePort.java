package ir.tic.clouddc.resource;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class DevicePort extends Port {

    private static final String TYPE = "Port-Device";

    @Column
    private String address;

    @Column
    private boolean Poe;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;
}
