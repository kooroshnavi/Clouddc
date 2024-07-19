package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.center.LocationPmCatalog;
import ir.tic.clouddc.individual.Person;

public class CatalogForm {
    private LocationPmCatalog locationPmCatalog;

    private Location location;

    private String nextDue;

    private Person defaultPerson;

    private boolean enabled;

    private PmInterface pmInterface;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocationPmCatalog getLocationPmCatalog() {
        return locationPmCatalog;
    }

    public void setLocationPmCatalog(LocationPmCatalog locationPmCatalog) {
        this.locationPmCatalog = locationPmCatalog;
    }

    public String getNextDue() {
        return nextDue;
    }

    public void setNextDue(String nextDue) {
        this.nextDue = nextDue;
    }

    public Person getDefaultPerson() {
        return defaultPerson;
    }

    public void setDefaultPerson(Person defaultPerson) {
        this.defaultPerson = defaultPerson;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public PmInterface getPmInterface() {
        return pmInterface;
    }

    public void setPmInterface(PmInterface pmInterface) {
        this.pmInterface = pmInterface;
    }
}
