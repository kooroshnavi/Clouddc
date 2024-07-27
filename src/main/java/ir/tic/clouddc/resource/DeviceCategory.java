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
    @Column
    private Integer id;

    @Column
    private String category;  // server - switch - firewall - enclosure

    @Column
    private int categoryId;  // 1 - 2 - 3 - 4

    @Column
    private String vendor; // hpe - cisco

    @Column
    private String model;   //

    @Column
    private String factor;

    @Column
    private int factorSize;

    @OneToMany(mappedBy = "deviceCategory")
    private List<Device> deviceList;
}
