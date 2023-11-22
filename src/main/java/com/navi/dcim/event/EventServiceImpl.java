package com.navi.dcim.event;

import com.github.mfathi91.time.PersianDateTime;
import com.navi.dcim.center.CenterService;
import com.navi.dcim.model.DailyReport;
import com.navi.dcim.model.Event;
import com.navi.dcim.model.EventType;
import com.navi.dcim.model.Person;
import com.navi.dcim.person.PersonService;
import com.navi.dcim.report.ReportService;
import com.navi.dcim.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.navi.dcim.utils.UtilService.getCurrentDate;

class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ReportService reportService;
    private final CenterService centerService;
    private final PersonService personService;
    private final TaskService taskService;
    private final EventTypeRepository eventTypeRepository;


    @Autowired
    public EventServiceImpl(
            EventRepository eventRepository
            , ReportService reportService, CenterService centerService, PersonService personService, TaskService taskService, EventTypeRepository eventTypeRepository) {
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
        DailyReport report = reportService.findActive(true);
        var eventType = new EventType(eventForm.getEventType());
        var registerDate = LocalDateTime.now();
        Event event = new Event(registerDate
                , registerDate
                , eventForm.isActive()
                , " ( "
                + dateTime.format(PersianDateTime.fromGregorian(registerDate))
                + "-->" + eventForm.getDescription()
                + " ) "
                + System.lineSeparator()
                , new EventType(eventForm.getEventType())
                , personService.getPerson(SecurityContextHolder.getContext().getAuthentication().getName())
                , centerService.getCenter(eventForm.getCenterId()));
        event.setDailyReportList(report);
        event.setType(eventType);
        eventType.setEvent(event);
        System.out.println("event added" + report.getEventList());
        eventRepository.save(event);

    }


    @Override
    public Event getEvent(int eventId) {
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        Event event = eventRepository.findById(eventId).get();
        event.setPersianDate(dateTime.format(PersianDateTime.fromGregorian(event.getEventDate())));
        event.setPersianUpdate(dateTime.format(PersianDateTime.fromGregorian(event.getUpdateDate())));
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
    public Model modelForEventUpdate(Model model, int eventId) {
        model.addAttribute("event", this.getEvent(eventId));
        model.addAttribute("id", eventId);
        model.addAttribute("eventForm", new EventForm());
        model.addAttribute("centerList", centerService.getCenterList());
        model.addAttribute("eventTypeList", getEventTypeList());
        return model;
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
        for (Event event : eventList
        ) {
            event.setPersianDate(dateTime.format(PersianDateTime.fromGregorian(event.getEventDate())));
            event.setPersianUpdate(dateTime.format(PersianDateTime.fromGregorian(event.getUpdateDate())));
        }

        return eventList;
    }

    @Override
    public void updateEvent(int eventId, EventForm eventForm) {
        DailyReport report = reportService.findActive(true);
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        Event event = eventRepository.findById(eventId).get();
        event.setActive(eventForm.isActive());
        event.setUpdateDate(LocalDateTime.now());
        event.setPersianDate(dateTime.format(PersianDateTime.fromGregorian(event.getEventDate())));
        event.setPersianUpdate(dateTime.format(PersianDateTime.fromGregorian(event.getUpdateDate())));
        var description = event.getDescription()
                + " ( "
                + event.getPersianUpdate()
                + "-->"
                + eventForm.getDescription()
                + " ) "
                + System.lineSeparator();
        event.setDescription(description);

        if (report.getEventList().stream().noneMatch(event1 -> event1.getId() == eventId)) {
            System.out.println("Today report does not contain event id: " + eventId);
            event.setDailyReportList(report);
            System.out.println("Event updated:    " + report.getEventList());
        }
        eventRepository.save(event);

    }

    @Override
    public List<Event> getPendingEventList() {
        return eventRepository.findAllByActive(true);
    }
}
