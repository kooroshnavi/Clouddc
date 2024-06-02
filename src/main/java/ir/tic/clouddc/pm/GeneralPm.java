package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.Location;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public class GeneralPm extends Pm { /// Text-based Pm. No specific field

    @OneToMany(mappedBy = "generalPm")
    private List<GeneralPmDetail> generalPmDetailList;

    @ManyToOne
    @JoinColumn(name = "location_id")  // Pm locate: Salon, Rack, Room
    private Location location;

}
