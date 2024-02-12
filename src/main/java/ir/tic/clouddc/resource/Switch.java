package ir.tic.clouddc.resource;

import ir.tic.clouddc.etisalat.Route;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
public class Switch extends Device{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "management_route_id")
    private Route managementRoute;

    @OneToMany
    private Map<Port, String> vlanMap;


}
