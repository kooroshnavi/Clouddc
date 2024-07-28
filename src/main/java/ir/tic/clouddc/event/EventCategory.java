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
    @Column(name = "EventCategoryID")
    private Integer id;

    @Column(name = "Title")
    private String title;

    @Column(name = "Target")
    private String target;  /// Center - Location - Device

    @OneToMany(mappedBy = "eventCategory")
    private List<Event> eventList;
}
