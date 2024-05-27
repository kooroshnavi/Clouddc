package ir.tic.clouddc.event;

import com.github.mfathi91.time.PersianDate;
import ir.tic.clouddc.center.*;
import ir.tic.clouddc.document.FileService;
import ir.tic.clouddc.document.MetaData;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.pm.PmService;
import ir.tic.clouddc.report.ReportService;
import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.ResourceService;
import ir.tic.clouddc.utils.UtilService;
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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventDetailRepository eventDetailRepository;
    private final ReportService reportService;
    private final CenterService centerService;
    private final PersonService personService;
    private final PmService pmService;
    private final FileService fileService;
    private final LogService logService;
    private final ResourceService resourceService;
    private final FailureDeviceEventRepository failureDeviceEventRepository;


    @Autowired
    public EventServiceImpl(
            EventRepository eventRepository
            , EventDetailRepository eventDetailRepository, ReportService reportService
            , CenterService centerService
            , PersonService personService
            , PmService pmService
            , FileService fileService, LogService logService, DataCenterRepository dataCenterRepository, ResourceService resourceService, FailureDeviceEventRepository failureDeviceEventRepository) {
        this.eventRepository = eventRepository;
        this.eventDetailRepository = eventDetailRepository;
        this.reportService = reportService;
        this.centerService = centerService;
        this.personService = personService;
        this.pmService = pmService;
        this.resourceService = resourceService;
        this.fileService = fileService;
        this.logService = logService;
        this.failureDeviceEventRepository = failureDeviceEventRepository;
    }

    @Override
    @PreAuthorize("id >= 1 && id <= 4")
    public Model getEventRegisterFormModel(Model model, int eventCategory) {
        EventForm eventForm = new EventForm();
        switch (eventCategory) {
            case 1 -> {  //// prepare deviceFailureForm model
                eventForm.setEventType(1);
                model.addAttribute("dataCenterNameList", centerService.getAllDataCenterNameList());
                model.addAttribute("salonNameList", centerService.getSalonNameList());
                model.addAttribute("rackList", centerService.getRackList());
                model.addAttribute("utilizerList", personService.getUtilizerList());
            }
            case 2 -> {  //// prepare centerVisitForm model
                eventForm.setEventType(2);
                model.addAttribute("dataCenterList", centerService.getCenterList());
            }
            case 3 -> {  //// prepare salonEventForm model
                eventForm.setEventType(3);
                model.addAttribute("salonList", centerService.getSalonList());
            }
        }

        model.addAttribute("eventForm", eventForm);
        return model;
    }


    @Override
    public void eventRegister(EventForm eventForm) throws IOException {

        switch (eventForm.getEventType()) {
            case 1 -> failureDeviceEventSetup(eventForm, '1');
            case 2 -> visitEventSetup(eventForm, '2');
            case 3 -> salonEventSetup(eventForm, '3');
        }

    }

    private void failureDeviceEventSetup(EventForm eventForm, char category) throws IOException {
        FailureDeviceEvent event = new FailureDeviceEvent();
        event.setCategory(category);
        event.setDate(UtilService.getDATE());
        event.setTime(UtilService.getTime());
        event.setActive(eventForm.isActive());
        Device device = resourceService.validateFormDevice(eventForm);
        device.setFailure(eventForm.isActive());
        event.setFailedDevice(device);

        eventDetailRegister(eventForm, event);
    }

    private void visitEventSetup(EventForm eventForm, char category) throws IOException {
        VisitEvent event = new VisitEvent();
        event.setCategory(category);
        event.setDate(UtilService.getDATE());
        event.setTime(UtilService.getTime());
        event.setActive(false);
        event.setCenter(new Center(eventForm.getCenterId()));
        eventDetailRegister(eventForm, event);
    }

    private void salonEventSetup(EventForm eventForm, char category) throws IOException {
        SalonEvent event = new SalonEvent();
        event.setCategory(category);
        event.setDate(UtilService.getDATE());
        event.setTime(UtilService.getTime());
        event.setActive(false);
        event.setSalon(centerService.getSalon(eventForm.getSalonId()));
        eventDetailRegister(eventForm, event);
    }


    private void eventDetailRegister(EventForm eventForm, Event event) throws IOException {
        EventDetail eventDetail = new EventDetail();
        var currentPerson = personService.getCurrentPerson();
        var persistence = logService.persistenceSetup(currentPerson);
        logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), '8', currentPerson, persistence);
        fileService.checkAttachment(eventForm.getFile(), persistence);
        eventDetail.setPersistence(persistence);
        eventDetail.setDescription(eventForm.getDescription());
        eventDetail.setDate(UtilService.getDATE());
        eventDetail.setTime(UtilService.getTime());
        eventDetail.setEvent(event);
        reportService.findActive(true).get().getEventList().add(event);
        eventDetailRepository.save(eventDetail);
    }

    @Override
    public Model getEventListModel(Model model) {
        List<Event> eventList = eventRepository.findAll(Sort.by("active").descending());

        for (Event event : eventList) {
            event.setPersianDate(UtilService.getFormattedPersianDate(event.getDate()));
            event.setPersianDayTime(UtilService.persianDay.get(event.getDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault())) + " - " + event.getTime());
            switch (event.getCategory()) {
                case '1':
                    event.setCategoryName("خرابی تجهیز");
                case '2':
                    event.setCategoryName("بازدید");
                case '3':
                    event.setCategoryName("مشکل سالن");
            }
        }
        model.addAttribute("eventList", getEventListModel());
        return model;
    }

    @Override
    public List<Event> getEventListModel() {
        return null;
    }

    @Override
    public Model getEventDetailModel(Model model, Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        List<EventDetail> eventDetailList;
        if (optionalEvent.isPresent()) {
            Event baseEvent = optionalEvent.get();

            if (baseEvent instanceof FailureDeviceEvent failureDeviceEvent) {
                eventDetailList = failureDeviceEvent.getEventDetailList();
                model.addAttribute("failureDeviceEvent", failureDeviceEvent);
            } else if (baseEvent instanceof VisitEvent visitEvent) {
                eventDetailList = visitEvent.getEventDetailList();
                model.addAttribute("visitEvent", visitEvent);
            } else if (baseEvent instanceof SalonEvent salonEvent) {
                eventDetailList = salonEvent.getEventDetailList();
                model.addAttribute("salonEvent", salonEvent);
            } else {
                return null;
            }

            for (EventDetail eventDetail : eventDetailList) {
                loadEventDetailTransients(eventDetail);
            }

            model.addAttribute("eventDetailList", eventDetailList);
            return model;
        }
        return null;
    }

    private void loadEventDetailTransients(EventDetail eventDetail) {
        eventDetail.setPersianDate(UtilService.getFormattedPersianDate(eventDetail.getDate()));
        eventDetail.setPersianDay(UtilService.persianDay.get(eventDetail.getDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault())));
    }


    @Override
    public Model getEvent(Long eventId) {
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        Event event = eventRepository.findById(eventId).get();
        event.setPersianDate(dateTime.format(PersianDate.fromGregorian(event
                .getEventDetailList().stream().findFirst().get()
                .getPersistence()
                .getLogHistoryList().stream().findFirst().get()
                .getDate())));
        return event;
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
            eventDetail.setTime(timeFormatter.format(eventDetail.getPersistence().getLogHistoryList().stream().findFirst().get().getTime()));
        }

        return event.getEventDetailList()
                .stream()
                .sorted(Comparator.comparing(EventDetail::getId).reversed())
                .collect(Collectors.toList());
    }


    @Override
    public void updateEvent(Long eventId, EventForm eventForm) throws IOException {
        Event event = eventRepository.findById(eventId).get();
        eventDetailRegister(eventForm, event);

        if (!eventForm.isActive()) {
            event.setActive(false);
        }

        eventRepository.save(event);

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

