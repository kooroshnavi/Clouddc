package ir.tic.clouddc.resource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
public class Storage extends Module {

    @Column
    private float capacity;   // 3.84 - 16  - TB

    @Column
    private String controller;   // SAS - SATA

    @Column
    private int speed;  // 12 - Gbps

    @Column
    private String type;  // ssd - hdd

    @Column
    private boolean nvme; // for ssd

    @Column
    private boolean m2; // for ssd

}
