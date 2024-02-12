package ir.tic.clouddc.center;

import ir.tic.clouddc.etisalat.Link;
import ir.tic.clouddc.resource.Device;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;

@Entity
@Table(schema = "Center")
@NoArgsConstructor
public class Rack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "salon_id")
    private Salon salon;

    private Map<String, Optional<Link>> patchPanel;    // B-7, 11: PatchPanel B, Port 7,8, Link id = 11.

    private Map<Integer, Optional<Device>> deviceMap ; // <UnitOffset, device> inside rack

    private boolean powerOn; // Rack has power or not.

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Salon getSalon() {
        return salon;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }

    public Map<String, Optional<Link>> getPatchPanel() {
        return patchPanel;
    }

    public void setPatchPanel(Map<String, Optional<Link>> patchPanel) {
        this.patchPanel = patchPanel;
    }

    public Map<Integer, Optional<Device>> getDeviceMap() {
        return deviceMap;
    }

    public void setDeviceMap(Map<Integer, Optional<Device>> deviceMap) {
        this.deviceMap = deviceMap;
    }

    public boolean isPowerOn() {
        return powerOn;
    }

    public void setPowerOn(boolean powerOn) {
        this.powerOn = powerOn;
    }
}
