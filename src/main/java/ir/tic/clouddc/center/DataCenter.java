package ir.tic.clouddc.center;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

import java.util.List;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public class DataCenter {

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


}
