package ir.tic.clouddc.center;

import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.PatchPanelPort;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public class Rack extends ResourceLocation {

    private static final String TYPE = "Location-Rack";

    @ManyToOne
    @JoinColumn(name = "salon_id")
    private Salon salon;

    @Column
    private boolean powered; // Rack has power or not.

    @OneToMany(mappedBy = "rack")
    private List<PatchPanelPort> patchPanelPortList;

    private Map<Integer, Optional<Device>> deviceMap; // <UnitOffset, device> inside rack


}
