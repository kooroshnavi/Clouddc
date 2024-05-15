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

    @OneToOne
    @JoinColumn(name = "persistent_id")
    private Persistence persistence;

}
