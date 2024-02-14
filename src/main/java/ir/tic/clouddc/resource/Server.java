package ir.tic.clouddc.resource;

import ir.tic.clouddc.etisalat.Route;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

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

    public Route getiLoRoute() {
        return iLoRoute;
    }

    public void setiLoRoute(Route iLoRoute) {
        this.iLoRoute = iLoRoute;
    }

    public String getiLoAddress() {
        return iLoAddress;
    }

    public void setiLoAddress(String iLoAddress) {
        this.iLoAddress = iLoAddress;
    }

    @Override
    public String toString() {
        return "Server{" +
                "iLoRoute=" + iLoRoute +
                ", iLoAddress='" + iLoAddress + '\'' +
                '}';
    }
}
