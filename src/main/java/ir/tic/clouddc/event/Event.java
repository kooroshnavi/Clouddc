package ir.tic.clouddc.event;


import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.tic.clouddc.center.Center;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "event_type_id")
    private EventType eventType;

    @ManyToOne
    @JoinColumn(name = "center_id")
    private Center center;

    @OneToMany(mappedBy = "event", cascade = {CascadeType.ALL})
    private List<EventDetail> eventDetailList;

    @Transient
    private String persianDate;

    public Event(boolean active, EventType eventType, Center center) {
        this.active = active;
        this.eventType = eventType;
        this.center = center;
    }

    public List<EventDetail> getEventDetailList() {
        return eventDetailList;
    }

    public void setEventDetailList(List<EventDetail> eventDetailList) {
        this.eventDetailList = eventDetailList;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    public void setPersianDate(String persianDate) {
        this.persianDate = persianDate;
    }

    public Center getCenter() {
        return center;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    public EventType getEventType() {
        return eventType;
    }

    @JsonIgnore
    public boolean isActive() {
        return active;
    }

    public String getPersianDate() {
        return persianDate;
    }


    public void setType(EventType eventType) {
        this.eventType = eventType;
    }
}
