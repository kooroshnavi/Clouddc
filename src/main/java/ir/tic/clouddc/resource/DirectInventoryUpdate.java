package ir.tic.clouddc.resource;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
public final class DirectInventoryUpdate extends InventoryUpdateDetail {

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.DETACH})
    @JoinColumn(name = "SpecificationID", nullable = false)
    private ModuleInventory moduleInventory;
}