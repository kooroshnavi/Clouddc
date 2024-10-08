package ir.tic.clouddc.resource;

import ir.tic.clouddc.log.Persistence;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
@Getter
@Setter
public final class ModuleInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SpecificationID")
    private Integer id;

    @Column(name = "Classification", nullable = false)
    private String classification;  // Transceiver - Storage - Memory - Power - NIC - SmartArray

    @Column(name = "ClassificationId")
    private int classificationId;

    @Column(name = "Category", nullable = false)
    private String category;  // SFP/CVR - HDD/SSD - Memory - Power - NIC - SmartArray

    @Column(name = "CategoryId", nullable = false)
    private int categoryId;  // 1 - 2 - 3 - 4 - 5 - 6

    @Column(name = "Vendor", nullable = false)
    private String vendor; // hpe - samsung

    @Column(name = "Specification")
    private String spec;

    @Column(name = "Value", nullable = false)
    private float value; // 10 - 7.68

    @Column(name = "Unit", nullable = false)
    private String unit; // G - TB - length

    @Column(name = "Available")
    private int available; // for storage -> count(spare:true)

    @Column(name = "DeviceExtension")
    private boolean deviceExtension;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "PersistenceID")
    private Persistence persistence;

    @OneToMany(mappedBy = "moduleInventory", cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.DETACH})
    private List<Storage> storageList;
}
