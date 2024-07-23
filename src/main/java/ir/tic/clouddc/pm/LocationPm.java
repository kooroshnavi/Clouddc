package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.LocationPmCatalog;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class LocationPm extends Pm {

    @ManyToOne
    @JoinColumn(name = "catalog_id")
    private LocationPmCatalog locationPmCatalog;
}
