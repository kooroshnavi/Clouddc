package ir.tic.clouddc.resource;

import jakarta.persistence.Column;

public class CVR extends Transceiver {

    @Column
    private SFP includedSFP;

}
