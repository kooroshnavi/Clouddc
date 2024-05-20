package ir.tic.clouddc.resource;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String serialNumber;

    @Column
    private String vendor;

    @Column
    private boolean dualPower;

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
