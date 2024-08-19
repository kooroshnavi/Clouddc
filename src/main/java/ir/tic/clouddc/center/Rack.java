package ir.tic.clouddc.center;

import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.Utilizer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
@Getter
@Setter
public final class Rack extends Location {

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "HallID")
    private Hall hall;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "UtilizerID")
    private Utilizer utilizer;

    @OneToMany(mappedBy = "location")
    private List<Device> deviceList;

    @Column(name = "Description")
    private String description;
}
