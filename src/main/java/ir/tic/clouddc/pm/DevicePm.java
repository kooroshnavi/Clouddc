package ir.tic.clouddc.pm;

import ir.tic.clouddc.resource.DevicePmCatalog;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class DevicePm extends Pm {

    @ManyToOne
    @JoinColumn(name = "catalog_id")
    private DevicePmCatalog devicePmCatalog;
}
