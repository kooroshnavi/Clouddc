package ir.tic.clouddc.event;

import ir.tic.clouddc.center.*;
import ir.tic.clouddc.document.FileService;
import ir.tic.clouddc.document.MetaData;
import ir.tic.clouddc.log.LogHistory;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.report.ReportService;
import ir.tic.clouddc.resource.*;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Slf4j
@Service
public final class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventDetailRepository eventDetailRepository;
    private final EventCategoryRepository eventCategoryRepository;

    private final LocationStatusEventRepository locationStatusEventRepository;
    private final ReportService reportService;
    private final CenterService centerService;
    private final PersonService personService;
    private final FileService fileService;
    private final LogService logService;
    private final ResourceService resourceService;


    private static final int VISIT_EVENT_CATEGORY_ID = 1;
    private static final int NewDevice_Installation_EVENT_CATEGORY_ID = 2;
    private static final int LOCATION_UTILIZER_EVENT_CATEGORY_ID = 3;
    private static final int DEVICE_MOVEMENT_EVENT_CATEGORY_ID = 4;
    private static final int DEVICE_UTILIZER_EVENT_CATEGORY_ID = 5;
    private static final int DEVICE_CHECKLIST_EVENT_CATEGORY_ID = 6;
    private final ResourceServiceImpl resourceServiceImpl;


    @Autowired
    public EventServiceImpl(
            EventRepository eventRepository
            , EventDetailRepository eventDetailRepository, EventCategoryRepository eventCategoryRepository, LocationStatusEventRepository locationStatusEventRepository, ReportService reportService
            , CenterService centerService
            , PersonService personService
            , FileService fileService,
            LogService logService, ResourceService resourceService, ResourceServiceImpl resourceServiceImpl) {
        this.eventRepository = eventRepository;
        this.eventDetailRepository = eventDetailRepository;
        this.eventCategoryRepository = eventCategoryRepository;
        this.locationStatusEventRepository = locationStatusEventRepository;

        this.reportService = reportService;
        this.centerService = centerService;
        this.personService = personService;
        this.resourceService = resourceService;
        this.fileService = fileService;
        this.logService = logService;
        this.resourceServiceImpl = resourceServiceImpl;
    }


    @Override
    public LocationStatusForm getLocationStatusForm(Location location) {
        LocationStatusForm locationStatusForm = new LocationStatusForm();
        locationStatusForm.setLocation(location);
        return locationStatusForm;
    }

    @Override
    public DeviceStatusForm getDeviceStatusForm(Device device) {
        DeviceStatusForm deviceStatusForm = new DeviceStatusForm();
        deviceStatusForm.setDevice(device);
        return deviceStatusForm;
    }

    @Override
    public DeviceStatus getCurrentDeviceStatus(Device device) {
        return resourceService.getCurrentDeviceStatus(device);
    }

    @Override
    public List<LocationCheckList> getLocationEventList(Location baseLocation) {

        return locationStatusEventRepository.findAllByLocation(baseLocation);
    }

    @Override
    public List<ResourceService.DeviceIdSerialCategory_Projection1> getLocationDeviceList(Long locationId) {
        return resourceService.getLocationDeviceList(locationId);
    }

    @Override
    public List<Location> getDeviceMovementEventData_2(Long locationId) {
        return centerService.getLocationListExcept(List.of(locationId));
    }

    @Override
    public Optional<Location> getLocation(Long locationId) {
        return centerService.getLocation(locationId);
    }

    @Override
    public Device getReferencedDevice(Long deviceId) {
        return resourceService.getReferencedDevice(deviceId);
    }

    @Override
    public List<ResourceService.DeviceIdSerialCategory_Projection1> getNewDeviceList() {
        return resourceService.getNewDeviceList();
    }

    @Override
    public LocationStatus getCurrentLocationStatus(Location location) {
        return centerService.getCurrentLocationStatus(location);
    }

    @Override
    public List<ResourceService.UtilizerIdNameProjection> getUtilizerList(List<Integer> utilizerIdList) {
        return resourceService.getUtilizerListExcept(utilizerIdList);
    }

    @Override
    public List<Center> getCenterList() {
        return centerService.getCenterList();
    }

    @Override
    public void eventRegister(EventForm eventForm, LocalDate validDate) throws IOException {
        Event event;
        EventDetail eventDetail;

        switch (eventForm.getEventCategoryId()) {
          /*  case VISIT_EVENT_CATEGORY_ID -> {
                var event = visitEventRegister_1(eventLandingForm);
                eventDetailRegister(event, eventLandingForm.getFile(), eventLandingForm.getDescription());
                eventRepository.save(event);
            }*/
        /*    case LOCATION_STATUS_EVENT_CATEGORY_ID -> {
                if (!Objects.isNull(locationStatusForm)) {
                    var event = locationStatusEventRegister_2(locationStatusForm);
                    eventDetailRegister(event, locationStatusForm.getFile(), locationStatusForm.getDescription());
                    centerService.updateLocationStatus(locationStatusForm, event);
                }
            }*/
            case NewDevice_Installation_EVENT_CATEGORY_ID -> {
                newDeviceInstallationEventRegister_2(eventForm, validDate);
            }

            case LOCATION_UTILIZER_EVENT_CATEGORY_ID -> {
                locationUtilizerEventRegister_3(eventForm, validDate);
            }

            case DEVICE_MOVEMENT_EVENT_CATEGORY_ID -> {
                deviceMovementEventRegister_4(eventForm, validDate);

            }
            case DEVICE_UTILIZER_EVENT_CATEGORY_ID -> {
                deviceUtilizerEventRegister_5(eventForm, validDate);
            }
            default -> throw new NoSuchElementException();
        }

    }

    private void newDeviceInstallationEventRegister_2(EventForm eventForm, LocalDate validDate) {

    }

    private void eventPersist(EventForm eventForm, Event event) throws IOException {
        EventDetail eventDetail;
        eventDetail = eventDetailRegister(event, eventForm);
        var persistedDetail = eventDetailRepository.save(eventDetail);
        if (eventForm.getMultipartFile() != null) {
            fileService.registerAttachment(eventForm.getMultipartFile(), persistedDetail.getPersistence());
        }
    }

    private VisitEvent visitEventRegister_1(EventLandingForm eventLandingForm) {
        VisitEvent visitEvent = new VisitEvent();
        visitEvent.setRegisterDate(UtilService.getDATE());
        visitEvent.setRegisterTime(UtilService.getTime());
        visitEvent.setEventCategory(eventCategoryRepository.findById(eventLandingForm.getEventCategoryId()).get());
        visitEvent.setCenter(eventLandingForm.getCenter());
        visitEvent.setActive(false);

        return visitEvent;
    }

    private LocationCheckList locationStatusEventRegister_2(LocationStatusForm locationStatusForm) {
        var currentStatus = locationStatusForm.getCurrentLocationStatus();
        LocationCheckList locationCheckList = new LocationCheckList();
        locationCheckList.setLocationStatus(currentStatus);
        locationCheckList.setDoorChanged(currentStatus.isDoor() != locationStatusForm.isDoor());
        locationCheckList.setVentilationChanged(currentStatus.isVentilation() != locationStatusForm.isVentilation());
        locationCheckList.setPowerChanged(currentStatus.isPower() != locationStatusForm.isPower());
        locationCheckList.setLocation(locationStatusForm.getLocation());
        locationCheckList.setActive(false);

        return locationCheckList;
    }

    private void locationUtilizerEventRegister_3(EventForm eventForm, LocalDate validDate) throws IOException {
        var optionalLocation = centerService.getLocation(eventForm.getUtilizer_locationId());
        var newUtilizer = resourceService.getReferencedUtilizer(eventForm.getUtilizer_newUtilizerId());

        LocationUtilizerEvent locationUtilizerEvent = new LocationUtilizerEvent();
        locationUtilizerEvent.setRegisterDate(UtilService.getDATE());
        locationUtilizerEvent.setRegisterTime(UtilService.getTime());
        locationUtilizerEvent.setOldUtilizer(resourceService.getReferencedUtilizer(eventForm.getUtilizer_oldUtilizerId()));
        locationUtilizerEvent.setNewUtilizer(newUtilizer);
        locationUtilizerEvent.setActive(false);
        locationUtilizerEvent.setEventDate(validDate);
        locationUtilizerEvent.setEventCategory(eventCategoryRepository.getReferenceById(eventForm.getEventCategoryId()));

        if (optionalLocation.isPresent()) {
            var location = optionalLocation.get();
            if (location instanceof Rack rack) {
                rack.setUtilizer(newUtilizer);
                locationUtilizerEvent.setLocation(rack);
            } else if (location instanceof Room room) {
                room.setUtilizer(newUtilizer);
                locationUtilizerEvent.setLocation(room);
            }

            List<ResourceService.DeviceIdUtilizerId_Projection2> referencedDeviceList = resourceService.getDeviceProjection2(location.getId());

            List<Integer> affectedUtilizerIdList = referencedDeviceList
                    .stream()
                    .map(ResourceService.DeviceIdUtilizerId_Projection2::getDeviceUtilizerId)
                    .distinct()
                    .toList();

            List<Integer> utilizerIdList = referencedDeviceList
                    .stream()
                    .map(ResourceService.DeviceIdUtilizerId_Projection2::getDeviceUtilizerId)
                    .toList();

            locationUtilizerEvent.setUtilizerBalance(getUtilizerBalanceMap(affectedUtilizerIdList, newUtilizer.getId(), utilizerIdList));

            List<Long> deviceIdList = referencedDeviceList
                    .stream()
                    .map(ResourceService.DeviceIdUtilizerId_Projection2::getDeviceId)
                    .toList();

            eventPersist(eventForm, locationUtilizerEvent);

            resourceService.updateDeviceUtilizer(deviceIdList, newUtilizer);
        }
        // event persist

        //    centerService.updateLocationUtilizer(location.getId(), newUtilizer);
    }

    private void deviceMovementEventRegister_4(EventForm eventForm, LocalDate validDate) throws IOException {
        var optionalLocation = centerService.getLocation(eventForm.getDeviceMovement_destLocId());
        Utilizer destinationUtilizer;

        if (optionalLocation.isPresent()) {
            var destinationLocation = optionalLocation.get();

            if (destinationLocation instanceof Rack rack) {
                destinationUtilizer = rack.getUtilizer();
            } else if (destinationLocation instanceof Room room) {
                destinationUtilizer = room.getUtilizer();
            } else {
                throw new NoSuchElementException();
            }

            DeviceMovementEvent deviceMovementEvent = new DeviceMovementEvent();
            deviceMovementEvent.setRegisterDate(UtilService.getDATE());
            deviceMovementEvent.setRegisterTime(UtilService.getTime());
            deviceMovementEvent.setEventCategory(eventCategoryRepository.getReferenceById(eventForm.getEventCategoryId()));
            deviceMovementEvent.setActive(false);
            deviceMovementEvent.setEventDate(validDate);
            deviceMovementEvent.setSource(centerService.getRefrencedLocation(eventForm.getDeviceMovement_sourceLocId()));
            deviceMovementEvent.setDestination(destinationLocation);

            // 1. referencedDeviceList
            List<Device> referencedDeviceList = new ArrayList<>();
            for (Long deviceId : eventForm.getDeviceIdList()) {
                var optionalDevice = resourceService.getDevice(deviceId);
                if (optionalDevice.isPresent()) {
                    var device = optionalDevice.get();
                    referencedDeviceList.add(device);
                }
            }

            // 2. affectedUtilizerIdList
            List<Integer> affectedUtilizerIdList = referencedDeviceList
                    .stream()
                    .map(Device::getUtilizer)
                    .map(Utilizer::getId)
                    .distinct()
                    .toList();
            List<Integer> utilizerIdList = referencedDeviceList
                    .stream()
                    .map(Device::getUtilizer)
                    .map(Utilizer::getId)
                    .toList();

            deviceMovementEvent.setUtilizerBalance(getUtilizerBalanceMap(affectedUtilizerIdList, destinationUtilizer.getId(), utilizerIdList));

            for (Device device : referencedDeviceList) {
                device.setLocation(destinationLocation);
                device.setUtilizer(destinationUtilizer);
            }
            deviceMovementEvent.setDeviceList(referencedDeviceList);

   /*     List<Long> deviceIdList = referencedDeviceList
                .stream()
                .map(Device::getId)
                .toList();*/

            eventPersist(eventForm, deviceMovementEvent);
        }
        //     resourceService.updateDeviceLocation(deviceIdList, destinationUtilizer, destinationLocation);
    }

    private void deviceUtilizerEventRegister_5(EventForm eventForm, LocalDate validDate) throws IOException {
        var newUtilizer = resourceService.getReferencedUtilizer(eventForm.getUtilizer_newUtilizerId());
        var optionalDevice = resourceService.getDevice(eventForm.getUtilizer_deviceId());

        if (optionalDevice.isPresent()) {
            var device = optionalDevice.get();
            DeviceUtilizerEvent deviceUtilizerEvent = new DeviceUtilizerEvent();
            deviceUtilizerEvent.setRegisterDate(UtilService.getDATE());
            deviceUtilizerEvent.setRegisterTime(UtilService.getTime());
            deviceUtilizerEvent.setEventCategory(eventCategoryRepository.getReferenceById(eventForm.getEventCategoryId()));
            deviceUtilizerEvent.setActive(false);
            deviceUtilizerEvent.setOldUtilizer(resourceService.getReferencedUtilizer(eventForm.getUtilizer_oldUtilizerId()));
            deviceUtilizerEvent.setNewUtilizer(newUtilizer);
            deviceUtilizerEvent.setEventDate(validDate);
            List<Integer> affectedUtilizerIdList = List.of(device.getUtilizer().getId());
            deviceUtilizerEvent.setUtilizerBalance(getUtilizerBalanceMap(affectedUtilizerIdList, newUtilizer.getId(), affectedUtilizerIdList));
            device.setUtilizer(newUtilizer);
            deviceUtilizerEvent.setDevice(device);

            eventPersist(eventForm, deviceUtilizerEvent);
        }

    }

    private static Map<Integer, Integer> getUtilizerBalanceMap(List<Integer> affectedUtilizerIdList, Integer newUtilizerId, List<Integer> utilizerIdList) {
        // 3. init utilizerBalance Map
        Map<Integer, Integer> utilizerBalance = new HashMap<>();
        for (Integer utilizerId : affectedUtilizerIdList) {
            utilizerBalance.put(utilizerId, 0);
        }

        if (affectedUtilizerIdList.stream().noneMatch(UtilizerId -> Objects.equals(UtilizerId, newUtilizerId))) {
            utilizerBalance.put(newUtilizerId, 0);
        }

        // 4. update utilizerBalance
        for (Integer utilizerId : utilizerIdList) {
            if (!Objects.equals(utilizerId, newUtilizerId)) {
                var currentBalance = utilizerBalance.get(utilizerId);
                currentBalance -= 1;
                utilizerBalance.put(utilizerId, currentBalance);

                var destBalance = utilizerBalance.get(newUtilizerId);
                destBalance += 1;
                utilizerBalance.put(newUtilizerId, destBalance);
            }
        }
        return utilizerBalance;
    }


    private EventDetail eventDetailRegister(Event event, EventForm eventForm) throws IOException {
        EventDetail eventDetail = new EventDetail();
        eventDetail.setRegisterDate(event.getRegisterDate());
        eventDetail.setRegisterTime(event.getRegisterTime());
        var persistence = getPersistence();
        eventDetail.setPersistence(persistence);
        eventDetail.setDescription(eventForm.getDescription());
        eventDetail.setEvent(event);
        event.setEventDetail(eventDetail);

        return eventDetail;
    }

    private Persistence getPersistence() {
        var currentPerson = personService.getCurrentPerson();
        var persistence = logService.persistenceSetup(currentPerson);
        LogHistory logHistory = new LogHistory(UtilService.getDATE(), UtilService.getTime(), currentPerson, persistence, UtilService.LOG_MESSAGE.get("EventRegister"), true);
        persistence.setLogHistoryList(List.of(logHistory));

        return persistence;
    }

    @Override
    public List<Event> getEventList() {
        List<Event> eventList;
        eventList = eventRepository.getEventList();
        for (Event event : eventList) {
            event.setPersianRegisterDate(UtilService.getFormattedPersianDate(event.getRegisterDate()));
            event.setPersianRegisterDayTime(UtilService.PERSIAN_DAY.get(event.getRegisterDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault())) + " - " + event.getRegisterTime());
        }
        return eventList;
    }

    @Override
    public Event getEventHistory(Long eventId) {
        var optionalEvent = eventRepository.findById(eventId);
        Event event;
        if (optionalEvent.isPresent()) {
            event = optionalEvent.get();
            event.setPersianRegisterDate(UtilService.getFormattedPersianDate(event.getRegisterDate()));
            event.setPersianRegisterDayTime(UtilService.getFormattedPersianDayTime(event.getRegisterDate(), event.getRegisterTime()));
            loadEventDetailTransients(event.getEventDetail());

            return event;
        }
        return null;
    }

    @Override
    public MetaData getRelatedMetadata(Long persistenceId) {
        var metadata = fileService.getRelatedMetadataList(List.of(persistenceId), false);
        if (!metadata.isEmpty()) {
            return metadata.get(0);
        }
        return null;
    }

    private void loadEventDetailTransients(EventDetail eventDetail) {
        eventDetail.setPersianDate(UtilService.getFormattedPersianDate(eventDetail.getRegisterDate()));
        eventDetail.setPersianDayTime(UtilService.PERSIAN_DAY.get(eventDetail.getRegisterDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault())));
    }

    @Override
    public List<EventCategory> getEventCategoryList() {
        return (List<EventCategory>) eventCategoryRepository.findAll();
    }

    @Override
    public List<CenterService.CenterIdNameProjection> getCenterIdAndNameList() {
        return centerService.getCenterIdAndNameList();
    }

    @Override
    public Model modelForEventController(Model model) {
        model.addAttribute("person", personService.getCurrentPerson());
        model.addAttribute("date", UtilService.getCurrentDate());
        return model;
    }

    @Override
    public Optional<Center> getCenter(Integer centerId) {
        return centerService.getCenter(centerId);
    }

    @Override
    public Location getRefrencedLocation(Long locationId) {
        return centerService.getRefrencedLocation(locationId);
    }

    @Override
    public Optional<Device> getDevice(String serialNumber) {
        return resourceService.getDeviceBySerialNumber(serialNumber);
    }

    @Override
    public long getEventCount() {
        return eventRepository.count();
    }

    @Override
    public long getActiveEventCount() {
        return eventRepository.getActiveEventCount(true);
    }

    @Override
    public List<Long> getEventTypeCount() {
        return null;
    }

    @Override
    public int getWeeklyRegisteredPercentage() {
        DecimalFormat decimalFormat = new DecimalFormat("###");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        var percent = (float) 25 * 100;
        log.info(String.valueOf(percent));
        var formatted = decimalFormat.format(percent);
        return Integer.parseInt(formatted);
    }

    @Override
    public int getActiveEventPercentage() {
        DecimalFormat decimalFormat = new DecimalFormat("###");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        float percent = ((float) getActiveEventCount() / getEventCount()) * 100;
        var formatted = decimalFormat.format(percent);
        return Integer.parseInt(formatted);
    }
}

