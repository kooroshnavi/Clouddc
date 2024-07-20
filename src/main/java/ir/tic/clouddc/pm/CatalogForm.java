package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.center.LocationPmCatalog;
import ir.tic.clouddc.individual.Person;

import java.time.LocalDate;

public class CatalogForm {
    private int locationPmCatalogId;

    private int locationId;

    private String nextDue;

    private int defaultPersonId;

    private boolean enabled;

    private int pmInterfaceId;


    public int getLocationPmCatalogId() {
        return locationPmCatalogId;
    }

    public void setLocationPmCatalogId(int locationPmCatalogId) {
        this.locationPmCatalogId = locationPmCatalogId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getNextDue() {
        return nextDue;
    }

    public void setNextDue(String nextDue) {
        this.nextDue = nextDue;
    }

    public int getDefaultPersonId() {
        return defaultPersonId;
    }

    public void setDefaultPersonId(int defaultPersonId) {
        this.defaultPersonId = defaultPersonId;
    }

    public int getPmInterfaceId() {
        return pmInterfaceId;
    }

    public void setPmInterfaceId(int pmInterfaceId) {
        this.pmInterfaceId = pmInterfaceId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


}
