package ir.tic.clouddc.center;

import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.pm.Pm;
import ir.tic.clouddc.pm.PmInterface;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public class LocationPmCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pmInterface_id")
    private PmInterface pmInterface;

    @ManyToOne
    @JoinColumn(name = "default_workspace_id")
    private Person defaultPerson;

    @OneToOne
    @JoinColumn(name = "last_pm_id")
    private Pm lastFinishedPm;

    @Column
    private LocalDate nextDueDate;

    @Column
    private boolean enabled;

    @Transient
    private String persianFinishedDate;

    @Transient
    private String persianFinishedDayTime;

    @Transient
    private String persianNextDue;


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public PmInterface getPmInterface() {
        return pmInterface;
    }

    public void setPmInterface(PmInterface pmInterface) {
        this.pmInterface = pmInterface;
    }

    public Pm getLastFinishedPm() {
        return lastFinishedPm;
    }

    public void setLastFinishedPm(Pm lastFinishedPm) {
        this.lastFinishedPm = lastFinishedPm;
    }

    public Person getDefaultPerson() {
        return defaultPerson;
    }

    public void setDefaultPerson(Person defaultPerson) {
        this.defaultPerson = defaultPerson;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public String getPersianFinishedDate() {
        return persianFinishedDate;
    }

    public void setPersianFinishedDate(String persianFinishedDate) {
        this.persianFinishedDate = persianFinishedDate;
    }

    public String getPersianFinishedDayTime() {
        return persianFinishedDayTime;
    }

    public void setPersianFinishedDayTime(String persianFinishedDayTime) {
        this.persianFinishedDayTime = persianFinishedDayTime;
    }

    public String getPersianNextDue() {
        return persianNextDue;
    }

    public void setPersianNextDue(String persianNextDue) {
        this.persianNextDue = persianNextDue;
    }


}
