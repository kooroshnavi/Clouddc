package ir.tic.clouddc.pm;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public final class GeneralDevicePm extends Pm {
}
