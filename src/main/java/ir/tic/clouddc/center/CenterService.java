package ir.tic.clouddc.center;

import java.time.LocalDate;
import java.util.List;

public interface CenterService {
    Salon getSalon(int salonId);
    List<Salon> getDefaultCenterList();
    List<SalonPmDue> getTodayCenterPmList(LocalDate due);
    List<Salon> getSalonList();
    List<SalonPmDue> getTodaySalonPmList();
}
