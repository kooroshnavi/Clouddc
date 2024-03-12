package ir.tic.clouddc.resource;

import jakarta.persistence.Column;

public class QSFP extends Transceiver {
    @Column
    private int bandwidth; // 40, 100  - Gbps

    @Column
    private String range; // LR4, SR-BD, ...

    @Column
    private boolean form28; // for Q-sfp28

}
