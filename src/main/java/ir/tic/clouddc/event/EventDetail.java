package ir.tic.clouddc.event;

import ir.tic.clouddc.log.Workflow;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(schema = "Event")
@NoArgsConstructor
public final class EventDetail extends Workflow {

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "EventID")
    private Event event;

    @Transient
    private String persianDate;

    @Transient
    private String persianDayTime;
}
