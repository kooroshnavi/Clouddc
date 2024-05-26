package ir.tic.clouddc.event;

import ir.tic.clouddc.log.WorkFlow;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
public class EventDetail extends WorkFlow {

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Transient
    private String persianDate;

    @Transient
    private String persianDay;

    public String getPersianDay() {
        return persianDay;
    }

    public void setPersianDay(String persianDay) {
        this.persianDay = persianDay;
    }

    public String getPersianDate() {
        return persianDate;
    }

    public void setPersianDate(String persianDate) {
        this.persianDate = persianDate;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
