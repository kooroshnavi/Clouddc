package ir.tic.clouddc.event;

import ir.tic.clouddc.log.Workflow;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
public class EventDetail extends Workflow {

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "event_id")
    private Event event;

    @Transient
    private String persianDate;

    @Transient
    private String persianDay;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getPersianDate() {
        return persianDate;
    }

    public void setPersianDate(String persianDate) {
        this.persianDate = persianDate;
    }

    public String getPersianDay() {
        return persianDay;
    }

    public void setPersianDay(String persianDay) {
        this.persianDay = persianDay;
    }
}
