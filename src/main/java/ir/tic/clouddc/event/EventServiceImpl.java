package ir.tic.clouddc.event;

import ir.tic.clouddc.center.*;
import ir.tic.clouddc.document.FileService;
import ir.tic.clouddc.document.MetaData;
import ir.tic.clouddc.log.LogHistory;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.report.ReportService;
import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.DeviceStatus;
import ir.tic.clouddc.resource.ResourceService;
import ir.tic.clouddc.resource.Utilizer;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.IOException;
import java.math.RoundingMode;
import java.sql.SQLException;
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
    private static final int LOCATION_STATUS_EVENT_CATEGORY_ID = 2;
    private static final int DEVICE_UTILIZER_EVENT_CATEGORY_ID = 3;
    private static final int DEVICE_MOVEMENT_EVENT_CATEGORY_ID = 4;
    private static final int DEVICE_STATUS_EVENT_CATEGORY_ID = 5;


    @Autowired
    public EventServiceImpl(
            EventRepository eventRepository
            , EventDetailRepository eventDetailRepository, EventCategoryRepository eventCategoryRepository, LocationStatusEventRepository locationStatusEventRepository, ReportService reportService
            , CenterService centerService
            , PersonService personService
            , FileService fileService, LogService logService, ResourceService resourceService) {
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
    public List<LocationStatusEvent> getLocationEventList(Location baseLocation) {

        return locationStatusEventRepository.findAllByLocation(baseLocation);
    }

    @Override
    public List<ResourceService.DeviceIdSerialCategoryProjection> getLocationDeviceList(Long locationId) {
        return resourceService.getLocationDeviceList(locationId);
    }

    @Override
    public List<Location> getDeviceMovementEventData_2(Long locationId) {
        return centerService.getLocationListExcept(List.of(locationId));
    }

    @Override
    public LocationStatus getCurrentLocationStatus(Location location) {
        return centerService.getCurrentLocationStatus(location);
    }

    @Override
    public List<ResourceService.UtilizerIdNameProjection> getUtilizerEventData_1(Utilizer utilizer) {
        return resourceService.getUtilizerListExcept(utilizer);
    }

    @Override
    public List<Center> getCenterList() {
        return centerService.getCenterList();
    }

    @Override
    public void eventRegister(EventForm eventForm, LocalDate validDate) throws IOException, SQLException {

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
        /*    case DEVICE_UTILIZER_EVENT_CATEGORY_ID -> {
                var event = deviceUtilizerEventRegister_3(eventLandingForm);
                eventDetailRegister(event, eventLandingForm.getFile(), eventLandingForm.getDescription());
                eventRepository.save(event);
                resourceService.updateDeviceUtilizer(event);
            }*/
            case DEVICE_MOVEMENT_EVENT_CATEGORY_ID -> {
                var event = deviceMovementEventRegister_4(eventForm, validDate);
                var eventDetail = eventDetailRegister(event, eventForm);
                var persistedDetail = eventDetailRepository.save(eventDetail);

                if (eventForm.getMultipartFile() != null) {
                    fileService.registerAttachment(eventForm.getMultipartFile(), persistedDetail.getPersistence());
                }
            }
            case DEVICE_STATUS_EVENT_CATEGORY_ID -> {/*
                if (!Objects.isNull(deviceStatusForm)) {
                    var event = deviceStatusEventRegister_5(deviceStatusForm);
                    eventDetailRegister(event, deviceStatusForm.getFile(), deviceStatusForm.getDescription());
                    resourceService.updateDeviceStatus(deviceStatusForm, event);
                }
            }*/
            }

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

    private LocationStatusEvent locationStatusEventRegister_2(LocationStatusForm locationStatusForm) {
        var currentStatus = locationStatusForm.getCurrentLocationStatus();
        LocationStatusEvent locationStatusEvent = new LocationStatusEvent();
        locationStatusEvent.setLocationStatus(currentStatus);
        locationStatusEvent.setDoorChanged(currentStatus.isDoor() != locationStatusForm.isDoor());
        locationStatusEvent.setVentilationChanged(currentStatus.isVentilation() != locationStatusForm.isVentilation());
        locationStatusEvent.setPowerChanged(currentStatus.isPower() != locationStatusForm.isPower());
        locationStatusEvent.setLocation(locationStatusForm.getLocation());
        locationStatusEvent.setActive(false);

        return locationStatusEvent;
    }

    private DeviceUtilizerEvent deviceUtilizerEventRegister_3(EventLandingForm eventLandingForm) {
        DeviceUtilizerEvent deviceUtilizerEvent = new DeviceUtilizerEvent();
        deviceUtilizerEvent.setRegisterDate(UtilService.getDATE());
        deviceUtilizerEvent.setRegisterTime(UtilService.getTime());
        deviceUtilizerEvent.setOldUtilizer(eventLandingForm.getDevice().getUtilizer());
        deviceUtilizerEvent.setNewUtilizer(resourceService.getUtilizer(eventLandingForm.getUtilizerId()));
        deviceUtilizerEvent.setDevice(eventLandingForm.getDevice());
        deviceUtilizerEvent.setActive(false);
        deviceUtilizerEvent.setEventCategory(eventCategoryRepository.findById(eventLandingForm.getEventCategoryId()).get());

        return deviceUtilizerEvent;
    }

    private DeviceMovementEvent deviceMovementEventRegister_4(EventForm eventForm, LocalDate validDate) {
        var optionalLocation = centerService.getLocation(eventForm.getDeviceMovement_destLocId());
        Utilizer destinationUtilizer;
        if (optionalLocation.get() instanceof Rack rack) {
            destinationUtilizer = rack.getUtilizer();
        } else {
            destinationUtilizer = ((Room) optionalLocation.get()).getUtilizer();
        }
        var destinationLocation = optionalLocation.get();

        DeviceMovementEvent deviceMovementEvent = new DeviceMovementEvent();
        deviceMovementEvent.setRegisterDate(UtilService.getDATE());
        deviceMovementEvent.setRegisterTime(UtilService.getTime());
        deviceMovementEvent.setEventCategory(eventCategoryRepository.getReferenceById(eventForm.getEventCategoryId()));
        deviceMovementEvent.setActive(false);
        deviceMovementEvent.setMovementDate(validDate);
        deviceMovementEvent.setSource(centerService.getRefrencedLocation(eventForm.getDeviceMovement_sourceLocId()));
        deviceMovementEvent.setDestination(destinationLocation);

        // 1. referencedDeviceList
        List<Device> referencedDeviceList = new ArrayList<>();
        for (Long deviceId : eventForm.getDeviceMovement_deviceIdList()) {
            referencedDeviceList.add(resourceService.getReferencedDevice(deviceId));
        }
        deviceMovementEvent.setDeviceList(referencedDeviceList);

        // 2. affectedUtilizerList
        List<Utilizer> affectedUtilizerList = referencedDeviceList
                .stream()
                .map(Device::getUtilizer)
                .distinct()
                .toList();


        // 3. init utilizerBalance Map
        Map<Integer, Integer> utilizerBalance = new HashMap<>();
        for (Utilizer activeUtilizer : affectedUtilizerList) {
            utilizerBalance.put(activeUtilizer.getId(), 0);
        }

        if (affectedUtilizerList.stream().noneMatch(Utilizer -> Objects.equals(Utilizer.getId(), destinationUtilizer.getId()))) {
            utilizerBalance.put(destinationUtilizer.getId(), 0);
        }

        // 4. update utilizerBalance
        for (Device device : referencedDeviceList) {
            if (!Objects.equals(device.getUtilizer().getId(), destinationUtilizer.getId())) {
                var currentBalance = utilizerBalance.get(device.getUtilizer().getId());
                currentBalance -= 1;
                utilizerBalance.put(device.getUtilizer().getId(), currentBalance);

                var destBalance = utilizerBalance.get(destinationUtilizer.getId());
                destBalance += 1;
                utilizerBalance.put(destinationUtilizer.getId(), destBalance);
            }
        }

        deviceMovementEvent.setUtilizerBalance(utilizerBalance);

        resourceService.updateDeviceLocation(deviceMovementEvent, destinationUtilizer);

        return deviceMovementEvent;
    }

    private DeviceStatusEvent deviceStatusEventRegister_5(DeviceStatusForm deviceStatusForm) {
        DeviceStatusEvent deviceStatusEvent = new DeviceStatusEvent();
        deviceStatusEvent.setRegisterDate(UtilService.getDATE());
        deviceStatusEvent.setRegisterTime(UtilService.getTime());
        deviceStatusEvent.setEventCategory(eventCategoryRepository.findById(deviceStatusForm.getEventCategoryId()).get());
        deviceStatusEvent.setDevice(deviceStatusForm.getDevice());
        deviceStatusEvent.setActive(false);

        return deviceStatusEvent;
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
    public List<Event> getEventList(Integer categoryId) {
        List<Event> eventList;
        var category = eventCategoryRepository.getReferenceById(categoryId);
        eventList = eventRepository.findAllByEventCategory(category, Sort.by("registerDate"));
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

