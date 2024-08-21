package ir.tic.clouddc.center;

import ir.tic.clouddc.event.LocationCheckList;
import ir.tic.clouddc.event.LocationStatusForm;
import ir.tic.clouddc.report.DailyReport;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CenterService {

    Location getRefrencedLocation(Long locationId);

    List<Location> getLocationListExcept(List<Long> locationId);

    List<Location> getLocationList();

    void saveRackDevicePosition(Rack rack);

    void verifyRackDevicePosition(List<Rack> rackList);

    ////    Repository Projection name convention: EntityFiled1Field2...Projection
    interface CenterIdNameProjection {
        short getId();

        String getName();
    }
    interface LocationIdNameCenterCategoryProjection1 {
        Long getId();

        String getLocationName();

        String getCenterName();

        String getCategoryName();
    }

    List<LocationPmCatalog> getLocationCatalogList(Location baseLocation);

    LocationStatus getCurrentLocationStatus(Location location);

    List<Location> getCustomizedLocationList(List<String> locationCategoryNameList);

    Model getCenterLandingPageModel(Model model);

    List<CenterIdNameProjection> getCenterIdAndNameList();

    void updateLocationStatus(LocationStatusForm locationStatusForm, LocationCheckList event);

    Hall getHall(int hallId);

    Optional<Location> getLocation(Long locationId);

    Optional<Center> getCenter(int centerId);

    List<Hall> getHallList();

    List<Center> getCenterList();

    Model modelForCenterController(Model model);

    void setDailyTemperatureReport(DailyReport currentReport);

    List<Float> getWeeklyTemperature(List<LocalDate> weeklyDateList, int centerId);
}
