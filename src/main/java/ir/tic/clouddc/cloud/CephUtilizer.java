package ir.tic.clouddc.cloud;

import ir.tic.clouddc.resource.Utilizer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(schema = "Cloud")
@NoArgsConstructor
@Getter
@Setter
public class CephUtilizer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CephUtilizerID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ProviderID", nullable = false)
    private Ceph provider;

    @ManyToOne
    @JoinColumn(name = "UtilizerID", nullable = false)
    private Utilizer utilizer;

    @Column(name = "Usage", nullable = false)
    private float usage;

    @Column(name = "Unit", nullable = false)
    private String unit;

    @Column(name = "localDateTime", nullable = false)
    private LocalDateTime localDateTime;

    @Transient
    private String persianDateTime;
}
