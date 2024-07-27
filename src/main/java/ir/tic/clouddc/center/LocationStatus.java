package ir.tic.clouddc.center;

import ir.tic.clouddc.event.Event;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
@Data
public final class LocationStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column
    private boolean door;

    @Column
    private boolean ventilation;

    @Column
    private boolean power;

    @Column
    private boolean active;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "LocationID")
    private Location location;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EventID")
    private Event event;

    @Transient
    private String persianStatusDate;

    @Transient
    private String persianStatusDayTime;

}
