package ir.tic.clouddc.pm;

import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public class PmType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    @Nationalized
    private String name;

    @OneToMany(mappedBy = "type")
    private List<Pm> pmList;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persistent_id")
    private Persistence persistence;

    public PmType(String name, List<Pm> pmList, Persistence persistence) {
        this.name = name;
        this.pmList = pmList;
        this.persistence = persistence;
    }

    public PmType(int id) {
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Pm> getPmList() {
        return pmList;
    }

    public Persistence getPersistence() {
        return persistence;
    }
}
