package ir.tic.clouddc.resource;

import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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
    private LocalDate mfg;

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
}
