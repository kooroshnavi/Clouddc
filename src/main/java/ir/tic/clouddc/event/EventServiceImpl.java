package ir.tic.clouddc.event;

import com.github.mfathi91.time.PersianDate;
import ir.tic.clouddc.center.*;
import ir.tic.clouddc.document.FileService;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.report.ReportService;
import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.DeviceStatus;
import ir.tic.clouddc.resource.ResourceService;
import ir.tic.clouddc.resource.Utilizer;
import ir.tic.clouddc.utils.UtilService;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

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

    private static final short VISIT_EVENT_CATEGORY_ID = 1;
    private static final short LOCATION_EVENT_CATEGORY_ID = 2;
    private static final short DEVICE_UTILIZER_EVENT_CATEGORY_ID = 3;
    private static final short DEVICE_MOVEMENT_EVENT_CATEGORY_ID = 4;
    private static final short DEVICE_STATUS_EVENT_CATEGORY_ID = 5;


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
    public LocationStatus getCurrentLocationStatus(Location location) {
        return centerService.getCurrentLocationStatus(location);
    }

    @Override
    public List<Utilizer> deviceUtilizerEventData(Utilizer utilizer) {
        return resourceService.getUtilizerListExcept(utilizer);
    }

    @Override
    public List<Center> getCenterList() {
        return centerService.getCenterList();
    }

    @Override
    public void eventSetup(EventLandingForm eventLandingForm
            , @Nullable DeviceStatusForm deviceStatusForm
            , @Nullable LocationStatusForm locationStatusForm) throws IOException {
        EventDetail eventDetail;

        switch (eventLandingForm.getEventCategoryId()) {
            case VISIT_EVENT_CATEGORY_ID -> {

            }
            case LOCATION_EVENT_CATEGORY_ID -> {

            }
            case DEVICE_UTILIZER_EVENT_CATEGORY_ID -> {

            }
            case DEVICE_MOVEMENT_EVENT_CATEGORY_ID -> {

            }
            case DEVICE_STATUS_EVENT_CATEGORY_ID -> {
                if (!Objects.isNull(deviceStatusForm)) {
                    eventLandingForm.setFile(deviceStatusForm.getFile());
                    eventLandingForm.setDescription(deviceStatusForm.getDescription());
                    eventLandingForm.setDevice(deviceStatusForm.getDevice());
                }
                DeviceStatusEvent event = eventRegister(eventLandingForm);
                resourceService.updateDeviceStatus(deviceStatusForm, event);
            }
        }

        if (!Objects.isNull(deviceStatusForm)) {    //  5. Device status event


        } else if (!Objects.isNull(locationStatusForm)) {
            //   2. Location status event


            eventDetail = locationStatusEvent.registerEvent(locationStatusForm);
            eventDetail.setPersistence(logService.persistenceSetup(personService.getCurrentPerson()));
            fileService.checkAttachment(locationStatusForm.getFile(), eventDetail.getPersistence());

            var persistedEventDetail = eventDetailRepository.saveAndFlush(eventDetail);

            centerService.updateLocationStatus(locationStatusForm, (LocationStatusEvent) persistedEventDetail.getEvent());

        } else {
            switch (eventLandingForm.getEventCategoryId()) {
                case 1 -> {   // 1. visit event
                    VisitEvent visitEvent = new VisitEvent();
                    visitEvent.setRegisterDate(UtilService.getDATE());
                    visitEvent.setRegisterTime(UtilService.getTime());
                    visitEvent.setEventCategory(eventCategoryRepository.findById(eventLandingForm.getEventCategoryId()).get());
                    visitEvent.setCenter(eventLandingForm.getCenter());
                    visitEvent.setActive(false);

                    eventDetail = new EventDetail();
                    eventDetail.setEvent(visitEvent);
                    eventDetail.setRegisterDate(visitEvent.getRegisterDate());
                    eventDetail.setRegisterTime(visitEvent.getRegisterTime());
                    eventDetail.setDescription(eventLandingForm.getDescription());
                    eventDetail.setPersistence(logService.persistenceSetup(personService.getCurrentPerson()));
                    fileService.checkAttachment(eventLandingForm.getFile(), eventDetail.getPersistence());

                    eventDetailRepository.saveAndFlush(eventDetail);
                }

                case 3 -> {      // 3. Device utilizer event

                    eventDetail = deviceUtilizerEvent.registerEvent(eventLandingForm);
                    eventDetail.setPersistence(logService.persistenceSetup(personService.getCurrentPerson()));

                    fileService.checkAttachment(eventLandingForm.getFile(), eventDetail.getPersistence());
                    var persistedEventDetail = eventDetailRepository.saveAndFlush(eventDetail);

                    resourceService.updateDeviceUtilizer((DeviceUtilizerEvent) persistedEventDetail.getEvent());
                }
                case 4 -> { // 4. Device movement event

                    var persistedEventDetail = eventDetailRepository.saveAndFlush(eventDetail);

                    resourceService.updateDeviceLocation((DeviceMovementEvent) persistedEventDetail.getEvent());
                }
            }
        }
    }

    private Event eventRegister(EventLandingForm eventLandingForm) throws IOException {
        EventDetail persistedEventDetail;
        switch (eventLandingForm.getEventCategoryId()) {
            case VISIT_EVENT_CATEGORY_ID -> {

            }
            case LOCATION_EVENT_CATEGORY_ID -> {
                LocationStatusEvent locationStatusEvent = new LocationStatusEvent();
                locationStatusEvent.setRegisterDate(UtilService.getDATE());
                locationStatusEvent.setRegisterTime(UtilService.getTime());
                locationStatusEvent.setEventCategory(eventCategoryRepository.findById(eventLandingForm.getEventCategoryId()).get());
            }
            case DEVICE_UTILIZER_EVENT_CATEGORY_ID -> {
                DeviceUtilizerEvent deviceUtilizerEvent = new DeviceUtilizerEvent();
                deviceUtilizerEvent.setRegisterDate(UtilService.getDATE());
                deviceUtilizerEvent.setRegisterTime(UtilService.getTime());
                deviceUtilizerEvent.setEventCategory(eventCategoryRepository.findById(eventLandingForm.getEventCategoryId()).get());
                deviceUtilizerEvent.setNewUtilizer(resourceService.getUtilizer(eventLandingForm.getUtilizerId()));
                deviceUtilizerEvent.setOldUtilizer(eventLandingForm.getDevice().getUtilizer());
                deviceUtilizerEvent.setDevice(eventLandingForm.getDevice());
                deviceUtilizerEvent.setActive(false);
                persistedEventDetail = eventDetailRegister(deviceUtilizerEvent, eventLandingForm.getFile(), eventLandingForm.getDescription());
            }
            case DEVICE_MOVEMENT_EVENT_CATEGORY_ID -> {
                DeviceMovementEvent deviceMovementEvent = new DeviceMovementEvent();
                deviceMovementEvent.setRegisterDate(UtilService.getDATE());
                deviceMovementEvent.setRegisterTime(UtilService.getTime());
                deviceMovementEvent.setEventCategory(eventCategoryRepository.findById(eventLandingForm.getEventCategoryId()).get());
                var destination = centerService.getLocation(eventLandingForm.getLocationId());
                destination.ifPresent(deviceMovementEvent::setDestination);
                deviceMovementEvent.setSource(eventLandingForm.getDevice().getLocation());
                deviceMovementEvent.setDevice(eventLandingForm.getDevice());
                deviceMovementEvent.setActive(false);
                persistedEventDetail = eventDetailRegister(deviceMovementEvent, eventLandingForm.getFile(), eventLandingForm.getDescription());
            }
            case DEVICE_STATUS_EVENT_CATEGORY_ID -> {
                DeviceStatusEvent deviceStatusEvent = new DeviceStatusEvent();
                deviceStatusEvent.setRegisterDate(UtilService.getDATE());
                deviceStatusEvent.setRegisterTime(UtilService.getTime());
                deviceStatusEvent.setEventCategory(eventCategoryRepository.findById(eventLandingForm.getEventCategoryId()).get());
                deviceStatusEvent.setDevice(eventLandingForm.getDevice());
                deviceStatusEvent.setActive(false);
                persistedEventDetail = eventDetailRegister(deviceStatusEvent, eventLandingForm.getFile(), eventLandingForm.getDescription());
            }

        }


    }

    private EventDetail eventDetailRegister(Event event, MultipartFile file, String description) throws IOException {
        EventDetail eventDetail = new EventDetail();
        var currentPerson = personService.getCurrentPerson();
        var persistence = logService.persistenceSetup(currentPerson);
        logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), 5, currentPerson, persistence);
        fileService.checkAttachment(file, persistence);
        eventDetail.setPersistence(persistence);
        eventDetail.setDescription(description);
        eventDetail.setRegisterDate(event.getRegisterDate());
        eventDetail.setRegisterTime(event.getRegisterTime());
        eventDetail.setEvent(event);

        return eventDetailRepository.save(eventDetail);

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

            var eventForm = new EventLandingForm();
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
    public List<CenterService.CenterIdNameProjection> getCenterIdAndNameList() {
        return centerService.getCenterIdAndNameList();
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
    public void updateEvent(EventLandingForm eventLandingForm, Event event) throws IOException {
        if (!eventLandingForm.isActive()) {
            event.setActive(false);
        }
        eventDetailRegister(eventLandingForm, event);
    }


    @Override
    public List<Event> getPendingEventList() {
        return eventRepository.findAllByActive(true);
    }

    @Override
    public Optional<Center> getCenter(short centerId) {
        return centerService.getCenter(centerId);
    }

    @Override
    public Optional<Location> getLocation(int locationId) {
        return centerService.getLocation(locationId);
    }

    @Override
    public Optional<Device> getDevice(String serialNumber) {
        return resourceService.getDevice(serialNumber);
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

