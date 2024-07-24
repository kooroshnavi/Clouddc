package ir.tic.clouddc.pm;

import ir.tic.clouddc.resource.DevicePmCatalog;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class GeneralDevicePm extends Pm {
}
