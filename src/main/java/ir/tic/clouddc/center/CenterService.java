package ir.tic.clouddc.center;

import ir.tic.clouddc.event.LocationStatusEvent;
import ir.tic.clouddc.event.LocationStatusForm;
import ir.tic.clouddc.pm.CatalogForm;
import ir.tic.clouddc.report.DailyReport;
import org.springframework.ui.Model;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CenterService {
    LocationPmCatalog registerNewCatalog(CatalogForm catalogForm, LocalDate validNextDue);

    Location getRefrencedLocation(Long locationId) throws SQLException;

    ////    Repository Projection name convention: EntityFiled1Field2...Projection
    interface CenterIdNameProjection {
        short getId();

        String getName();
    }
    interface LocationIdNameProjection {
        Long getId();

        String getName();
    }

    List<LocationPmCatalog> getLocationCatalogList(Location baseLocation);

    LocationStatus getCurrentLocationStatus(Location location);

    List<Location> getCustomizedLocationList(List<String> locationCategoryNameList);

    Model getCenterLandingPageModel(Model model);

    List<CenterIdNameProjection> getCenterIdAndNameList();

    void updateLocationStatus(LocationStatusForm locationStatusForm, LocationStatusEvent event);

    Hall getHall(int hallId);

    Optional<Location> getLocation(Long locationId);

    Optional<Center> getCenter(int centerId);

    List<Hall> getHallList();

    List<Center> getCenterList();

    Model modelForCenterController(Model model);

    void setDailyTemperatureReport(DailyReport currentReport);

    List<Float> getWeeklyTemperature(List<LocalDate> weeklyDateList, int centerId);


}
