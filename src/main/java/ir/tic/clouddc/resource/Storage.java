package ir.tic.clouddc.resource;

import jakarta.persistence.Column;

public abstract class Storage extends Module {

    @Column
    private float capacity;   // 3.84 - 16  - TB

    @Column
    private String controller;   // SAS - SATA

    @Column
    private int speed;  // 6 - Gbps
}
