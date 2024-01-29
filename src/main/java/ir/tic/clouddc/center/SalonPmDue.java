package ir.tic.clouddc.center;

import ir.tic.clouddc.task.Pm;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public class SalonPmDue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @ManyToOne
    @JoinColumn(name = "salon_id")
    private Salon salon;

    @ManyToOne
    @JoinColumn(name = "pm_id")
    private Pm pm;

    @Column
    private LocalDate due;

    @Transient
    private String persianDue;

    public String getPersianDue() {
        return persianDue;
    }

    public void setPersianDue(String persianDue) {
        this.persianDue = persianDue;
    }

    @Override
    public String toString() {
        return "CenterPmDue{" +
                "id=" + id +
                ", center=" + salon +
                ", pm=" + pm +
                ", nextDue=" + due +
                ", persianNextDue='" + persianDue + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Salon getSalon() {
        return salon;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }

    public Pm getPm() {
        return pm;
    }

    public void setPm(Pm pm) {
        this.pm = pm;
    }

    public LocalDate getDue() {
        return due;
    }

    public void setDue(LocalDate due) {
        this.due = due;
    }
}
