package ir.tic.clouddc.center;

import ir.tic.clouddc.resource.Device;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
class Room extends ResourceLocation {

    private static final String TYPE = "Location-Room";

    @ManyToOne
    @JoinColumn(name = "datacenter")
    private DataCenter dataCenter;

    @OneToMany(mappedBy = "room")
    private List<Device> deviceList;

}
