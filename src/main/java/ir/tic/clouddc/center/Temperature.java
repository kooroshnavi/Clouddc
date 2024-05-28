package ir.tic.clouddc.center;

import ir.tic.clouddc.log.Workflow;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public class Temperature extends Workflow {

    @Column
    private float value; // Celsius. its approximate

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

}
