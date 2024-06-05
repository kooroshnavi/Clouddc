package ir.tic.clouddc.center;

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
    private short id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pmInterface_id")
    private PmInterface pmInterface;

    @Column
    private LocalDate lastFinishedDate;

    @Column
    private LocalDate nextDueDate;

    @Transient
    private String persianFinishedDate;

    @Transient
    private String persianNextDue;


    public short getId() {
        return id;
    }

    public void setId(short id) {
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

    public LocalDate getLastFinishedDate() {
        return lastFinishedDate;
    }

    public void setLastFinishedDate(LocalDate lastFinishedDate) {
        this.lastFinishedDate = lastFinishedDate;
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

    public String getPersianNextDue() {
        return persianNextDue;
    }

    public void setPersianNextDue(String persianNextDue) {
        this.persianNextDue = persianNextDue;
    }
}
