package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.LocationPmCatalog;

public interface PmAction {

    public PmDetail registerPm(LocationPmCatalog catalog);

    public PmDetail updatePmDetail(PmDetail pmDetail, PmUpdateForm pmUpdateForm);

    public PmDetail registerPmDetail();

    public Pm endPm(Pm pm);
}
