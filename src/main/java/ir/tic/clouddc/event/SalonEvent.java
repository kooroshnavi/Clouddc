package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Salon;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
public class SalonEvent extends Event{

    private Salon salon;

    public Salon getSalon() {
        return salon;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }
}
