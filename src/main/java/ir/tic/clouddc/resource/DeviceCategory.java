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
public final class DeviceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DeviceCategoryID")
    private Integer id;

    @Column(name = "Category", nullable = false)
    private String category;  // server - switch - firewall - enclosure

    @Column(name = "CategoryId", nullable = false)
    private int categoryId;  // 5 - 6 - 7 - 8

    @Column(name = "Vendor", nullable = false)
    private String vendor; // hpe - cisco

    @Column(name = "Model", nullable = false)
    private String model;   //

    @Column(name = "Factor")
    private String factor;

    @Column(name = "FactorSize")
    private int factorSize;

    @OneToMany(mappedBy = "deviceCategory")
    private List<Device> deviceList;
}
