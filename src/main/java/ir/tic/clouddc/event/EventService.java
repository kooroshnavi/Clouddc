package ir.tic.clouddc.event;

import org.springframework.ui.Model;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface EventService {


    void eventRegister(EventForm eventForm) throws IOException;

    Event getEvent(Long eventId);

    List<EventType> getEventTypeList();

    void deleteEventAttchment(long metaDataId);

    Model modelForEventRegisterForm(Model model);

    Model modelForEventController(Model model);

    Model modelForEventDetail(Model model, Long eventId);

    Model modelForEventList(Model model);

    List<Event> getEventList();

    void updateEvent(Long eventId, EventForm eventForm) throws IOException;

    List<Event> getPendingEventList();

    static LocalDate getTime() {
        return LocalDate.now();
    }

    long getEventCount();

    long getActiveEventCount();

    List<Long> getEventTypeCount();

    int getWeeklyRegisteredPercentage();

    int getActiveEventPercentage();

}
