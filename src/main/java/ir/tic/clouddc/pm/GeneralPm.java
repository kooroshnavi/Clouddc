package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.center.LocationPmCatalog;
import ir.tic.clouddc.utils.UtilService;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "Pm")
@NoArgsConstructor
public class GeneralPm extends Pm {

    private CenterService centerService;

    @Override
    public PmDetail registerPm(LocationPmCatalog catalog) {
        this.setLocation(catalog.getLocation());
        this.setDueDate(UtilService.getDATE());
        this.setPmInterface(catalog.getPmInterface());
        this.setDelay(0);
        this.setActive(true);
        GeneralPmDetail generalPmDetail = new GeneralPmDetail();
        generalPmDetail.setPm(this);
        generalPmDetail.setDelay(0);
        generalPmDetail.setActive(true);
        generalPmDetail.setRegisterDate(UtilService.getDATE());
        generalPmDetail.setRegisterTime(UtilService.getTime());

        return generalPmDetail;
    }

    @Override
    public PmDetail updatePmDetail(PmDetail pmDetail, PmUpdateForm pmUpdateForm) {
        GeneralPmDetail generalPmDetail = (GeneralPmDetail) pmDetail;
        generalPmDetail.setFinishedDate(UtilService.getDATE());
        generalPmDetail.setFinishedTime(UtilService.getTime());
        generalPmDetail.setActive(false);

        return generalPmDetail;
    }

    @Override
    public PmDetail registerPmDetail() {
        GeneralPmDetail pmDetail = new GeneralPmDetail();
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
        centerService.updateCatalogDueDate(pm.getPmInterface(), pm.getLocation());

        return pm;
    }


}
