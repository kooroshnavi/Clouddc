package ir.tic.clouddc.resource;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Table(schema = "Resource")
@Data
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SupplierID")
    private Integer supplierID;

    @Column(name = "Name")
    private String name;

    @Column(name = "ContractNumber")
    private String contractNumber;

    @Column(name = "DeliveryDate")
    private LocalDate deliveryDate;
}
