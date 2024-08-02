package ir.tic.clouddc.resource;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
@Data
public final class DeviceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DeviceCategoryID")
    private Integer id;

    @Column(name = "Category")
    private String category;  // server - switch - firewall - enclosure

    @Column(name = "CategoryId")
    private int categoryId;  // 1 - 2 - 3 - 4

    @Column(name = "Vendor")
    private String vendor; // hpe - cisco

    @Column(name = "Model")
    private String model;   //

    @Column(name = "Factor")
    private String factor;

    @Column(name = "FactorSize")
    private int factorSize;

    @OneToMany(mappedBy = "deviceCategory")
    private List<Device> deviceList;
}
