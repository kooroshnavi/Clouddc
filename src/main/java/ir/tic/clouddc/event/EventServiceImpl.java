package ir.tic.clouddc.event;

import com.github.mfathi91.time.PersianDate;
import ir.tic.clouddc.center.*;
import ir.tic.clouddc.document.FileService;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.report.ReportService;
import ir.tic.clouddc.resource.DeviceStatus;
import ir.tic.clouddc.resource.ResourceService;
import ir.tic.clouddc.utils.UtilService;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public final class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventDetailRepository eventDetailRepository;
    private final EventCategoryRepository eventCategoryRepository;
    private final ReportService reportService;
    private final CenterService centerService;
    private final PersonService personService;
    private final FileService fileService;
    private final LogService logService;
    private final ResourceService resourceService;


    @Autowired
    public EventServiceImpl(
            EventRepository eventRepository
            , EventDetailRepository eventDetailRepository, EventCategoryRepository eventCategoryRepository, ReportService reportService
            , CenterService centerService
            , PersonService personService
            , FileService fileService, LogService logService, ResourceService resourceService) {
        this.eventRepository = eventRepository;
        this.eventDetailRepository = eventDetailRepository;
        this.eventCategoryRepository = eventCategoryRepository;
        this.reportService = reportService;
        this.centerService = centerService;
        this.personService = personService;
        this.resourceService = resourceService;
        this.fileService = fileService;
        this.logService = logService;
    }

    @Override
    public Model getDeviceEventEntryForm(Model model, short target) {
        EventRegisterForm eventEntryForm = new EventRegisterForm();
        model.addAttribute("eventEntryForm", eventEntryForm);

        if (target == 1) {  // 1. CenterList for visit event entry form
            List<CenterService.CenterIdNameProjection> centerList = centerService.getCenterIdAndNameList();
        }
        else if (target == 2) {  // 2. LocationList for Location status event entry form
            short salonType = 1;
            short roomType = 3;
            List<Short> locationCategoryList = List.of(salonType,roomType);
            List<Location> salonRoomLocationList = centerService.getTCustomizedLocationList(locationCategoryList);
            model.addAttribute("salonRoomLocationList", salonRoomLocationList);
        }

        return model;
    }

    @Override
    public Model getRelatedEventViewModel(Model model, @Nullable EventRegisterForm eventRegisterForm, @Nullable EventRegisterForm fromDevicePmForm) {

        if (!Objects.isNull(eventRegisterForm)) {
            var category = eventRegisterForm.getCategory();
            var device = resourceService.getDevice(eventRegisterForm.getSerialNumber());
            var location = centerService.getLocation(eventRegisterForm.getLocationId());
            model.addAttribute("category", category);

            if (device.isPresent()) {
                model.addAttribute("device", device.get());
                switch (category) {
                    case 5:  // 5. Device utilizer form
                        eventRegisterForm.setDevice(device.get());
                        model.addAttribute("eventForm", eventRegisterForm);
                        model.addAttribute("utilizerList", resourceService.getUtilizerList());

                    case 6:  // 6. Device movement form
                        model.addAttribute("eventForm", eventRegisterForm);
                        model.addAttribute("centerList", centerService.getCenterList());
                        model.addAttribute("salonList", centerService.getSalonList());

                    case 7:  // 7. Device status form
                        DeviceStatusForm deviceStatusForm = new DeviceStatusForm();
                        deviceStatusForm.setCategory(category);
                        deviceStatusForm.setDevice(device.get());
                        model.addAttribute("eventForm", deviceStatusForm);

                        var currentStatus = device.get().getDeviceStatusList().stream().filter(DeviceStatus::isCurrent).findFirst();
                        if (currentStatus.isPresent()) {
                            model.addAttribute("currentStatus", currentStatus.get());
                        } else {
                            DeviceStatus defaultDeviceStatus = new DeviceStatus();
                            defaultDeviceStatus.setDualPower(true);
                            defaultDeviceStatus.setSts(true);
                            defaultDeviceStatus.setFan(true);
                            defaultDeviceStatus.setModule(true);
                            defaultDeviceStatus.setStorage(true);
                            defaultDeviceStatus.setPort(true);
                            model.addAttribute("currentStatus", defaultDeviceStatus);
                        }
                }
            } else if (location.isPresent()) {
                if (category == 2) {  // 2. Location status form
                    LocationStatusForm locationStatusForm = new LocationStatusForm();
                    locationStatusForm.setCategory(category);
                    locationStatusForm.setLocation(location.get());
                    model.addAttribute("eventForm", locationStatusForm);

                    var currentStatus = location.get().getLocationStatusList().stream().filter(LocationStatus::isCurrent).findFirst();
                    if (currentStatus.isPresent()) {
                        model.addAttribute("currentStatus", currentStatus.get());
                    } else {
                        LocationStatus defaultLocationStatus = new LocationStatus();
                        defaultLocationStatus.setDoor(true);
                        defaultLocationStatus.setVentilation(true);
                        defaultLocationStatus.setPower(true);
                        model.addAttribute("currentStatus", defaultLocationStatus);
                    }
                }

            }
        }
        return model;
    }

    @Override
    public void eventRegister(@Nullable EventRegisterForm eventRegisterForm
            , @Nullable DeviceStatusForm deviceStatusForm
            , @Nullable LocationStatusForm locationStatusForm) throws IOException {
        EventDetail eventDetail;

        if (!Objects.isNull(deviceStatusForm)) {

            if (deviceStatusForm.getCategory() == 7) {  //   7. Device status event
                DeviceStatusEvent deviceStatusEvent = new DeviceStatusEvent();
                deviceStatusEvent.setRegisterDate(UtilService.getDATE());
                deviceStatusEvent.setRegisterTime(UtilService.getTime());
                deviceStatusEvent.setEventCategory(eventCategoryRepository.findById(deviceStatusForm.getCategory()).get());
                eventDetail = deviceStatusEvent.registerEvent(deviceStatusForm);
                eventDetail.setPersistence(logService.persistenceSetup(personService.getCurrentPerson()));
                fileService.checkAttachment(deviceStatusForm.getFile(), eventDetail.getPersistence());

                var persistedEventDetail = eventDetailRepository.saveAndFlush(eventDetail);

                resourceService.updateDeviceStatus(deviceStatusForm, (DeviceStatusEvent) persistedEventDetail.getEvent());
            }


        } else if (!Objects.isNull(locationStatusForm)) {
            if (locationStatusForm.getCategory() == 2) {    //   2. Location status event
                LocationStatusEvent locationStatusEvent = new LocationStatusEvent();
                locationStatusEvent.setRegisterDate(UtilService.getDATE());
                locationStatusEvent.setRegisterTime(UtilService.getTime());
                locationStatusEvent.setEventCategory(eventCategoryRepository.findById(locationStatusForm.getCategory()).get());
                eventDetail = locationStatusEvent.registerEvent(locationStatusForm);
                eventDetail.setPersistence(logService.persistenceSetup(personService.getCurrentPerson()));
                fileService.checkAttachment(locationStatusForm.getFile(), eventDetail.getPersistence());

                var persistedEventDetail = eventDetailRepository.saveAndFlush(eventDetail);

                centerService.updateLocationStatus(locationStatusForm, (LocationStatusEvent) persistedEventDetail.getEvent());
            }
        } else if (!Objects.isNull(eventRegisterForm)) {
            switch (eventRegisterForm.getCategory()) {
                case 1 -> {   // 1. visit event
                    VisitEvent visitEvent = new VisitEvent();
                    visitEvent.setRegisterDate(UtilService.getDATE());
                    visitEvent.setRegisterTime(UtilService.getTime());
                    visitEvent.setEventCategory(eventCategoryRepository.findById(eventRegisterForm.getCategory()).get());
                    visitEvent.setCenter(centerService.getCenter(eventRegisterForm.getCenterId()));
                    visitEvent.setActive(false);

                    eventDetail = new EventDetail();
                    eventDetail.setEvent(visitEvent);
                    eventDetail.setRegisterDate(visitEvent.getRegisterDate());
                    eventDetail.setRegisterTime(visitEvent.getRegisterTime());
                    eventDetail.setDescription(eventRegisterForm.getDescription());
                    eventDetail.setPersistence(logService.persistenceSetup(personService.getCurrentPerson()));
                    fileService.checkAttachment(eventRegisterForm.getFile(), eventDetail.getPersistence());

                    eventDetailRepository.saveAndFlush(eventDetail);
                }

                case 5 -> {      // 5. Device utilizer event
                    DeviceUtilizerEvent deviceUtilizerEvent = new DeviceUtilizerEvent();
                    deviceUtilizerEvent.setRegisterDate(UtilService.getDATE());
                    deviceUtilizerEvent.setRegisterTime(UtilService.getTime());
                    deviceUtilizerEvent.setEventCategory(eventCategoryRepository.findById(eventRegisterForm.getCategory()).get());
                    deviceUtilizerEvent.setNewUtilizer(resourceService.getUtilizer(eventRegisterForm.getUtilizerId()));
                    eventDetail = deviceUtilizerEvent.registerEvent(eventRegisterForm);
                    eventDetail.setPersistence(logService.persistenceSetup(personService.getCurrentPerson()));

                    fileService.checkAttachment(eventRegisterForm.getFile(), eventDetail.getPersistence());
                    var persistedEventDetail = eventDetailRepository.saveAndFlush(eventDetail);

                    resourceService.updateDeviceUtilizer((DeviceUtilizerEvent) persistedEventDetail.getEvent());
                }
                case 6 -> { // 6. Device movement event
                    DeviceMovementEvent deviceMovementEvent = new DeviceMovementEvent();
                    deviceMovementEvent.setRegisterDate(UtilService.getDATE());
                    deviceMovementEvent.setRegisterTime(UtilService.getTime());
                    deviceMovementEvent.setEventCategory(eventCategoryRepository.findById(eventRegisterForm.getCategory()).get());
                    var destination = centerService.getLocation(eventRegisterForm.getLocationId());
                    destination.ifPresent(deviceMovementEvent::setDestination);
                    eventDetail = deviceMovementEvent.registerEvent(eventRegisterForm);
                    eventDetail.setPersistence(logService.persistenceSetup(personService.getCurrentPerson()));
                    fileService.checkAttachment(eventRegisterForm.getFile(), eventDetail.getPersistence());

                    var persistedEventDetail = eventDetailRepository.saveAndFlush(eventDetail);

                    resourceService.updateDeviceLocation((DeviceMovementEvent) persistedEventDetail.getEvent());
                }
            }
        }
    }


    private void eventDetailRegister(EventRegisterForm eventRegisterForm, Event event) throws IOException {
        EventDetail eventDetail = new EventDetail();
        var currentPerson = personService.getCurrentPerson();
        var persistence = logService.persistenceSetup(currentPerson);
        logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), 5, currentPerson, persistence);
        fileService.checkAttachment(eventRegisterForm.getFile(), persistence);
        eventDetail.setPersistence(persistence);
        eventDetail.setDescription(eventRegisterForm.getDescription());
        eventDetail.setRegisterDate(UtilService.getDATE());
        eventDetail.setRegisterTime(UtilService.getTime());
        eventDetail.setEvent(event);

        reportService.findActive(true).get().getEventList().add(event);
        eventDetailRepository.save(eventDetail);
    }

    @Override
    public Model getEventListModel(Model model) {
        List<Event> eventList = eventRepository.findAll(Sort.by("active").descending());
        for (Event event : eventList) {
            event.setPersianRegisterDayTime(UtilService.getFormattedPersianDate(event.getRegisterDate()));
            event.setPersianRegisterDate(UtilService.persianDay.get(event.getRegisterDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault())) + " - " + event.getRegisterTime());
        }
        model.addAttribute("eventList", eventList);
        return model;
    }

    @Override
    public Model getEventListByCategoryModel(Model model, @Nullable Short categoryId) {
        if (categoryId != null) {
            var optionalEventCategory = eventCategoryRepository.findById(categoryId);
            if (optionalEventCategory.isPresent()) {
                var category = optionalEventCategory.get();
                List<Event> eventList = eventRepository.findAllByCategory(category);
                for (Event event : eventList) {
                    event.setPersianRegisterDayTime(UtilService.getFormattedPersianDate(event.getRegisterDate()));
                    event.setPersianRegisterDate(UtilService.persianDay.get(event.getRegisterDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault())) + " - " + event.getRegisterTime());
                }
                model.addAttribute("eventList", eventList);
            }
        }

        return model;
    }

    @Override
    public Model getEventDetailModel(Model model, Long eventId) {
        var optionalEvent = eventRepository.findById(eventId);
        List<EventDetail> eventDetailList;

        if (optionalEvent.isPresent()) {
            var baseEvent = optionalEvent.get();
            eventDetailList = baseEvent.getEventDetailList();

            if (baseEvent instanceof DeviceStatusEvent event) {

                model.addAttribute("event", event);

            } else if (baseEvent instanceof VisitEvent event) {

                model.addAttribute("event", event);

            } else if (baseEvent instanceof LocationStatusEvent event) {

                model.addAttribute("event", event);

            } else {
                return model;
            }

            List<Long> persistenceIdList = new ArrayList<>();
            for (EventDetail eventDetail : eventDetailList) {
                loadEventDetailTransients(eventDetail);
                persistenceIdList.add(eventDetail.getPersistence().getId());
            }

            var sortedEventDetail = eventDetailList
                    .stream()
                    .sorted(Comparator.comparing(EventDetail::getId).reversed())
                    .toList();

            var eventForm = new EventRegisterForm();
            eventForm.setEventId(eventId);

            model.addAttribute("eventDetailList", sortedEventDetail);
            model.addAttribute("eventForm", eventForm);
            model.addAttribute("metaDataList", fileService.getRelatedMetadataList(persistenceIdList));

        }
        return model;
    }

    private void loadEventDetailTransients(EventDetail eventDetail) {
        eventDetail.setPersianDate(UtilService.getFormattedPersianDate(eventDetail.getRegisterDate()));
        eventDetail.setPersianDay(UtilService.persianDay.get(eventDetail.getRegisterDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault())));
    }


    @Override
    public Event getEvent(Long eventId) {
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        Event event = eventRepository.findById(eventId).get();
        event.setPersianRegisterDayTime(dateTime.format(PersianDate.fromGregorian(event
                .getEventDetailList().stream().findFirst().get()
                .getPersistence()
                .getLogHistoryList().stream().findFirst().get()
                .getDate())));
        return event;
    }

    @Override
    public List<EventCategory> getEventCategoryList() {
        return (List<EventCategory>) eventCategoryRepository.findAll();
    }


    @Override
    public Model modelForEventController(Model model) {
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = personService.getPerson(personName);
        model.addAttribute("person", person);
        model.addAttribute("role", authenticated.getAuthorities());
        model.addAttribute("date", UtilService.getCurrentDate());
        return model;
    }


    private List<EventDetail> getEventDetailList(Event event) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        for (EventDetail eventDetail : event.getEventDetailList()
        ) {
            var date = eventDetail.getPersistence().getLogHistoryList().stream().findFirst().get().getDate();
            eventDetail.setPersianDate(dateFormatter.format
                    (PersianDate
                            .fromGregorian
                                    (date)));
            eventDetail.setPersianDay(UtilService.persianDay.get(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault())));
            eventDetail.setRegisterTime(timeFormatter.format(eventDetail.getPersistence().getLogHistoryList().stream().findFirst().get().getTime()));
        }

        return event.getEventDetailList()
                .stream()
                .sorted(Comparator.comparing(EventDetail::getId).reversed())
                .collect(Collectors.toList());
    }


    @Override
    @PreAuthorize("event.active == true")
    public void updateEvent(EventRegisterForm eventRegisterForm, Event event) throws IOException {
        if (!eventRegisterForm.isActive()) {
            event.setActive(false);
        }
        eventDetailRegister(eventRegisterForm, event);
    }


    @Override
    public List<Event> getPendingEventList() {
        return eventRepository.findAllByActive(true);
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
        log.info(eventRepository.getEventTypeCount().toString());
        return eventRepository.getEventTypeCount();
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

