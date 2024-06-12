package ir.tic.clouddc.center;

import ir.tic.clouddc.event.LocationStatusEvent;
import ir.tic.clouddc.event.LocationStatusForm;
import ir.tic.clouddc.pm.PmInterface;
import ir.tic.clouddc.report.DailyReport;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CenterService {

    ////    Repository Projection name convention: EntityFiled1Field2...Projection
    interface CenterIdNameProjection {
        short getId();

        String getName();
    }
    interface HallIdName {
        short getId();

        String getName();
    }


    List<Location> getTCustomizedLocationList(List<Short> locationCategoryTypeList);

    Model getCenterLandingPageModel(Model model);

    List<CenterIdNameProjection> getCenterIdAndNameList();

    void updateLocationStatus(LocationStatusForm locationStatusForm, LocationStatusEvent event);

    List<LocationPmCatalog> getTodayCatalogList(LocalDate date);

    void updateCatalogDueDate(PmInterface pmInterface, Location location);

    void updateNewlyEnabledCatalog(PmInterface pmInterface);

    Hall getHall(int hallId);

    Optional<Location> getLocation(int locationId);

    Model getLocationDetailModel(int locationId, Model model);

    Center getCenter(int centerId);

    List<Hall> getSalonList();

    List<Center> getCenterList();

    Model modelForCenterController(Model model);

    void setDailyTemperatureReport(DailyReport currentReport);

    List<Float> getWeeklyTemperature(List<LocalDate> weeklyDateList, int centerId);

    List<Rack> getRackList();

    List<Room> getRoomList();
}
