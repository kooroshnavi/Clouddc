package ir.tic.clouddc.resource;

import ir.tic.clouddc.etisalat.Route;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
public class Switch extends Device {

    private static final String TYPE = "Device-Switch";

    @OneToOne
    @JoinColumn(name = "management_route_id")
    private Route managementRoute;

    @OneToMany
    private Map<Port, String> vlanMap;

}
