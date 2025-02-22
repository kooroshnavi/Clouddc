package ir.tic.clouddc.resource;

import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public final class ModulePackUpdate extends InventoryUpdateDetail {

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.DETACH})
    @JoinColumn(name = "ModulePackID", nullable = false)
    private ModulePack modulePack;
}
