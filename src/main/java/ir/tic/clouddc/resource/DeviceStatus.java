package ir.tic.clouddc.resource;

import ir.tic.clouddc.event.Event;
import ir.tic.clouddc.log.Auditable;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
public class DeviceStatus extends Auditable {

    @Id
    private int id;

    @Column
    private boolean dualPower;  // order 0

    @Column
    private boolean sts; // order 1

    @Column
    private boolean fan; // order 2

    @Column
    private boolean module; // order 3

    @Column
    private boolean storage; // order 4

    @Column
    private boolean port; // order 5

    @Column
    private boolean current;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    @OneToOne(cascade = {CascadeType.ALL})
    @MapsId
    @JoinColumn(name = "event_id")
    private Event event;

    @Transient
    private String persianCheckDate;

    @Transient
    private String persianCheckDayTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDualPower() {
        return dualPower;
    }

    public void setDualPower(boolean dualPower) {
        this.dualPower = dualPower;
    }

    public boolean isSts() {
        return sts;
    }

    public void setSts(boolean sts) {
        this.sts = sts;
    }

    public boolean isFan() {
        return fan;
    }

    public void setFan(boolean fan) {
        this.fan = fan;
    }

    public boolean isModule() {
        return module;
    }

    public void setModule(boolean module) {
        this.module = module;
    }

    public boolean isStorage() {
        return storage;
    }

    public void setStorage(boolean storage) {
        this.storage = storage;
    }

    public boolean isPort() {
        return port;
    }

    public void setPort(boolean port) {
        this.port = port;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getPersianCheckDate() {
        return persianCheckDate;
    }

    public void setPersianCheckDate(String persianCheckDate) {
        this.persianCheckDate = persianCheckDate;
    }

    public String getPersianCheckDayTime() {
        return persianCheckDayTime;
    }

    public void setPersianCheckDayTime(String persianCheckDayTime) {
        this.persianCheckDayTime = persianCheckDayTime;
    }
}
