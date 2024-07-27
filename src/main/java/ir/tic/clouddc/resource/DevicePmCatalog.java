package ir.tic.clouddc.resource;

import ir.tic.clouddc.pm.PmInterfaceCatalog;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
public final class DevicePmCatalog extends PmInterfaceCatalog {

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "DeviceID")
    private Device device;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
