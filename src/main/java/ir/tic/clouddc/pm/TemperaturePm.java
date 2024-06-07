package ir.tic.clouddc.pm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public class TemperaturePm extends Pm {

    @Transient
    private final String TEMPERATURE_FIELD_NAME = "دما";

    @Column
    private float averageDailyValue;

    public String getTEMPERATURE_FIELD_NAME() {
        return TEMPERATURE_FIELD_NAME;
    }

    public float getAverageDailyValue() {
        return averageDailyValue;
    }

    public void setAverageDailyValue(float averageDailyValue) {
        this.averageDailyValue = averageDailyValue;
    }

    @Override
    public void registerPm() {

    }

    @Override
    public void assignPm() {

    }

    @Override
    public void endPm() {

    }
}
