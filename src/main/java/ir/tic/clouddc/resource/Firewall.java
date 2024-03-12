package ir.tic.clouddc.resource;


import ir.tic.clouddc.etisalat.Route;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
public class Firewall extends Device {

    @Column
    private Route HA;
}
