package ir.tic.clouddc.pm;

import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public class PmCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    @Nationalized
    private String name;

    @OneToMany(mappedBy = "pmCategory")
    private List<PmInterface> pmInterfaceList;



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

    public List<PmInterface> getPmInterfaceList() {
        return pmInterfaceList;
    }

    public void setPmInterfaceList(List<PmInterface> pmInterfaceList) {
        this.pmInterfaceList = pmInterfaceList;
    }
}
