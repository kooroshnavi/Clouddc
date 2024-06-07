package ir.tic.clouddc.resource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Resource")
@NoArgsConstructor
public class Server extends Device {

    @Column
    private String remoteAddress;

    @Column
    private String formFactor;   //LFF

    @Column
    private int factorSize;  // 8

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getFormFactor() {
        return formFactor;
    }

    public void setFormFactor(String formFactor) {
        this.formFactor = formFactor;
    }

    public int getFactorSize() {
        return factorSize;
    }

    public void setFactorSize(int factorSize) {
        this.factorSize = factorSize;
    }
}
