package ir.tic.clouddc.pm;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "pm")
@NoArgsConstructor
public class GeneralDevicePm extends Pm {
}
