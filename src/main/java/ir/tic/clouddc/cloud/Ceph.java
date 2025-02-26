package ir.tic.clouddc.cloud;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(schema = "Cloud")
@NoArgsConstructor
@Getter
@Setter
public class Ceph extends CloudProvider {

    @Column(name = "Capacity", nullable = false)
    private float capacity;

    @Column(name = "Usage", nullable = false)
    private float usage;

    @Column(name = "CapacityUnit", nullable = false)
    private String capacityUnit;

    @Column(name = "UsageUnit", nullable = false)
    private String usageUnit;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private List<CephUtilizer> cephUtilizerList;

    @Transient
    private String readOnlyRemaining;

    @Transient
    private String readOnlyRemainingUnit;

}
