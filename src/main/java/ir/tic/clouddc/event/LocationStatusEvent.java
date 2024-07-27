package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.center.LocationStatus;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
public final class LocationStatusEvent extends Event {

    @Column
    private boolean doorChanged;  // order 0

    @Column
    private boolean ventilationChanged; // order 1

    @Column
    private boolean powerChanged; // order 2

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "StatusID")
    private LocationStatus locationStatus;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "LocationID")
    private Location location;

    public LocationStatus getLocationStatus() {
        return locationStatus;
    }

    public void setLocationStatus(LocationStatus locationStatus) {
        this.locationStatus = locationStatus;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isDoorChanged() {
        return doorChanged;
    }

    public void setDoorChanged(boolean doorChanged) {
        this.doorChanged = doorChanged;
    }

    public boolean isVentilationChanged() {
        return ventilationChanged;
    }

    public void setVentilationChanged(boolean ventilationChanged) {
        this.ventilationChanged = ventilationChanged;
    }

    public boolean isPowerChanged() {
        return powerChanged;
    }

    public void setPowerChanged(boolean powerChanged) {
        this.powerChanged = powerChanged;
    }
}
