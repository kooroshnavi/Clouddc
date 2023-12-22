package com.navi.dcim.event;

import com.github.mfathi91.time.PersianDateTime;
import com.navi.dcim.center.CenterService;
import com.navi.dcim.person.Person;
import com.navi.dcim.person.PersonService;
import com.navi.dcim.report.DailyReport;
import com.navi.dcim.report.ReportService;
import com.navi.dcim.task.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.navi.dcim.utils.UtilService.getCurrentDate;

@Slf4j
@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ReportService reportService;
    private final CenterService centerService;
    private final PersonService personService;
    private final TaskService taskService;
    private final EventTypeRepository eventTypeRepository;

    @Autowired
    public EventServiceImpl(
            EventRepository eventRepository
            , ReportService reportService
            , CenterService centerService
            , PersonService personService
            , TaskService taskService
            , EventTypeRepository eventTypeRepository) {
        this.eventRepository = eventRepository;
        this.reportService = reportService;
        this.centerService = centerService;
        this.personService = personService;
        this.taskService = taskService;
        this.eventTypeRepository = eventTypeRepository;
    }

    @Override
    public void eventRegister(EventForm eventForm) {
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        Optional<DailyReport> report = reportService.findActive(true);
        var eventType = new EventType(eventForm.getEventType());
        var registerDate = LocalDateTime.now();

        Event event = new Event(registerDate
                , eventForm.isActive()
                , new EventType(eventForm.getEventType())
                , centerService.getCenter(eventForm.getCenterId()));

        EventDetail eventDetail1 = defaultEventDetail(eventForm);
        eventDetail1.setUpdated(registerDate);
        eventDetail1.setPersianDate(dateTime.format(PersianDateTime.fromGregorian(registerDate)));
        eventDetail1.setEvent(event);
        event.setEventDetailList(eventDetail1);

        event.setDailyReportList(report.get());
        event.setType(eventType);
        eventType.setEvent(event);
        log.info("event added" + report.get().getEventList());
        eventRepository.save(event);

    }

    private EventDetail defaultEventDetail(EventForm eventForm) {
        EventDetail eventDetail = new EventDetail();
        eventDetail.setPerson(personService.getPerson(SecurityContextHolder.getContext().getAuthentication().getName()));
        eventDetail.setActive(false);
        eventDetail.setDescription(eventForm.getDescription());
        return eventDetail;
    }

    private EventDetail activeEventDetailRegister(Event event, EventForm eventForm) {
        EventDetail eventDetail = new EventDetail();
        eventDetail.setActive(false);
        eventDetail.setUpdated(LocalDateTime.now());
        eventDetail.setEvent(event);
        eventDetail.setDescription(eventForm.getDescription());
        eventDetail.setPerson(personService.getPerson(SecurityContextHolder.getContext().getAuthentication().getName()));
        event.setEventDetailList(eventDetail);
        return eventDetail;
    }


    @Override
    public Event getEvent(int eventId) {
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        Event event = eventRepository.findById(eventId).get();
        event.setPersianDate(dateTime.format(PersianDateTime.fromGregorian(event.getEventDate())));
        return event;
    }

    @Override
    public List<EventType> getEventTypeList() {
        return eventTypeRepository.findAll();
    }

    @Override
    public Model modelForEventController(Model model) {
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = personService.getPerson(personName);
        model.addAttribute("person", person);
        model.addAttribute("pending", taskService.getPersonTask().size());
        model.addAttribute("pendingEvents", getPendingEventList().size());
        model.addAttribute("date", getCurrentDate());
        return model;
    }

    @Override
    public Model modelForEventRegisterForm(Model model) {
        model.addAttribute("centerList", centerService.getCenterList());
        model.addAttribute("eventTypeList", eventTypeRepository.findAll());
        model.addAttribute("eventRegister", new EventForm());
        return model;
    }

    @Override
    public Model modelForEventDetail(Model model, int eventId) {
        List<EventDetail> eventDetailList = getEventDetailList(eventId);

        model.addAttribute("eventDetailList", eventDetailList);
        model.addAttribute("event", this.getEvent(eventId));
        model.addAttribute("id", eventId);
        model.addAttribute("eventForm", new EventForm());
        model.addAttribute("centerList", centerService.getCenterList());
        model.addAttribute("eventTypeList", getEventTypeList());
        return model;
    }

    private List<EventDetail> getEventDetailList(int eventId) {
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        Event event = eventRepository.findById(eventId).get();
        for (EventDetail eventDetail : event.getEventDetailList()
        ) {
            eventDetail.setPersianDate(dateTime.format
                    (PersianDateTime
                            .fromGregorian
                                    (eventDetail.getUpdated())));
        }

        return event.getEventDetailList()
                .stream()
                .sorted(Comparator.comparing(EventDetail::getId).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Model modelForEventList(Model model) {
        model.addAttribute("centerList", centerService.getCenterList());
        model.addAttribute("eventTypeList", getEventTypeList());
        model.addAttribute("eventList", getEventList());
        return model;
    }

    @Override
    public List<Event> getEventList() {
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        List<Event> eventList = eventRepository.findAll(Sort.by("active").descending());
        for (Event event : eventList) {
            event.setPersianDate(dateTime.format(PersianDateTime.fromGregorian(event.getEventDate())));
        }

        return eventList;
    }

    @Override
    public void updateEvent(int eventId, EventForm eventForm) {
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        Optional<DailyReport> report = reportService.findActive(true);
        Event event = eventRepository.findById(eventId).get();
        EventDetail eventDetail = activeEventDetailRegister(event, eventForm);
        eventDetail.setPersianDate(dateTime.format(PersianDateTime.fromGregorian(eventDetail.getUpdated())));

        if (!eventForm.isActive()) {
            event.setActive(false);
        }

        if (report.get().getEventList().stream().noneMatch(event1 -> event1.getId() == eventId)) {
            event.setDailyReportList(report.get());
        }

        eventRepository.save(event);

    }

    @Override
    public List<Event> getPendingEventList() {
        return eventRepository.findAllByActive(true);
    }
}
