package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Center;
import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.center.LocationStatus;
import ir.tic.clouddc.document.MetaData;
import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.DeviceStatus;
import ir.tic.clouddc.resource.Utilizer;
import jakarta.annotation.Nullable;
import org.springframework.ui.Model;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EventService {


    void eventSetup(EventLandingForm eventLandingForm
            , @Nullable DeviceStatusForm deviceStatusForm
            , @Nullable LocationStatusForm locationStatusForm) throws IOException, SQLException;


    List<EventCategory> getEventCategoryList();

    List<CenterService.CenterIdNameProjection> getCenterIdAndNameList();

    Model modelForEventController(Model model);

    Event getEventHistory(Long eventId);

    MetaData getRelatedMetadata(Long persistenceId);

    List<Event> getEventList(@Nullable Integer categoryId);

    Optional<Center> getCenter(Integer centerId);

    Location getRefrencedLocation(Long locationId) throws SQLException;
    Optional<Device> getDevice(String serialNumber);

    long getEventCount();

    long getActiveEventCount();

    List<Long> getEventTypeCount();

    int getWeeklyRegisteredPercentage();

    int getActiveEventPercentage();

    LocationStatusForm getLocationStatusForm(Location location);

    LocationStatus getCurrentLocationStatus(Location location);

    List<Utilizer> deviceUtilizerEventData(Utilizer utilizer);

    List<Center> getCenterList();

    DeviceStatusForm getDeviceStatusForm(Device device);

    DeviceStatus getCurrentDeviceStatus(Device device);

    List<LocationStatusEvent> getLocationEventList(Location baseLocation);

}
