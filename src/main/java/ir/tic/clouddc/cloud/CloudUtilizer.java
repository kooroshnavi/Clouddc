package ir.tic.clouddc.cloud;

import ir.tic.clouddc.resource.Utilizer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor
@Getter
@Setter
public abstract class CloudUtilizer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CloudUtilizerGenerator")
    @SequenceGenerator(name = "CloudUtilizerGenerator", sequenceName = "CloudUtilizer_SEQ", allocationSize = 1, schema = "Cloud", initialValue = 1000)
    @Column(name = "CloudUtilizerID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ProviderID", nullable = false)
    private CloudProvider provider;

    @ManyToOne
    @JoinColumn(name = "UtilizerID", nullable = false)
    private Utilizer utilizer;

    @Column(name = "localDateTime", nullable = false)
    private LocalDateTime localDateTime;

}
