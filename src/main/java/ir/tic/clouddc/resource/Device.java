package ir.tic.clouddc.resource;

import ir.tic.clouddc.center.Rack;
import ir.tic.clouddc.event.Event;
import ir.tic.clouddc.event.InstallationEvent;
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

    @Column
    private String type;

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
    private InstallationEvent installationEvent;

    public boolean isFailure() {
        return failure;
    }

    public void setFailure(boolean failure) {
        this.failure = failure;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Utilizer getUtilizer() {
        return utilizer;
    }

    public void setUtilizer(Utilizer utilizer) {
        this.utilizer = utilizer;
    }

    public Rack getRack() {
        return rack;
    }

    public void setRack(Rack rack) {
        this.rack = rack;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    public InstallationEvent getInstallationEvent() {
        return installationEvent;
    }

    public void setInstallationEvent(InstallationEvent installationEvent) {
        this.installationEvent = installationEvent;
    }

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
