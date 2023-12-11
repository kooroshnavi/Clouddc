package com.navi.dcim.event;

import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;

public interface EventService {


    void eventRegister(EventForm eventForm);

    Event getEvent(int eventId);

    List<EventType> getEventTypeList();

    Model modelForEventRegisterForm(Model model);

    Model modelForEventController(Model model);

    Model modelForEventUpdate(Model model, int eventId);

    Model modelForEventList(Model model);

    List<Event> getEventList();

    void updateEvent(int eventId, EventForm eventForm);

    List<Event> getPendingEventList();

    static LocalDate getTime() {
        return LocalDate.now();
    }

}
