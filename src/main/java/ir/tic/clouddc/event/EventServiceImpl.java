package ir.tic.clouddc.event;

import ir.tic.clouddc.center.*;
import ir.tic.clouddc.document.FileService;
import ir.tic.clouddc.document.MetaData;
import ir.tic.clouddc.log.LogHistory;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.log.Workflow;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.resource.*;
import ir.tic.clouddc.utils.UtilService;
import jakarta.persistence.EntityNotFoundException;
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
    private final CenterService centerService;
    private final PersonService personService;
    private final FileService fileService;
    private final LogService logService;
    private final ResourceService resourceService;
    private static final int General_Event_Category_ID = 1;
    private static final int NewDevice_Installation_EVENT_CATEGORY_ID = 2;
    private static final int LOCATION_UTILIZER_EVENT_CATEGORY_ID = 3;
    private static final int DEVICE_MOVEMENT_EVENT_CATEGORY_ID = 4;
    private static final int DEVICE_UTILIZER_EVENT_CATEGORY_ID = 5;
    private static final int DEVICE_CHECKLIST_EVENT_CATEGORY_ID = 6;


    @Autowired
    public EventServiceImpl(
            EventRepository eventRepository
            , EventDetailRepository eventDetailRepository, EventCategoryRepository eventCategoryRepository
            , CenterService centerService
            , PersonService personService
            , FileService fileService,
            LogService logService, ResourceService resourceService) {
        this.eventRepository = eventRepository;
        this.eventDetailRepository = eventDetailRepository;
        this.eventCategoryRepository = eventCategoryRepository;
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
    public List<ResourceService.DeviceIdSerialCategoryVendor_Projection1> getLocationDeviceList(Long locationId) {
        return resourceService.getLocationDeviceListProjection(locationId);
    }

    @Override
    public List<Location> getLocationListExcept(Long locationId) {
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
    public List<ResourceService.DeviceIdSerialCategoryVendor_Projection1> getNewDeviceList() {
        return resourceService.getNewDeviceList();
    }

    @Override
    public List<Location> getLocationList() {
        return centerService.getLocationList();
    }

    @Override
    public Location getReferencedLocation(Long locationId) throws EntityNotFoundException {
        try {
            return centerService.getRefrencedLocation(locationId);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(e);
        }
    }

    @Override
    public Utilizer getReferencedUtilizer(Integer utilizerId) {
        return resourceService.getReferencedUtilizer(utilizerId);
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
    public void eventRegister(EventForm eventForm, LocalDate validDate) throws IOException {
        switch (eventForm.getEventCategoryId()) {

        /*    case LOCATION_STATUS_EVENT_CATEGORY_ID -> {
                if (!Objects.isNull(locationStatusForm)) {
                    var event = locationStatusEventRegister_2(locationStatusForm);
                    eventDetailRegister(event, locationStatusForm.getFile(), locationStatusForm.getDescription());
                    centerService.updateLocationStatus(locationStatusForm, event);
                }
            }*/

            case General_Event_Category_ID -> generalEventRegister_1(eventForm, validDate);
            case NewDevice_Installation_EVENT_CATEGORY_ID -> newDeviceInstallationEventRegister_2(eventForm, validDate);
            case LOCATION_UTILIZER_EVENT_CATEGORY_ID -> locationUtilizerEventRegister_3(eventForm, validDate);
            case DEVICE_MOVEMENT_EVENT_CATEGORY_ID -> deviceMovementEventRegister_4(eventForm, validDate);
            case DEVICE_UTILIZER_EVENT_CATEGORY_ID -> deviceUtilizerEventRegister_5(eventForm, validDate);

            default -> throw new NoSuchElementException();
        }
    }

    private void generalEventRegister_1(EventForm eventForm, LocalDate validDate) throws IOException {
        GeneralEvent generalEvent = new GeneralEvent();
        generalEvent.setRegisterDate(UtilService.getDATE());
        generalEvent.setRegisterTime(UtilService.getTime());
        generalEvent.setEventDate(validDate);
        generalEvent.setEventCategory(eventCategoryRepository.getReferenceById(eventForm.getEventCategoryId()));
        generalEvent.setGeneralCategoryId(eventForm.getGeneral_category());
        generalEvent.setActive(!eventForm.isGeneral_activation());
        generalEvent.setLocationList(List.of(centerService.getRefrencedLocation(eventForm.getGeneral_locationId())));

        eventPersist(eventForm, generalEvent);
    }

    private void newDeviceInstallationEventRegister_2(EventForm eventForm, LocalDate validDate) throws IOException {
        Utilizer utilizer;
        Location location;

        NewDeviceInstallationEvent newDeviceInstallationEvent = new NewDeviceInstallationEvent();
        newDeviceInstallationEvent.setRegisterDate(UtilService.getDATE());
        newDeviceInstallationEvent.setRegisterTime(UtilService.getTime());
        newDeviceInstallationEvent.setActive(false);
        newDeviceInstallationEvent.setEventDate(validDate);
        newDeviceInstallationEvent.setEventCategory(eventCategoryRepository.getReferenceById(eventForm.getEventCategoryId()));

        if (eventForm.getUtilizer_oldUtilizerId() != null) {
            utilizer = resourceService.getReferencedUtilizer(eventForm.getUtilizer_oldUtilizerId());
            location = centerService.getRefrencedLocation(eventForm.getUtilizer_locationId());
            newDeviceInstallationEvent.setInstallationUtilizer(utilizer);
            newDeviceInstallationEvent.setInstallationLocation(location);
        } else {
            utilizer = resourceService.getReferencedUtilizer(eventForm.getUtilizer_newUtilizerId());
            newDeviceInstallationEvent.setInstallationUtilizer(utilizer);
            var optionalLocation = centerService.getLocation(eventForm.getUtilizer_locationId());
            if (optionalLocation.isPresent()) {
                location = optionalLocation.get();
                if (location instanceof Rack rack) {
                    rack.setUtilizer(utilizer);
                    newDeviceInstallationEvent.setInstallationLocation(rack);
                } else if (location instanceof Room room) {
                    room.setUtilizer(utilizer);
                    newDeviceInstallationEvent.setInstallationLocation(room);
                }
            } else {
                throw new NoSuchElementException();
            }
        }

        List<Device> referencedDeviceList = new ArrayList<>();
        Supplier supplier = resourceService.getReferencedDefaultSupplier();

        for (Integer unassignedDeviceId : eventForm.getUnassignedDeviceIdList()) {
            var unassignedDevice = resourceService.getReferencedUnassignedDevice(unassignedDeviceId);
            Device device;
            switch (unassignedDevice.getDeviceCategory().getCategoryId()) {
                case 5 -> device = new Server();
                case 6 -> device = new Switch();
                case 7 -> device = new Firewall();
                case 8 -> device = new Enclosure();
                default -> throw new NoSuchElementException();
            }
            device.setPriorityDevice(false);
            device.setDeviceCategory(unassignedDevice.getDeviceCategory());
            device.setSerialNumber(unassignedDevice.getSerialNumber());
            device.setUtilizer(utilizer);
            device.setLocation(location);
            device.setSupplier(supplier);

            referencedDeviceList.add(device);
        }

        newDeviceInstallationEvent.setDeviceList(referencedDeviceList);
        newDeviceInstallationEvent.setLocationList(List.of(location)); // new device installed @ this location
        newDeviceInstallationEvent.setUtilizerList(List.of(utilizer)); // new device installed for this utilizer

        Map<Integer, Integer> utilizerBalance = new HashMap<>();
        utilizerBalance.put(utilizer.getId(), referencedDeviceList.size());
        newDeviceInstallationEvent.setUtilizerBalance(utilizerBalance);

        eventPersist(eventForm, newDeviceInstallationEvent);

        resourceService.deleteUnassignedList(eventForm.getUnassignedDeviceIdList());
    }

    private void eventPersist(EventForm eventForm, Event event) throws IOException {
        EventDetail eventDetail;
        eventDetail = eventDetailRegister(event, eventForm);
        var persistedDetail = eventDetailRepository.save(eventDetail);
        if (eventForm.getMultipartFile() != null) {
            fileService.registerAttachment(eventForm.getMultipartFile(), persistedDetail.getPersistence());
        }
    }


    private LocationCheckList locationStatusEventRegister_2(LocationStatusForm locationStatusForm) {
        var currentStatus = locationStatusForm.getCurrentLocationStatus();
        LocationCheckList locationCheckList = new LocationCheckList();
        // locationCheckList.setLocationStatus(currentStatus);
        // locationCheckList.setDoorChanged(currentStatus.isDoor() != locationStatusForm.isDoor());
        // locationCheckList.setVentilationChanged(currentStatus.isVentilation() != locationStatusForm.isVentilation());
        // locationCheckList.setPowerChanged(currentStatus.isPower() != locationStatusForm.isPower());
        //locationCheckList.setLocation(locationStatusForm.getLocation());
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
                locationUtilizerEvent.setLocationList(List.of(rack));
            } else if (location instanceof Room room) {
                room.setUtilizer(newUtilizer);
                locationUtilizerEvent.setLocationList(List.of(room));
            } else {
                throw new NoSuchElementException();
            }

            List<Device> referencedDeviceList = resourceService.getLocationDeviceList(location.getId());

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

            locationUtilizerEvent.setUtilizerBalance(getUtilizerBalanceMap(affectedUtilizerIdList, newUtilizer.getId(), utilizerIdList));

            for (Device device : referencedDeviceList) {
                device.setUtilizer(newUtilizer);
            }
            // many to many for inverse-side reference
            locationUtilizerEvent.setDeviceList(referencedDeviceList);
            locationUtilizerEvent.setLocationList(List.of(location));
            locationUtilizerEvent.setUtilizerList(List.of(newUtilizer, locationUtilizerEvent.getOldUtilizer()));

            eventPersist(eventForm, locationUtilizerEvent);
        } else {
            throw new NoSuchElementException();
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

            // many to many for inverse-side references
            deviceMovementEvent.setDeviceList(referencedDeviceList);
            deviceMovementEvent.setLocationList(List.of(destinationLocation, deviceMovementEvent.getSource()));
            List<Utilizer> utilizerList = new ArrayList<>();
            for (Integer utilizerId : deviceMovementEvent.getUtilizerBalance().keySet()) {
                utilizerList.add(resourceService.getReferencedUtilizer(utilizerId));
            }
            deviceMovementEvent.setUtilizerList(utilizerList);

            eventPersist(eventForm, deviceMovementEvent);
        } else {
            throw new NoSuchElementException();
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

            deviceUtilizerEvent.setLocationList(List.of(device.getLocation()));
            deviceUtilizerEvent.setDeviceList(List.of(device));
            deviceUtilizerEvent.setUtilizerList(List.of(newUtilizer, deviceUtilizerEvent.getOldUtilizer()));

            eventPersist(eventForm, deviceUtilizerEvent);
        } else {
            throw new NoSuchElementException();
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
        event.setEventDetailList(List.of(eventDetail));

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
            loadEventTransients_1(List.of(event));
            loadEventTransients_2(event.getEventDetailList());

            return event;
        }
        throw new NoSuchElementException();
    }

    @Override
    public List<MetaData> getRelatedMetadataList(List<EventDetail> eventDetailList) {
        List<Long> persistenceIdList = eventDetailList
                .stream()
                .map(Workflow::getPersistence)
                .map(Persistence::getId)
                .distinct()
                .toList();

        return fileService.getRelatedMetadataList(persistenceIdList, false);
    }

    @Override
    public List<Event> loadEventTransients_1(List<Event> eventList) {
        for (Event event : eventList) {
            event.setPersianRegisterDate(UtilService.getFormattedPersianDate(event.getRegisterDate()));
            event.setPersianRegisterDayTime(UtilService.getFormattedPersianDayTime(event.getRegisterDate(), event.getRegisterTime()));
        }
        return eventList;
    }

    private void loadEventTransients_2(List<EventDetail> eventDetailList) {
        for (EventDetail eventDetail : eventDetailList
        ) {
            eventDetail.setPersianDate(UtilService.getFormattedPersianDate(eventDetail.getRegisterDate()));
            eventDetail.setPersianDayTime(UtilService.PERSIAN_DAY.get(eventDetail.getRegisterDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault())));
        }
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

