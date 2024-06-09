package ir.tic.clouddc.center;

import ir.tic.clouddc.pm.PmInterface;
import ir.tic.clouddc.report.DailyReport;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;

public interface CenterService {

    Model getCenterLandingPageModel(Model model);
    List<LocationPmCatalog> getTodayCatalogList(LocalDate date);
    void updateCatalogDueDate(PmInterface pmInterface, Location location);
    void updateNewlyEnabledCatalog(PmInterface pmInterface);
    List<Location> getLocationList();
    Salon getSalon(int salonId);
    Location getLocation(int locationId);
    Model getLocationDetailModel(int locationId, Model model);
    Center getCenter(int centerId);
    List<Salon> getSalonList();
    List<Center> getCenterList();
    Model modelForCenterController(Model model);
    void setDailyTemperatureReport(DailyReport currentReport);
    List<Float> getWeeklyTemperature(List<LocalDate> weeklyDateList, int centerId);
    List<Rack> getRackList();
    List<Room> getRoomList();
}
