package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.Location;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public class GeneralPm extends Pm { /// Text-based Pm. No specific field

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")  // Pm locate: Salon, Rack, Room
    private Location location;

    public GeneralPm(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
