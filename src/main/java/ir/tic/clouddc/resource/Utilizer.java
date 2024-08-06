package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.Rack;
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
public final class Utilizer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UtilizerID")
    private Integer id;

    @Column(name = "Name")
    private String name;

    @Column(name = "Messenger")
    private boolean messenger;

    @OneToMany(mappedBy = "utilizer")
    private List<Rack> rackList;

    @OneToMany(mappedBy = "utilizer")
    private List<Device> deviceList;
}
