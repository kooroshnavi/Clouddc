package ir.tic.clouddc.event;


import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.tic.clouddc.log.WorkFlow;
import jakarta.persistence.*;

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

    @OneToMany(mappedBy = "event", cascade = {CascadeType.ALL})
    private List<WorkFlow> eventDetailList;

    @Column
    private String description;

    @Transient
    private String persianDate;

    @Transient
    private String persianDay;

    @Transient
    private String time;


    public List<WorkFlow> getEventDetailList() {
        return eventDetailList;
    }

    public void setEventDetailList(List<WorkFlow> eventDetailList) {
        this.eventDetailList = eventDetailList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setPersianDate(String persianDate) {
        this.persianDate = persianDate;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    @JsonIgnore
    public boolean isActive() {
        return active;
    }

    public String getPersianDate() {
        return persianDate;
    }

}

