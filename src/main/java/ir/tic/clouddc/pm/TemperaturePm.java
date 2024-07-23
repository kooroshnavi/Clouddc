package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.LocationPmCatalog;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "pm")
@NoArgsConstructor
public final class TemperaturePm extends Pm {

    @Transient
    private final String TEMPERATURE_FIELD_NAME = "دما";

    @Column
    private float averageDailyValue;

    @ManyToOne
    @JoinColumn(name = "catalog_id")
    private LocationPmCatalog locationPmCatalog;

    public String getTEMPERATURE_FIELD_NAME() {
        return TEMPERATURE_FIELD_NAME;
    }

    public float getAverageDailyValue() {
        return averageDailyValue;
    }

    public void setAverageDailyValue(float averageDailyValue) {
        this.averageDailyValue = averageDailyValue;
    }

}
