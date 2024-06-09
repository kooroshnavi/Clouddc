package ir.tic.clouddc.event;

import org.springframework.ui.Model;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface EventService {


    void eventRegister(EventRegisterForm eventRegisterForm) throws IOException;

    Event getEvent(Long eventId);

    List<EventCategory> getEventCategoryList();

    Model getEventRegisterFormModel(Model model, int eventCategory);

    Model modelForEventController(Model model);

    Model getEventDetailModel(Model model, Long eventId);

    Model getEventListModel(Model model);

    Model getEventListByCategoryModel(Model model, int categoryId);

    void updateEvent(EventRegisterForm eventRegisterForm, Event event) throws IOException;

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
