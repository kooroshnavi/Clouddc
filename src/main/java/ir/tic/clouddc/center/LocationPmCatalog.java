package ir.tic.clouddc.center;

import ir.tic.clouddc.pm.PmInterfaceCatalog;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
@Getter
@Setter
public final class LocationPmCatalog extends PmInterfaceCatalog {

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "LocationID")
    private Location location;
}
