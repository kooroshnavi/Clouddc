package ir.tic.clouddc.pm;

import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Pm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private int id;

    @Column
    @Nationalized
    private String name;

    @Column
    @Nationalized
    private String description;

    @Column
    private int period;

    @Column
    private boolean enabled;

    @Column
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private PmCategory category;

    @OneToOne
    @JoinColumn(name = "persistence_id")
    private Persistence persistence;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public PmCategory getCategory() {
        return category;
    }

    public void setCategory(PmCategory category) {
        this.category = category;
    }

    public Persistence getPersistence() {
        return persistence;
    }

    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
    }

}
