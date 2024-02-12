package ir.tic.clouddc.etisalat;

import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.Port;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Etisalat")
@NoArgsConstructor
public class DeviceLink extends Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne
    @JoinColumn(name = "source_device_id")
    private Device source;   // Device

    @OneToOne
    @JoinColumn(name = "Source_Port_id")
    private Port sourcePort;

    @OneToOne
    @JoinColumn(name = "Source_Port_id")
    private Port destinationPort;

    @ManyToOne
    @JoinColumn(name = "destination_device_id")
    private Device destination; // Device

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Device getSource() {
        return source;
    }

    public void setSource(Device source) {
        this.source = source;
    }

    public Device getDestination() {
        return destination;
    }

    public void setDestination(Device destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "DeviceLink{" +
                "id=" + id +
                ", source=" + source +
                ", destination=" + destination +
                '}';
    }
}
