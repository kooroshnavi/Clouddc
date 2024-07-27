package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.Rack;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
@Data
public final class Utilizer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column
    private String name;

    @Column
    private boolean messenger;

    @OneToMany(mappedBy = "utilizer")
    private List<Rack> rackList;

    @OneToMany(mappedBy = "utilizer")
    private List<Device> deviceList;
}
