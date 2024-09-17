package ir.tic.clouddc.resource;

import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
@Getter
@Setter
public final class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StorageID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "SpecificationID")
    private ModuleInventory moduleInventory;

    @Column(name = "SerialNumber")
    private String serialNumber;

    @Column(name = "MFG")
    private YearMonth mfg;

    @Column(name = "LocalityId", nullable = false)
    private long localityId; // Spare: true -> RoomID or 0 , false -> DeviceID

    @Column(name = "Spare", nullable = false)
    private boolean spare;

    @Column(name = "Active")
    private boolean active;

    @Column(name = "Problematic")
    private boolean problematic;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "PersistenceID")
    private Persistence persistence;

    public Storage(ModuleInventory moduleInventory, String serialNumber, YearMonth mfg, long localityId, boolean spare, boolean active, boolean problematic) {
        this.moduleInventory = moduleInventory;
        this.serialNumber = serialNumber;
        this.mfg = mfg;
        this.localityId = localityId;
        this.spare = spare;
        this.active = active;
        this.problematic = problematic;
    }
}
