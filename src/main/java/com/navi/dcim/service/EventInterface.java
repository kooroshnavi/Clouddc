package com.navi.dcim.service;

import com.navi.dcim.form.EventForm;
import com.navi.dcim.model.Event;
import com.navi.dcim.model.Person;

import java.time.LocalDate;
import java.util.List;

public interface EventInterface {


    void eventRegister(EventForm eventForm, Person person);
    void eventRegister(EventForm eventForm);
    Event getEvent(int eventId);
    List<Event> getEventList();
    void updateEvent(int eventId, EventForm eventForm);
    List<Event> getPendingEventList();

    static LocalDate getTime(){
        return LocalDate.now();
    }
}
