package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Center;
import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.center.LocationStatus;
import ir.tic.clouddc.document.MetaData;
import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.DeviceStatus;
import ir.tic.clouddc.resource.ResourceService;
import ir.tic.clouddc.resource.Utilizer;
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

    Optional<Center> getCenter(Integer centerId);

    Location getRefrencedLocation(Long locationId);
    Optional<Device> getDevice(String serialNumber);

    long getEventCount();

    long getActiveEventCount();

    List<Long> getEventTypeCount();

    int getWeeklyRegisteredPercentage();

    int getActiveEventPercentage();

    LocationStatusForm getLocationStatusForm(Location location);

    LocationStatus getCurrentLocationStatus(Location location);

    List<ResourceService.UtilizerIdNameProjection> getUtilizerEventData_1(Utilizer utilizer);

    List<Center> getCenterList();

    DeviceStatusForm getDeviceStatusForm(Device device);

    DeviceStatus getCurrentDeviceStatus(Device device);

    List<LocationCheckList> getLocationEventList(Location baseLocation);

    List<ResourceService.DeviceIdSerialCategory_Projection1> getLocationDeviceList(Long locationId);

    List<Location> getDeviceMovementEventData_2(Long locationId);

    Optional<Location> getLocation(Long locationId);

    Device getReferencedDevice(Long deviceId);
}
