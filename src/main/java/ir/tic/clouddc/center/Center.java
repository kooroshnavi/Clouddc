package ir.tic.clouddc.center;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public class Center {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    @Nationalized
    private String name;

    @Column
    private String city;

    @Column
    private String province;

    @OneToMany(mappedBy = "dataCenter")
    private List<Salon> salonList;

    public Center(int id) {
        this.id = id;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public List<Salon> getSalonList() {
        return salonList;
    }

    public void setSalonList(List<Salon> salonList) {
        this.salonList = salonList;
    }
}
