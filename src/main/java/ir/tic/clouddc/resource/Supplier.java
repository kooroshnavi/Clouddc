package ir.tic.clouddc.resource;

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