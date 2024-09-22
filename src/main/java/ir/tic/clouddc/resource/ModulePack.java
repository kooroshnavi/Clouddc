package ir.tic.clouddc.resource;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "modulePackHistory", schema = "Resource",
            joinColumns = {@JoinColumn(name = "ModulePackID", referencedColumnName = "ModulePackID")})
    @MapKeyColumn(name = "LocalDateTime")
    @Column(name = "Balance")
    private Map<LocalDateTime, Integer> packHistory;

}
