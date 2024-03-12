package ir.tic.clouddc.resource;

import jakarta.persistence.Column;

public class SFP extends Transceiver { // Single Form pluggable

    @Column
    private boolean form25;  // true for sfp25

    @Column
    private int bandwidth; // 1, 10, 40, 100  - Gbps

    @Column
    private String range; // LR, SR, ...

}
