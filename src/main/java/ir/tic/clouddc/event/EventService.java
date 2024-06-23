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
import java.util.List;
import java.util.Optional;

public interface EventService {


    void eventSetup(EventLandingForm eventLandingForm
            , @Nullable DeviceStatusForm deviceStatusForm
            , @Nullable LocationStatusForm locationStatusForm) throws IOException;


    List<EventCategory> getEventCategoryList();

    List<CenterService.CenterIdNameProjection> getCenterIdAndNameList();

    Model modelForEventController(Model model);

    Event getEventHistory(int eventId);

    MetaData getRelatedMetadata(long persistenceId);

    List<Event> getEventList(@Nullable Short categoryId);

    Optional<Center> getCenter(short centerId);

    Optional<Location> getLocation(int locationId);
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
}
