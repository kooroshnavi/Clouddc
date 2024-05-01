package ir.tic.clouddc.event;

import com.github.mfathi91.time.PersianDateTime;
import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.report.DailyReport;
import ir.tic.clouddc.report.ReportService;
import ir.tic.clouddc.task.TaskService;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

        eventDetailRegister(eventForm, event, registerDate);

        event.setDailyReportList(report.get());
        event.setType(eventType);
        eventType.setEvent(event);
        log.info("event added" + report.get().getEventList());
        eventRepository.save(event);

    }

    private void eventDetailRegister(EventForm eventForm, Event event, LocalDateTime eventDetailDate) {
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        EventDetail eventDetail = new EventDetail();
        eventDetail.setPerson(personService.getPerson(SecurityContextHolder.getContext().getAuthentication().getName()));
        eventDetail.setDescription(eventForm.getDescription());
        eventDetail.setUpdated(eventDetailDate);
        eventDetail.setPersianDate(dateTime.format(PersianDateTime.fromGregorian(eventDetailDate)));
        eventDetail.setEvent(event);
        event.setEventDetailList(eventDetail);
    }


    @Override
    public Event getEvent(Long eventId) {
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
        model.addAttribute("role", authenticated.getAuthorities());
        model.addAttribute("date", UtilService.getCurrentDate());
        return model;
    }

    @Override
    public Model modelForEventRegisterForm(Model model) {
        model.addAttribute("centerList", centerService.getCenterList());
        model.addAttribute("eventTypeList", eventTypeRepository.findAll());
        model.addAttribute("eventForm", new EventForm());
        return model;
    }

    @Override
    public Model modelForEventDetail(Model model, Long eventId) {
        List<EventDetail> eventDetailList = getEventDetailList(eventId);

        var firstReport = eventDetailList.get(eventDetailList.size() - 1);

        model.addAttribute("eventDetailList", eventDetailList);
        model.addAttribute("event", this.getEvent(eventId));
        model.addAttribute("id", eventId);
        model.addAttribute("eventForm", new EventForm());
        model.addAttribute("firstReport", firstReport);
        return model;
    }

    private List<EventDetail> getEventDetailList(Long eventId) {
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
    public void updateEvent(Long eventId, EventForm eventForm) {
        Optional<DailyReport> report = reportService.findActive(true);
        Event event = eventRepository.findById(eventId).get();
        eventDetailRegister(eventForm, event, LocalDateTime.now());

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
}
