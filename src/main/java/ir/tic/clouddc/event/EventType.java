package ir.tic.clouddc.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
public class EventType {

    static final List<String> TYPES = Arrays.asList("خرابی تجهيز", "مشکل در سالن", "نصب و راه اندازي", "جابجایی", "تعویض", "بازدید");

    public EventType(int id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    @Nationalized
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
        this.eventList = new ArrayList<>();
        this.eventList.add(event);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }
}
