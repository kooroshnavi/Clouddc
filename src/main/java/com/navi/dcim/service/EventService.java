package com.navi.dcim.service;

import com.github.mfathi91.time.PersianDateTime;
import com.navi.dcim.form.EventForm;
import com.navi.dcim.model.DailyReport;
import com.navi.dcim.model.Event;
import com.navi.dcim.model.Person;
import com.navi.dcim.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventService implements EventInterface{

    private final TaskStatusRepository taskStatusRepository;
    private final TaskRepository taskRepository;
    private final TaskDetailRepository taskDetailRepository;
    private final PersonRepository personRepository;
    private final CenterRepository centerRepository;
    private final EventTypeRepository eventTypeRepository;
    private final EventRepository eventRepository;
    private final ReportRepository reportRepository;

    public EventService(TaskStatusRepository taskStatusRepository
            , TaskRepository taskRepository
            , TaskDetailRepository taskDetailRepository
            , PersonRepository personRepository
            , CenterRepository centerRepository
            , EventTypeRepository eventTypeRepository
            , EventRepository eventRepository
            , ReportRepository reportRepository) {
        this.taskStatusRepository = taskStatusRepository;
        this.taskRepository = taskRepository;
        this.taskDetailRepository = taskDetailRepository;
        this.personRepository = personRepository;
        this.centerRepository = centerRepository;
        this.eventTypeRepository = eventTypeRepository;
        this.eventRepository = eventRepository;
        this.reportRepository = reportRepository;
    }

    @Override
    public void eventRegister(EventForm eventForm, Person person) {
            DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            DailyReport report = reportRepository.findByActive(true);
            var eventType = eventTypeRepository.findById(eventForm.getEventType()).get();
            var registerDate = LocalDateTime.now();
            Event event = new Event(registerDate
                    , registerDate
                    , eventForm.isActive()
                    , " ( "
                    + dateTime.format(PersianDateTime.fromGregorian(registerDate))
                    + "-->" + eventForm.getDescription()
                    + " ) "
                    + System.lineSeparator()
                    , eventType
                    , person
                    , centerRepository.findById(eventForm.getCenterId()).get());
            event.setDailyReportList(report);
            event.setType(eventType);
            eventType.setEvent(event);
            System.out.println("event added" + report.getEventList());
            eventTypeRepository.save(eventType);

    }

    @Override
    public void eventRegister(EventForm eventForm) {

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
    public List<Event> getEventList() {
        return null;
    }

    @Override
    public void updateEvent(int eventId, EventForm eventForm) {
        DailyReport report = reportRepository.findByActive(true);
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
        return null;
    }
}
