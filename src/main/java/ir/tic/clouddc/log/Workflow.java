package ir.tic.clouddc.log;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Workflow {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private int id;

    @Column
    private LocalDate registerDate;  // Register or assign registerDate

    @Column
    private LocalTime registerTime;    // Register or assign registerTime

    @Column
    private String description;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "persistence_id")
    private Persistence persistence;

    @Transient
    private String persianRegisterDate;

    @Transient
    private String persianRegisterDayTime;


    public String getPersianRegisterDate() {
        return persianRegisterDate;
    }

    public void setPersianRegisterDate(String persianRegisterDate) {
        this.persianRegisterDate = persianRegisterDate;
    }

    public String getPersianRegisterDayTime() {
        return persianRegisterDayTime;
    }

    public void setPersianRegisterDayTime(String persianRegisterDayTime) {
        this.persianRegisterDayTime = persianRegisterDayTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Persistence getPersistence() {
        return persistence;
    }

    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
    }
}
