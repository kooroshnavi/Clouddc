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
    private LocalDateTime updateDate;

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

    @Transient
    private String persianUpdate;


    public Event(LocalDateTime eventDate
            , LocalDateTime updateDate
            , boolean active
            , String description
            , EventType eventType
            , Person person
            , Center center
            ) {
        this.eventDate = eventDate;
        this.updateDate = updateDate;
        this.active = active;
        this.description = description;
        this.eventType = eventType;
        this.person = person;
        this.center = center;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    public void setPersianDate(String persianDate) {
        this.persianDate = persianDate;
    }

    public String getPersianUpdate() {
        return persianUpdate;
    }

    public void setPersianUpdate(String persianUpdate) {
        this.persianUpdate = persianUpdate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
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

    public void setType(EventType eventType) {
        this.eventType = eventType;
    }
}
