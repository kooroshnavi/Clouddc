package com.navi.dcim.event;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.navi.dcim.center.Center;
import com.navi.dcim.person.Person;
import com.navi.dcim.report.DailyReport;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "Event")
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

    @ManyToOne
    @JoinColumn(name = "event_type_id")
    private EventType eventType;

    @ManyToOne
    @JoinColumn(name = "center_id")
    private Center center;

    @OneToMany(mappedBy = "event", cascade = {CascadeType.ALL})
    private List<EventDetail> eventDetailList;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinTable(name = "report_event", joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "report_id"))
    private List<DailyReport> dailyReportList;

    @Transient
    private String persianDate;


    public Event(LocalDateTime eventDate
            , boolean active
            , EventType eventType
            , Center center) {
        this.eventDate = eventDate;
        this.active = active;
        this.eventType = eventType;
        this.center = center;
    }


    public List<EventDetail> getEventDetailList() {
        return eventDetailList;
    }

    public void setEventDetailList(EventDetail eventDetail) {
        this.eventDetailList = new ArrayList<>();
        this.eventDetailList.add(eventDetail);
    }

    public void setDailyReportList(List<DailyReport> dailyReportList) {
        this.dailyReportList = dailyReportList;
    }

    public List<DailyReport> getDailyReportList() {
        return dailyReportList;
    }

    public void setDailyReportList(DailyReport report) {
        if (this.dailyReportList == null) {
            this.dailyReportList = new ArrayList<>();
        }
        this.dailyReportList.add(report);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
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

    public String getPersianDate() {
        return persianDate;
    }


    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", eventDate=" + eventDate +
                ", active=" + active +
                ", eventType=" + eventType +
                ", center=" + center +
                ", persianDate='" + persianDate + '\'' +
                '}';
    }

    public void setType(EventType eventType) {
        this.eventType = eventType;
    }
}
