package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.Rack;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
public class PatchPanelPort extends Port {

    private static final String TYPE = "Port-PatchPanel";

    @ManyToOne
    @JoinColumn(name = "rack_id")
    private Rack rack;

}
