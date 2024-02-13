package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.Rack;
import jakarta.persistence.*;

public class PatchPanelPort extends Port {
    private static final String TYPE = "PatchPanel";

    @ManyToOne
    @JoinColumn(name = "rack_id")
    private Rack rack;

}
