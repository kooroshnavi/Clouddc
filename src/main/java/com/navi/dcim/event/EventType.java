package com.navi.dcim.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.navi.dcim.event.Event;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
public class EventType {

    public EventType() {
        this.eventList = new ArrayList<>();
    }

    public EventType(int id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String name;

    @OneToMany(mappedBy = "eventType")
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
