package ir.tic.clouddc.event;

import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.center.LocationStatus;
import ir.tic.clouddc.document.MetaData;
import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.ResourceService;
import org.springframework.ui.Model;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventService {


    void eventRegister(EventForm eventForm, LocalDate validDate) throws IOException;

    List<EventCategory> getEventCategoryList();

    List<CenterService.CenterIdNameProjection> getCenterIdAndNameList();

    Model modelForEventController(Model model);

    Event getEventHistory(Long eventId);

    MetaData getRelatedMetadata(Long persistenceId);

    List<Event> getEventList();

    long getEventCount();

    long getActiveEventCount();

    List<Long> getEventTypeCount();

    int getWeeklyRegisteredPercentage();

    int getActiveEventPercentage();

    LocationStatusForm getLocationStatusForm(Location location);

    LocationStatus getCurrentLocationStatus(Location location);

    List<ResourceService.UtilizerIdNameProjection> getUtilizerList(List<Integer> utilizerIdList);

    List<ResourceService.DeviceIdSerialCategoryVendor_Projection1> getLocationDeviceList(Long locationId);

    List<Location> getLocationListExcept(Long locationId);

    Optional<Location> getLocation(Long locationId);

    Device getReferencedDevice(Long deviceId);

    List<ResourceService.DeviceIdSerialCategoryVendor_Projection1> getNewDeviceList();

    List<Location> getLocationList();
}
