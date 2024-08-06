package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Center;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
@Getter
@Setter
public final class VisitEvent extends Event {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "CenterID")
    private Center center;
}
