package com.navi.dcim.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class EventType {

    public EventType() {
        this.eventList = new ArrayList<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String name;

    @OneToMany(mappedBy = "eventType", cascade = CascadeType.ALL)
    private List<Event> eventList;

    @JsonIgnore
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEvent(Event event) {
        eventList.add(event);
    }
}
