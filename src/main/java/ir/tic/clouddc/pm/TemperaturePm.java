package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.CenterService;
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
public class TemperaturePm extends Pm {

    private CenterService centerService;

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

        centerService.updateCatalogDueDate(catalog.getPmInterface(), catalog.getLocation());

        return temperaturePmDetail;
    }

    @Override
    public PmDetail updatePmDetail(PmDetail pmDetail, PmUpdateForm pmUpdateForm) {
        TemperaturePmDetail temperaturePmDetail = (TemperaturePmDetail) pmDetail;
        temperaturePmDetail.setFinishedDate(UtilService.getDATE());
        temperaturePmDetail.setFinishedTime(UtilService.getTime());
        temperaturePmDetail.setActive(false);
        temperaturePmDetail.setTemperatureValue(pmUpdateForm.getTemperatureValue());

        return temperaturePmDetail;
    }

    @Override
    public PmDetail registerPmDetail() {
        TemperaturePmDetail pmDetail = new TemperaturePmDetail();
        pmDetail.setRegisterDate(UtilService.getDATE());
        pmDetail.setRegisterTime(UtilService.getTime());
        pmDetail.setDelay(0);
        pmDetail.setPm(this);
        return pmDetail;
    }

    @Override
    public Pm endPm(Pm pm) {
        pm.setFinishedDate(UtilService.getDATE());
        pm.setFinishedTime(UtilService.getTime());
        pm.setActive(false);
        TemperaturePm temperaturePm = (TemperaturePm) pm;
        List<TemperaturePmDetail> temperaturePmDetailList = new ArrayList<>();
        for (PmDetail pmDetail : temperaturePm.getPmDetailList()) {
            temperaturePmDetailList.add((TemperaturePmDetail) pmDetail);
        }
        float sum = (float) temperaturePmDetailList.stream().filter(temperaturePmDetail -> temperaturePmDetail.getTemperatureValue() > 0.0f).mapToDouble(TemperaturePmDetail::getTemperatureValue).sum();
        int reportedTemperatures = (int) temperaturePmDetailList.stream().filter(temperaturePmDetail -> temperaturePmDetail.getTemperatureValue() > 0.0f).count();
        temperaturePm.setAverageDailyValue(sum / reportedTemperatures);

        return temperaturePm;
    }
}
