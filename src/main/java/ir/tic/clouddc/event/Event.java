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
    private char category;

    @OneToMany(mappedBy = "event")
    private List<EventDetail> eventDetailList;

    @Transient
    private String persianDayTime;

    @Transient
    private String persianDate;

    private String categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public char getCategory() {
        return category;
    }

    public void setCategory(char category) {
        this.category = category;
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

    public String getPersianDayTime() {
        return persianDayTime;
    }

    public void setPersianDayTime(String persianDayTime) {
        this.persianDayTime = persianDayTime;
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

