package ir.tic.clouddc.event;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
@Data
public final class EventCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column
    private String name;

    @Column
    private String target;  /// Center - Location - Device

    @OneToMany(mappedBy = "eventCategory")
    private List<Event> eventList;
}
