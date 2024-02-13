package ir.tic.clouddc.resource;

import ir.tic.clouddc.etisalat.Route;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
public class Server extends Device {

    private static final String TYPE = "Device-Server";

    @OneToOne
    @JoinColumn(name = "iLo_Route_id")
    private Route iLoRoute;

    @Column
    private String iLoAddress;

    @OneToMany(mappedBy = "device_id")
    private List<Port> portList;

    private List<Route> routeList;

}
