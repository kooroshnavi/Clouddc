package ir.tic.clouddc.resource;

import jakarta.persistence.*;

public abstract class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;
    @Column
    private String vendor;
    @Column
    private String serialNumber;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;
}
