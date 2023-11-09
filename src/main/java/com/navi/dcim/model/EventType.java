package com.navi.dcim.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table
@NoArgsConstructor
public class EventType {

    public EventType(int id, String name, List<Event> eventList) {
        this.id = id;
        this.name = name;
        this.eventList = eventList;
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
}
