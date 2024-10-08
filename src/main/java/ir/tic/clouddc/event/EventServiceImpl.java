package ir.tic.clouddc.event;

import ir.tic.clouddc.center.*;
import ir.tic.clouddc.document.FileService;
import ir.tic.clouddc.document.MetaData;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.log.Workflow;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.resource.*;
import ir.tic.clouddc.utils.UtilService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class EventServiceImpl implements EventService {

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
    public List<ResourceService.UtilizerIdNameProjection> getUtilizerList(List<Integer> utilizerIdList) {
        return resourceService.getUtilizerListExcept(utilizerIdList);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISOR', 'OPERATOR')")
    public void registerEvent(EventForm eventForm, LocalDate validDate) throws IOException {
        Event event;
        switch (eventForm.getEventCategoryId()) {
        /*    case LOCATION_STATUS_EVENT_CATEGORY_ID -> {
                if (!Objects.isNull(locationStatusForm)) {
                    var event = locationStatusEventRegister_2(locationStatusForm);
                    eventDetailRegister(event, locationStatusForm.getFile(), locationStatusForm.getDescription());
                    centerService.updateLocationStatus(locationStatusForm, event);
                }
            }*/

            case General_Event_Category_ID -> event = generalEventRegister_1(eventForm); // No balance

            case NewDevice_Installation_EVENT_CATEGORY_ID ->
                    event = newDeviceInstallationEventRegister_2(eventForm); // balance: +

            case LOCATION_UTILIZER_EVENT_CATEGORY_ID ->
                    event = locationUtilizerEventRegister_3(eventForm); // balance being checked on every event

            case DEVICE_MOVEMENT_EVENT_CATEGORY_ID ->
                    event = deviceMovementEventRegister_4(eventForm); // balance being checked on every event

            case DEVICE_UTILIZER_EVENT_CATEGORY_ID ->
                    event = deviceUtilizerEventRegister_5(eventForm); // balance: -1 -> +1

            default -> throw new NoSuchElementException();
        }

        finalizeEvent(eventForm, validDate, event);
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

    private Event generalEventRegister_1(EventForm eventForm) {
        GeneralEvent generalEvent = new GeneralEvent();
        generalEvent.setGeneralCategoryId(eventForm.getGeneral_category());
        generalEvent.setLocationList(List.of(centerService.getRefrencedLocation(eventForm.getGeneral_locationId())));

        return generalEvent;
    }

    private Event newDeviceInstallationEventRegister_2(EventForm eventForm) {
        Utilizer utilizer;
        Location location = Hibernate.unproxy(centerService.getRefrencedLocation(eventForm.getUtilizer_locationId()), Location.class);

        NewDeviceInstallationEvent newDeviceInstallationEvent = new NewDeviceInstallationEvent();

        if (eventForm.getUtilizer_oldUtilizerId() != null) {
            utilizer = resourceService.getReferencedUtilizer(eventForm.getUtilizer_oldUtilizerId());
        } else {
            utilizer = resourceService.getReferencedUtilizer(eventForm.getUtilizer_newUtilizerId());
            if (location instanceof Rack rack) {
                rack.setUtilizer(utilizer);
            } else if (location instanceof Room room) {
                room.setUtilizer(utilizer);
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

        if (location instanceof Rack rack) { // updates device position
            destinationRackPositionUpdate(rack, referencedDeviceList);
        }

        newDeviceInstallationEvent.setDeviceList(referencedDeviceList);
        newDeviceInstallationEvent.setLocationList(List.of(location)); // new device installed @ this location
        newDeviceInstallationEvent.setUtilizerList(List.of(utilizer)); // new device installed for this utilizer

        Map<Integer, Integer> utilizerBalance = new HashMap<>();
        utilizerBalance.put(utilizer.getId(), referencedDeviceList.size());
        newDeviceInstallationEvent.setUtilizerBalance(utilizerBalance);

        return newDeviceInstallationEvent;
    }

    private Event locationUtilizerEventRegister_3(EventForm eventForm) {
        var optionalLocation = centerService.getLocation(eventForm.getUtilizer_locationId());
        var newUtilizer = resourceService.getReferencedUtilizer(eventForm.getUtilizer_newUtilizerId());
        var oldUtilizer = resourceService.getReferencedUtilizer(eventForm.getUtilizer_oldUtilizerId());

        LocationUtilizerEvent locationUtilizerEvent = new LocationUtilizerEvent();

        if (optionalLocation.isPresent()) {
            var location = optionalLocation.get();
            if (location instanceof Rack rack) {
                rack.setUtilizer(newUtilizer);
            } else if (location instanceof Room room) {
                room.setUtilizer(newUtilizer);
            } else {
                throw new NoSuchElementException();
            }

            List<Utilizer> utilizerList = new ArrayList<>();
            utilizerList.add(oldUtilizer);
            utilizerList.add(newUtilizer);

            List<Device> referencedDeviceList = resourceService.getLocationDeviceList(location.getId());

            if (!referencedDeviceList.isEmpty()) {

                List<Integer> affectedUtilizerIdList = referencedDeviceList
                        .stream()
                        .map(Device::getUtilizer)
                        .map(Utilizer::getId)
                        .distinct()
                        .toList();

                boolean hasBalance;
                hasBalance = isBalance(newUtilizer, affectedUtilizerIdList);

                if (hasBalance) {
                    List<Integer> utilizerIdList = referencedDeviceList
                            .stream()
                            .map(Device::getUtilizer)
                            .map(Utilizer::getId)
                            .toList();

                    locationUtilizerEvent.setUtilizerBalance(setupUtilizerBalanceMap(affectedUtilizerIdList, newUtilizer.getId(), utilizerIdList));

                    Iterator<Device> deviceIterator = referencedDeviceList.iterator();
                    while (deviceIterator.hasNext()) {
                        var device = deviceIterator.next();
                        if (Objects.equals(device.getUtilizer().getId(), newUtilizer.getId())) {
                            deviceIterator.remove();
                        } else {
                            device.setUtilizer(newUtilizer);
                        }
                    }
                    locationUtilizerEvent.setDeviceList(referencedDeviceList);

                    List<Integer> finalUtilizerIdList = utilizerList
                            .stream()
                            .map(Utilizer::getId)
                            .toList();

                    for (Integer utilizerId : locationUtilizerEvent.getUtilizerBalance().keySet()) {
                        if (!finalUtilizerIdList.contains(utilizerId)) {
                            utilizerList.add(resourceService.getReferencedUtilizer(utilizerId));
                        }
                    }
                }
            }

            // many to many for inverse-side reference - Holds important event data
            locationUtilizerEvent.setLocationList(List.of(location));
            locationUtilizerEvent.setUtilizerList(utilizerList);

            return locationUtilizerEvent;

        } else {
            throw new NoSuchElementException();
        }
    }

    private Event deviceMovementEventRegister_4(EventForm eventForm) {
        var sourceLocation = Hibernate.unproxy(centerService.getRefrencedLocation(eventForm.getDeviceMovement_sourceLocId()), Location.class);
        var destinationLocation = Hibernate.unproxy(centerService.getRefrencedLocation(eventForm.getDeviceMovement_destLocId()), Location.class);
        Utilizer destinationUtilizer;

        if (destinationLocation instanceof Rack rack) {
            destinationUtilizer = rack.getUtilizer();
        } else if (destinationLocation instanceof Room room) {
            destinationUtilizer = room.getUtilizer();
        } else {
            throw new NoSuchElementException();
        }

        DeviceMovementEvent deviceMovementEvent = new DeviceMovementEvent();

        // 1. referencedDeviceList
        List<Device> referencedDeviceList = new ArrayList<>();
        for (Long deviceId : eventForm.getDeviceIdList()) {
            var optionalDevice = resourceService.getDevice(deviceId);
            if (optionalDevice.isPresent()) {
                var device = optionalDevice.get();
                referencedDeviceList.add(device);
            }
        }

        // Source Rack: Update Positions
        if (sourceLocation instanceof Rack sourceRack) {
            // 1. Remove operation
            Iterator<Map.Entry<Integer, Device>> entryIterator = sourceRack.getDevicePositionMap().entrySet().iterator();
            while (entryIterator.hasNext()) {
                Map.Entry<Integer, Device> entry = entryIterator.next();
                var device = entry.getValue();
                if (referencedDeviceList.contains(device)) {
                    entryIterator.remove();
                }
            }

            // 2. Update key position
            if (!sourceRack.getDevicePositionMap().isEmpty()) {
                Map<Integer, Device> newDeviceMap = new HashMap<>();
                int newPosition = 0;
                for (Integer oldPosition : sourceRack.getDevicePositionMap()
                        .keySet()
                        .stream()
                        .sorted()
                        .toList()) {
                    newPosition += 1;
                    newDeviceMap.put(newPosition, sourceRack.getDevicePositionMap().get(oldPosition));
                }
                sourceRack.setDevicePositionMap(newDeviceMap);
            }
        }

        // Destination Rack: Update Positions
        if (destinationLocation instanceof Rack destinationRack) {
            destinationRackPositionUpdate(destinationRack, referencedDeviceList);
        }

        // Balance Settings
        List<Integer> affectedUtilizerIdList = referencedDeviceList
                .stream()
                .map(Device::getUtilizer)
                .map(Utilizer::getId)
                .distinct()
                .toList();

        boolean hasBalance;
        hasBalance = isBalance(destinationUtilizer, affectedUtilizerIdList);

        if (hasBalance) {
            List<Integer> utilizerIdList = referencedDeviceList
                    .stream()
                    .map(Device::getUtilizer)
                    .map(Utilizer::getId)
                    .toList();

            deviceMovementEvent.setUtilizerBalance(setupUtilizerBalanceMap(affectedUtilizerIdList, destinationUtilizer.getId(), utilizerIdList));

            List<Utilizer> utilizerList = new ArrayList<>();
            for (Integer utilizerId : deviceMovementEvent.getUtilizerBalance().keySet()) {
                utilizerList.add(resourceService.getReferencedUtilizer(utilizerId));
            }
            deviceMovementEvent.setUtilizerList(utilizerList);
        } else {
            deviceMovementEvent.setUtilizerList(List.of(destinationUtilizer));
        }

        for (Device device : referencedDeviceList) {
            device.setLocation(destinationLocation);
            if (hasBalance) {
                device.setUtilizer(destinationUtilizer);
            }
        }

        // many to many for inverse-side references
        deviceMovementEvent.setDeviceList(referencedDeviceList);
        deviceMovementEvent.setLocationList(List.of(sourceLocation, destinationLocation));

        return deviceMovementEvent;
    }

    private Event deviceUtilizerEventRegister_5(EventForm eventForm) {
        var newUtilizer = resourceService.getReferencedUtilizer(eventForm.getUtilizer_newUtilizerId());
        var optionalDevice = resourceService.getDevice(eventForm.getUtilizer_deviceId());

        if (optionalDevice.isPresent()) {
            var device = optionalDevice.get();
            DeviceUtilizerEvent deviceUtilizerEvent = new DeviceUtilizerEvent();

            List<Integer> affectedUtilizerIdList = List.of(device.getUtilizer().getId());
            deviceUtilizerEvent.setUtilizerBalance
                    (setupUtilizerBalanceMap(affectedUtilizerIdList, newUtilizer.getId(), affectedUtilizerIdList));

            device.setUtilizer(newUtilizer);

            deviceUtilizerEvent.setLocationList(List.of(device.getLocation()));
            deviceUtilizerEvent.setDeviceList(List.of(device));
            deviceUtilizerEvent.setUtilizerList  // 0: old   1: new
                    (List.of(resourceService.getReferencedUtilizer(eventForm.getUtilizer_oldUtilizerId())
                            , newUtilizer));

            return deviceUtilizerEvent;

        } else {
            throw new NoSuchElementException();
        }
    }

    private void finalizeEvent(EventForm eventForm, LocalDate validDate, Event event) throws IOException {
        event.setRegisterDate(UtilService.getDATE());
        event.setRegisterTime(UtilService.getTime());
        event.setEventDate(validDate);
        event.setEventCategory(eventCategoryRepository.getReferenceById(eventForm.getEventCategoryId()));
        if (eventForm.getEventCategoryId() == General_Event_Category_ID) {
            event.setActive(!eventForm.isGeneral_activation());
        } else {
            event.setActive(false);
        }

        eventPersistence(eventForm, event);
        eventPostProcessing(eventForm);
    }

    private void eventPostProcessing(EventForm eventForm) {
        if (eventForm.getEventCategoryId() == NewDevice_Installation_EVENT_CATEGORY_ID) {
            resourceService.deleteUnassignedDeviceIdList(eventForm.getUnassignedDeviceIdList());
        }
    }

    private static boolean isBalance(Utilizer newUtilizer, List<Integer> affectedUtilizerIdList) {
        boolean hasBalance;
        hasBalance = affectedUtilizerIdList.size() != 1 || !Objects.equals(affectedUtilizerIdList.get(0), newUtilizer.getId());
        return hasBalance;
    }

    private static Map<Integer, Integer> setupUtilizerBalanceMap(List<Integer> affectedUtilizerIdList, Integer destinationUtilizerId, List<Integer> utilizerIdList) {
        // 3. init utilizerBalance Map
        Map<Integer, Integer> utilizerBalance = new HashMap<>();
        for (Integer utilizerId : affectedUtilizerIdList) {
            utilizerBalance.put(utilizerId, 0);
        }

        if (affectedUtilizerIdList.stream().noneMatch(UtilizerId -> Objects.equals(UtilizerId, destinationUtilizerId))) {
            utilizerBalance.put(destinationUtilizerId, 0);
        }

        // 4. update utilizerBalance
        for (Integer utilizerId : utilizerIdList) {
            if (!Objects.equals(utilizerId, destinationUtilizerId)) {
                var currentBalance = utilizerBalance.get(utilizerId);
                currentBalance -= 1;
                utilizerBalance.put(utilizerId, currentBalance);

                var destBalance = utilizerBalance.get(destinationUtilizerId);
                destBalance += 1;
                utilizerBalance.put(destinationUtilizerId, destBalance);
            }
        }
        return utilizerBalance;
    }

    private static void destinationRackPositionUpdate(Rack rack, List<Device> referencedDeviceList) {
        if (rack.getDevicePositionMap().isEmpty()) { // Empty Rack
            Map<Integer, Device> devicePositionMap = new HashMap<>();
            int position = 0;
            for (Device device : referencedDeviceList) {
                position += 1;
                devicePositionMap.put(position, device);
            }
            rack.setDevicePositionMap(devicePositionMap);
        } else {  // New devices will be added to the end of the list
            int position = rack.getDevicePositionMap().size();
            for (Device device : referencedDeviceList) {
                position += 1;
                rack.getDevicePositionMap().put(position, device);
            }
        }
    }

    private EventDetail eventDetailRegister(Event event, EventForm eventForm) {
        EventDetail eventDetail = new EventDetail();
        eventDetail.setRegisterDate(UtilService.getDATE());
        eventDetail.setRegisterTime(UtilService.getTime());
        Persistence persistence;
        if (eventForm.getEventId() != null) {
            persistence = logService.newPersistenceInitialization("EventUpdate", personService.getCurrentPerson(), "Event");
        } else {
            persistence = logService.newPersistenceInitialization("EventRegister", personService.getCurrentPerson(), "Event");
        }
        eventDetail.setPersistence(persistence);
        eventDetail.setDescription(eventForm.getDescription());
        eventDetail.setEvent(event);
        if (event.getEventDetailList() != null) {
            event.getEventDetailList().add(eventDetail);
        } else {
            event.setEventDetailList(List.of(eventDetail));
        }

        return eventDetail;
    }

    private void eventPersistence(EventForm eventForm, Event event) throws IOException {
        EventDetail eventDetail;
        if (eventForm.getEventId() != null) {
            event.setActive(!eventForm.isGeneral_activation());
        }
        eventDetail = eventDetailRegister(event, eventForm);
        var persistedDetail = eventDetailRepository.save(eventDetail);
        if (eventForm.getMultipartFile() != null) {
            fileService.registerAttachment(eventForm.getMultipartFile(), persistedDetail.getPersistence());
        }
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISOR', 'OPERATOR', 'MANAGER')")
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
    public Map<Utilizer, Integer> getBalanceReference(Event baseEvent) {
        Map<Utilizer, Integer> balanceMap = new HashMap<>();
        Map<Integer, Integer> balanceId = baseEvent.getUtilizerBalance();
        if (!balanceId.isEmpty()) {
            Set<Integer> utilizerIdSet = balanceId.keySet();
            for (Integer utilizerId : utilizerIdSet) {
                var utilizer = resourceService.getReferencedUtilizer(utilizerId);
                balanceMap.put(utilizer, balanceId.get(utilizerId));
            }
        }
        return balanceMap;
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISOR', 'OPERATOR')")
    public void updateGeneralEvent(EventForm eventForm) throws IOException {
        var event = eventRepository.getReferenceById(eventForm.getEventId());
        eventPersistence(eventForm, event);
    }

    @Override
    public boolean newDevicePresentCheck() {
        return resourceService.newDevicePresentCheck();
    }

    @Override
    public List<ModuleInventory> getDeviceCompatibleModuleInventoryList(Integer deviceCategoryID) {
        return resourceService
                .getDeviceCompatibleModuleInventoryList(deviceCategoryID)
                .stream()
                .sorted(Comparator.comparing(ModuleInventory::getClassification))
                .toList();
    }

    @Override
    public Map<ModuleInventory, Integer> getDeviceModuleOverviewMap(List<ModulePack> packList) {
        return resourceService.getDeviceModuleOverview(packList);
    }

    @Override
    public List<Event> loadEventTransients_1(List<Event> eventList) {
        for (Event event : eventList) {
            event.setPersianEventDate(UtilService.getFormattedPersianDate(event.getEventDate()));
            event.setPersianEventDay(UtilService.PERSIAN_DAY.get(event.getEventDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault())));
            event.setPersianRegisterDate(UtilService.getFormattedPersianDate(event.getRegisterDate()));
            event.setPersianRegisterDayTime(UtilService.getFormattedPersianDayTime(event.getRegisterDate(), event.getRegisterTime()));
        }
        return eventList;
    }

    private void loadEventTransients_2(List<EventDetail> eventDetailList) {
        for (EventDetail eventDetail : eventDetailList
        ) {
            eventDetail.setPersianRegisterDate(UtilService.getFormattedPersianDate(eventDetail.getRegisterDate()));
            eventDetail.setPersianRegisterDayTime(UtilService.getFormattedPersianDayTime(eventDetail.getRegisterDate(), eventDetail.getRegisterTime()));
        }
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

