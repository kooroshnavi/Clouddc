package ir.tic.clouddc.center;

import ir.tic.clouddc.event.Event;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public class LocationStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private boolean door;

    @Column
    private boolean ventilation;

    @Column
    private boolean power;

    @Column
    private boolean current;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_id")
    private Event event;

    @Transient
    private String persianStatusDate;

    @Transient
    private String persianStatusDayTime;

    public String getPersianStatusDate() {
        return persianStatusDate;
    }

    public void setPersianStatusDate(String persianStatusDate) {
        this.persianStatusDate = persianStatusDate;
    }

    public String getPersianStatusDayTime() {
        return persianStatusDayTime;
    }

    public void setPersianStatusDayTime(String persianStatusDayTime) {
        this.persianStatusDayTime = persianStatusDayTime;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDoor() {
        return door;
    }

    public void setDoor(boolean door) {
        this.door = door;
    }

    public boolean isVentilation() {
        return ventilation;
    }

    public void setVentilation(boolean ventilation) {
        this.ventilation = ventilation;
    }

    public boolean isPower() {
        return power;
    }

    public void setPower(boolean power) {
        this.power = power;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
