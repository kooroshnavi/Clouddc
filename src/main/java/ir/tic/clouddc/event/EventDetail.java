package ir.tic.clouddc.event;

import ir.tic.clouddc.log.Workflow;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
@Getter
@Setter
public final class EventDetail extends Workflow {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EventID")
    private Event event;
}
