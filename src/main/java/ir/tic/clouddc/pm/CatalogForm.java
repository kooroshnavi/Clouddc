package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.center.LocationPmCatalog;
import ir.tic.clouddc.individual.Person;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CatalogForm {
    private int locationPmCatalogId;

    private Long locationId;

    private String nextDue;

    private int defaultPersonId;

    private boolean enabled;

    private int pmInterfaceId;
}
