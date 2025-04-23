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
public class IaasUtilizer extends CloudUtilizer {

    @Column(name = "RunningVM", nullable = false)
    private int runningVM;

    @Column(name = "CpuCore", nullable = false)
    private int cpuCore;

    @Column(name = "AllocatedRam", nullable = false)
    private float allocatedMemory;

    @Column(name = "Disk", nullable = false)
    private float disk;

    @Column(name = "TotalVM", nullable = false)
    private int totalVM;

}
