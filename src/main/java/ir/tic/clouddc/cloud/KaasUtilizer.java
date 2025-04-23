package ir.tic.clouddc.cloud;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "Cloud")
@NoArgsConstructor
@Getter
@Setter
public class KaasUtilizer extends CloudUtilizer {

    @Column(name = "Node", nullable = false)
    private int node;

    @Column(name = "CPU", nullable = false)
    private int cpu;

    @Column(name = "RAM", nullable = false)
    private float memory;

    @Column(name = "SSD", nullable = false)
    private int ssd;

    @Column(name = "HDD", nullable = false)
    private int hdd;

    @Column(name = "RamUnit", nullable = false)
    private String memoryUnit;

    @Column(name = "SsdUnit", nullable = false)
    private String ssdUnit;

    @Column(name = "HddUnit", nullable = false)
    private String hddUnit;

}
