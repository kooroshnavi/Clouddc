package ir.tic.clouddc.center;

import ir.tic.clouddc.pm.PmInterfaceCatalog;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public final class LocationPmCatalog extends PmInterfaceCatalog {

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "LocationID")
    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
