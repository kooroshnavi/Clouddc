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
    private short id;

    @Column
    @Nationalized
    private String name;

    @OneToMany(mappedBy = "pmCategory")
    private List<PmInterface> pmInterfaceList;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persistent_id")
    private Persistence persistence;

}
