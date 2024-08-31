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
public final class Room extends Location {

    @OneToMany(mappedBy = "location")
    private List<Device> deviceList;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "UtilizerID")
    private Utilizer utilizer;
}
