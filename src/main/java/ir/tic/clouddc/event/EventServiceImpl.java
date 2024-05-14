package ir.tic.clouddc.event;

import com.github.mfathi91.time.PersianDate;
import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.document.FileService;
import ir.tic.clouddc.document.MetaData;
import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.report.ReportService;
import ir.tic.clouddc.pm.PmService;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
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
    private final EventTypeRepository eventTypeRepository;
    private final FileService fileService;


    @Autowired
    public EventServiceImpl(
            EventRepository eventRepository
            , EventDetailRepository eventDetailRepository, ReportService reportService
            , CenterService centerService
            , PersonService personService
            , PmService pmService
            , EventTypeRepository eventTypeRepository, FileService fileService) {
        this.eventRepository = eventRepository;
        this.eventDetailRepository = eventDetailRepository;
        this.reportService = reportService;
        this.centerService = centerService;
        this.personService = personService;
        this.pmService = pmService;
        this.eventTypeRepository = eventTypeRepository;
        this.fileService = fileService;
    }

    @Override
    public void eventRegister(EventForm eventForm) throws IOException {
        var eventType = new EventType(eventForm.getEventType());

        Event event = new Event(eventForm.isActive()
                , new EventType(eventForm.getEventType())
                , centerService.getCenter(eventForm.getCenterId()));

        eventDetailRegister(eventForm, event);

        event.setType(eventType);
        eventType.setEvent(event);
        eventRepository.save(event);

    }

    private void eventDetailRegister(EventForm eventForm, Event event) throws IOException {
        var registerTime = LocalTime.now();
        EventDetail eventDetail = new EventDetail();
        var persistence = persistenceSetup(registerTime, eventForm);
        eventDetail.setPersistence(persistence);
        eventDetail.setDescription(eventForm.getDescription());
        eventDetail.setEvent(event);
        if (event.getEventDetailList() == null) {
            List<EventDetail> eventDetailList = new ArrayList<>();
            eventDetailList.add(eventDetail);
            event.setEventDetailList(eventDetailList);
        } else {
            event.getEventDetailList().add(eventDetail);
        }
    }

    private Persistence persistenceSetup(LocalTime registerTime, EventForm eventForm) throws IOException {
        Persistence persistence = new Persistence();
        var person = personService.getPerson(SecurityContextHolder.getContext().getAuthentication().getName());
        var report = reportService.findActive(true).get();
        persistence.setDailyReport(report);
        persistence.setTime(registerTime);
        persistence.setPerson(person);
        if (eventForm.getFile().getSize() != 0) {
            return fileService.registerAttachment(eventForm.getFile(), persistence);
        }
        return persistence;
    }


    @Override
    public Event getEvent(Long eventId) {
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        Event event = eventRepository.findById(eventId).get();
        event.setPersianDate(dateTime.format(PersianDate.fromGregorian(event.getEventDetailList().get(0).getPersistence().getDailyReport().getDate())));
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
        List<EventDetail> eventDetailList = getEventDetailList(getEvent(eventId));
        List<Long> persistenceIdList = eventDetailRepository.getPersistenceIdList(getEvent(eventId));
        List<MetaData> metaDataList = fileService.getRelatedMetadataList(persistenceIdList);
        if (metaDataList.size() > 0) {
            model.addAttribute("metaDataList", metaDataList);
        }
        var firstReport = eventDetailList.get(eventDetailList.size() - 1);
        model.addAttribute("eventDetailList", eventDetailList);
        model.addAttribute("event", this.getEvent(eventId));
        model.addAttribute("id", eventId);
        model.addAttribute("eventForm", new EventForm());
        model.addAttribute("firstReport", firstReport);
        return model;
    }

    private List<EventDetail> getEventDetailList(Event event) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        for (EventDetail eventDetail : event.getEventDetailList()
        ) {
            var date = eventDetail.getPersistence().getDailyReport().getDate();
            eventDetail.setPersianDate(dateFormatter.format
                    (PersianDate
                            .fromGregorian
                                    (date)));
            eventDetail.setPersianDay(UtilService.persianDay.get(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault())));
            eventDetail.setTime(timeFormatter.format(eventDetail.getPersistence().getTime()));
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
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        List<Event> eventList = eventRepository.findAll(Sort.by("active").descending());
        for (Event event : eventList) {
            var date = event.getEventDetailList().get(0).getPersistence().getDailyReport().getDate();
            event.setPersianDate(dateFormatter.format(PersianDate.fromGregorian(date)));
            event.setTime(timeFormatter.format(event.getEventDetailList().get(0).getPersistence().getTime()));
            event.setPersianDay(UtilService.persianDay.get(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault())));
        }

        return eventList;
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
