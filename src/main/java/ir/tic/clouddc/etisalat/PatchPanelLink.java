package ir.tic.clouddc.etisalat;

import ir.tic.clouddc.center.Rack;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Etisalat")
@NoArgsConstructor
public class PatchPanelLink extends Link {

    private Rack source;

    private Rack destination;

    private String sourcePatchPanel;

    private String destinationPatchPanel;

}