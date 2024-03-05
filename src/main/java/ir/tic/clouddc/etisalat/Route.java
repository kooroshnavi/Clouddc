package ir.tic.clouddc.etisalat;

import ir.tic.clouddc.resource.Device;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Etisalat")
@NoArgsConstructor
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "device_A_id")
    private Device deviceA;

    @ManyToOne
    @JoinColumn(name = "device_B_id")
    private Device deviceB;

    @OneToMany(mappedBy = "route")
    private List<Link> linkList;

    @Column
    private boolean up;

}
