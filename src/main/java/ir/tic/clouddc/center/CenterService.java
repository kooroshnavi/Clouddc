package ir.tic.clouddc.center;

import java.time.LocalDate;
import java.util.List;

public interface CenterService {
    Salon getCenter(int centerId);
    List<Salon> getDefaultCenterList();
    List<SalonChecklist> getTodayCenterPmList(LocalDate due);
    List<Salon> getCenterList();
}
