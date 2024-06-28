package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.center.LocationPmCatalog;
import ir.tic.clouddc.utils.UtilService;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public final class GeneralPm extends Pm {

}
