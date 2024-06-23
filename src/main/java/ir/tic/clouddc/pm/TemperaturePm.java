package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.LocationPmCatalog;
import ir.tic.clouddc.utils.UtilService;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public final class TemperaturePm extends Pm {

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
    public PmDetail registerPm(LocationPmCatalog catalog) {
        this.setLocation(catalog.getLocation());
        this.setDueDate(UtilService.getDATE());
        this.setPmInterface(catalog.getPmInterface());
        this.setDelay(0);
        this.setActive(true);

        TemperaturePmDetail temperaturePmDetail = new TemperaturePmDetail();
        temperaturePmDetail.setPm(this);
        temperaturePmDetail.setDelay(0);
        temperaturePmDetail.setActive(true);
        temperaturePmDetail.setRegisterDate(UtilService.getDATE());
        temperaturePmDetail.setRegisterTime(UtilService.getTime());

        return temperaturePmDetail;
    }


}
