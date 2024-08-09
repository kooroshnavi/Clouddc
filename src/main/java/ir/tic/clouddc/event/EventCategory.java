package ir.tic.clouddc.event;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
@Getter
@Setter
public final class EventCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EventCategoryID")
    private Integer id;

    @Column(name = "Title")
    private String title;

    @OneToMany(mappedBy = "eventCategory")
    private List<Event> eventList;
}
