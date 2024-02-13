package ir.tic.clouddc.etisalat;

import ir.tic.clouddc.resource.Device;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Etisalat")
@NoArgsConstructor
public class CompoundLink extends Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;   // Device

}
