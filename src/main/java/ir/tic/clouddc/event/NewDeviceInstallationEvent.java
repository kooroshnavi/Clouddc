package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.resource.Device;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(schema = "Event")
@NoArgsConstructor
@Getter
@Setter
public class NewDeviceInstallationEvent extends Event {

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST})
    private Location installationLocation;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Device> deviceList;
}
