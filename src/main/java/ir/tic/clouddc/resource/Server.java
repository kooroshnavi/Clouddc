package ir.tic.clouddc.resource;

import ir.tic.clouddc.etisalat.Route;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
public class Server extends Device{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @OneToOne
    @JoinColumn(name = "iLo_Route_id")
    private Route iLoRoute;

    @Column
    private String iLoAddress;



}
