package com.navi.dcim.event;

import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;

public interface EventService {


    void eventRegister(EventForm eventForm);

    Event getEvent(Long eventId);

    List<EventType> getEventTypeList();

    Model modelForEventRegisterForm(Model model);

    Model modelForEventController(Model model);

    Model modelForEventDetail(Model model, Long eventId);

    Model modelForEventList(Model model);

    List<Event> getEventList();

    void updateEvent(Long eventId, EventForm eventForm);

    List<Event> getPendingEventList();

    static LocalDate getTime() {
        return LocalDate.now();
    }

}
