package com.navi.dcim.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table
@NoArgsConstructor
public class Event {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private LocalDateTime eventDate;

    @Column
    private boolean active;

    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "event_type_id")
    private EventType eventType;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "center_id")
    private Center center;


    @Transient
    private String persianDate;


    public Event(int id, LocalDateTime eventDate, Center center, boolean active, EventType eventType, Person person, String description, String persianDate) {
        this.id = id;
        this.eventDate = eventDate;
        this.center = center;
        this.active = active;
        this.eventType = eventType;
        this.person = person;
        this.description = description;
        this.persianDate = "persianDate";
    }

    public Center getCenter() {
        return center;
    }

    @JsonIgnore
    public int getId() {
        return id;
    }

    public EventType getEventType() {
        return eventType;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    @JsonIgnore
    public boolean isActive() {
        return active;
    }


    public Person getPerson() {
        return person;
    }

    public String getDescription() {
        return description;
    }

    public String getPersianDate() {
        return persianDate;
    }


    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", eventDate=" + eventDate +
                ", active=" + active +
                ", description='" + description + '\'' +
                ", eventType=" + eventType +
                ", person=" + person +
                ", center=" + center +
                ", persianDate='" + persianDate + '\'' +
                '}';
    }
}
