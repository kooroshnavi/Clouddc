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
public final class GeneralPm extends Pm {

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

}
