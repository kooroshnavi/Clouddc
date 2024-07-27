package ir.tic.clouddc.center;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
@Data
public final class Center {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column
    private String name;

    @Column
    private String city;

    @Column
    private String province;

    @OneToMany(mappedBy = "center")
    private List<Location> locationList;
}
