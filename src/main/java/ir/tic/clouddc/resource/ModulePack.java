package ir.tic.clouddc.resource;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
@Getter
@Setter
public final class ModulePack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ModulePackID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "DeviceID")
    private Device device;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.DETACH})
    @JoinColumn(name = "InventoryID")
    private ModuleInventory moduleInventory;

    @Column(name = "Assigned")
    private int qty;
}
