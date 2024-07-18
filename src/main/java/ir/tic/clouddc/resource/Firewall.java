package ir.tic.clouddc.resource;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "resource")
@NoArgsConstructor
public class Firewall extends Device{

}
