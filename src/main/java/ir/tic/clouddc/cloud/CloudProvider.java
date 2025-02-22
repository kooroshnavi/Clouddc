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
public abstract class CloudProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CloudProviderGenerator")
    @SequenceGenerator(name = "CloudProviderGenerator", sequenceName = "CloudProvider_SEQ", allocationSize = 1, schema = "Cloud", initialValue = 1000)
    @Column(name = "CloudProviderID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ProviderID", nullable = false)
    private Utilizer provider;

    @Column(name = "localDateTime", nullable = false)
    private LocalDateTime localDateTime;

    @Column(name = "serviceType")
    private char serviceType;

    @Column(name = "isCurrent")
    private boolean current;

    @Transient
    private String persianDateTime;

    @Transient
    private String serviceName;
}
