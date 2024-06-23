package ir.tic.clouddc.event;

import ir.tic.clouddc.log.Workflow;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
public class EventDetail extends Workflow {

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "event_id")
    private Event event;

    @Transient
    private String persianDate;

    @Transient
    private String persianDayTime;

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

    public String getPersianDayTime() {
        return persianDayTime;
    }

    public void setPersianDayTime(String persianDayTime) {
        this.persianDayTime = persianDayTime;
    }
}
