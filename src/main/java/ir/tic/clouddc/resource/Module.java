package ir.tic.clouddc.resource;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Map;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(schema = "Resource")
public abstract class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String vendor; // cisco- samsung - seagate , ...

    @Column
    private String serialNumber;

    @OneToMany
    private Map<LocalDate, Device> moduleDeviceHistoryMap;

}
