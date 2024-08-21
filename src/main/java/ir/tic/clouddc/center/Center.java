package ir.tic.clouddc.center;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
@Getter
@Setter
public final class Center {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CenterID")
    private Integer id;

    @Column(name = "Name", unique = true, nullable = false)
    private String name;

    @Column(name = "City")
    private String city;

    @Column(name = "Province")
    private String province;

    @OneToMany(mappedBy = "center")
    private List<Location> locationList;
}
