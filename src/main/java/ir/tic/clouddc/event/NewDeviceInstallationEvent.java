package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.Utilizer;
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
    @JoinColumn(name = "InstallationLocation")
    private Location installationLocation;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST})
    @JoinColumn(name = "InstallationUtilizer")
    private Utilizer installationUtilizer;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Device> deviceList;

}
