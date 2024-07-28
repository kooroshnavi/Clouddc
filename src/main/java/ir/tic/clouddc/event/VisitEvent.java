package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Center;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
public final class VisitEvent extends Event {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "CenterID")
    private Center center;

    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }
}
