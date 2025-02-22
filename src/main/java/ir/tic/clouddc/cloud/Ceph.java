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

    @Column(name = "Unit")
    private String unit;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private List<CephUtilizer> cephUtilizerList;
}
