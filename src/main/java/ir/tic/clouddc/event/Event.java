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
    private int id;

    @Column
    private boolean active;

    @Column
    private LocalDate registerDate;

    @Column
    private LocalTime registerTime;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "category_id")
    private EventCategory eventCategory;

    @OneToMany(mappedBy = "event")
    private List<EventDetail> eventDetailList;

    @Transient
    private String persianRegisterDate;

    @Transient
    private String persianRegisterDayTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPersianRegisterDayTime() {
        return persianRegisterDayTime;
    }

    public void setPersianRegisterDayTime(String persianRegisterDayTime) {
        this.persianRegisterDayTime = persianRegisterDayTime;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDate registerDate) {
        this.registerDate = registerDate;
    }

    public LocalTime getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(LocalTime registerTime) {
        this.registerTime = registerTime;
    }

    public List<EventDetail> getEventDetailList() {
        return eventDetailList;
    }

    public String getPersianRegisterDate() {
        return persianRegisterDate;
    }

    public void setPersianRegisterDate(String persianRegisterDate) {
        this.persianRegisterDate = persianRegisterDate;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }


}

