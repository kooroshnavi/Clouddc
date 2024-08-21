package ir.tic.clouddc.resource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
@Getter
@Setter
public final class Server extends Device {

    @Column(name = "RemoteAddress", unique = true)
    private String remoteAddress;
}
