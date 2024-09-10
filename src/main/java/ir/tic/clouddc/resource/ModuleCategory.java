package ir.tic.clouddc.resource;

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
public final class ModuleCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ModuleCategoryID")
    private Integer id;

    @Column(name = "Classification", nullable = false)
    private String classification;  // Transceiver - Storage - Memory - Power - NIC - SmartArray

    @Column(name = "Category", nullable = false)
    private String category;  // SFP/CVR - HDD/SSD - Memory - Power - NIC - SmartArray

    @Column(name = "CategoryId", nullable = false)
    private int categoryId;  // 1 - 2 - 3 - 4 - 5 - 6

    @Column(name = "Vendor", nullable = false)
    private String vendor; // hpe - samsung

    @Column(name = "Specification")
    private String spec;

    @Column(name = "PartNumber")
    private String partNumber;

    @Column(name = "Value", nullable = false)
    private float value; // 10 - 7.68

    @Column(name = "Unit", nullable = false)
    private String unit; // G - TB

    @OneToMany(mappedBy = "moduleCategory")
    private List<Module> moduleList;
}
