package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.LocationPmCatalog;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "pm")
@NoArgsConstructor
public final class GeneralLocationPm extends Pm {
}
