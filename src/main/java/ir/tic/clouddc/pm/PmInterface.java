package ir.tic.clouddc.pm;

import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public class PmInterface {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    @Nationalized
    private String name;

    @Column
    private int period;

    @Column
    private boolean active;

    @Column
    private boolean enabled;

    @Column
    private boolean general;

    @Column
    @Nationalized
    private String description;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pmCategory_id")
    private PmCategory pmCategory;

    @OneToMany(mappedBy = "pmInterface")
    private List<Pm> pmList;

    @OneToOne
    @JoinColumn(name = "persistence_id")
    private Persistence persistence;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isGeneral() {
        return general;
    }

    public void setGeneral(boolean general) {
        this.general = general;
    }

    public List<Pm> getPmList() {
        return pmList;
    }

    public void setPmList(List<Pm> pmList) {
        this.pmList = pmList;
    }

    public PmCategory getPmCategory() {
        return pmCategory;
    }

    public void setPmCategory(PmCategory pmCategory) {
        this.pmCategory = pmCategory;
    }

    public Persistence getPersistence() {
        return persistence;
    }

    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
    }
}
