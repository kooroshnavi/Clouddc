package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.Rack;
import ir.tic.clouddc.event.Event;
import ir.tic.clouddc.event.Installation;
import ir.tic.clouddc.person.Utilizer;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private long id;

    @Column
    private String serialNumber;

    @Column
    private String vendor;

    @Column
    private boolean dualPower;

    @Column
    private boolean failure;

    @ManyToOne
    @JoinColumn(name = "utilizer_id")
    private Utilizer utilizer;

    @ManyToOne
    @JoinColumn(name = "rack_id")
    private Rack rack;

    @OneToMany(mappedBy = "device")
    private List<Event> eventList;

    @ManyToOne
    @JoinColumn(name = "installation_event_id")
    private Installation installation;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public boolean isDualPower() {
        return dualPower;
    }

    public void setDualPower(boolean dualPower) {
        this.dualPower = dualPower;
    }
}
