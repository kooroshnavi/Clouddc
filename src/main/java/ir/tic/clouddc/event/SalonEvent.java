package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Salon;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
public class SalonEvent extends Event {

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "salon_id")
    private Salon location;

    public Salon getLocation() {
        return location;
    }

    public void setLocation(Salon location) {
        this.location = location;
    }
}
