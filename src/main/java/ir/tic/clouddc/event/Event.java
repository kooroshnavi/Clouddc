package ir.tic.clouddc.event;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @Column
    private boolean active;

    @Column
    private LocalDate date;

    @Column
    private LocalTime time;

    @Column
    private String title;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "category_id")
    private EventCategory eventCategory;

    @OneToMany(mappedBy = "event")
    private List<EventDetail> eventDetailList;

    @Transient
    private String persianWeekday;

    @Transient
    private String persianDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public EventCategory getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(EventCategory eventCategory) {
        this.eventCategory = eventCategory;
    }

    public void setEventDetailList(List<EventDetail> eventDetailList) {
        this.eventDetailList = eventDetailList;
    }

    public String getPersianDate() {
        return persianDate;
    }

    public void setPersianDate(String persianDate) {
        this.persianDate = persianDate;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public List<EventDetail> getEventDetailList() {
        return eventDetailList;
    }

    public String getPersianWeekday() {
        return persianWeekday;
    }

    public void setPersianWeekday(String persianWeekday) {
        this.persianWeekday = persianWeekday;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getId() {
        return id;
    }
    public boolean isActive() {
        return active;
    }


}

