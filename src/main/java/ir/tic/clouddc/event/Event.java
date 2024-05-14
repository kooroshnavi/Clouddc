package ir.tic.clouddc.event;


import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.tic.clouddc.center.Salon;
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
    private Salon salon;

    @OneToMany(mappedBy = "event", cascade = {CascadeType.ALL})
    private List<EventDetail> eventDetailList;

    @Transient
    private String persianDate;

    @Transient
    private String persianDay;

    @Transient
    private String time;

    public Event(boolean active, EventType eventType, Salon salon) {
        this.active = active;
        this.eventType = eventType;
        this.salon = salon;
    }

    public String getPersianDay() {
        return persianDay;
    }

    public void setPersianDay(String persianDay) {
        this.persianDay = persianDay;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public void setSalon(Salon salon) {
        this.salon = salon;
    }

    public void setPersianDate(String persianDate) {
        this.persianDate = persianDate;
    }

    public Salon getSalon() {
        return salon;
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
