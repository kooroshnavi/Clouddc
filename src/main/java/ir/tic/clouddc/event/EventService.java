package ir.tic.clouddc.event;

import jakarta.annotation.Nullable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;
import java.util.List;

public interface EventService {


    void eventRegister(@Nullable EventRegisterForm eventRegisterForm
            , @Nullable DeviceStatusForm deviceStatusForm
            , @Nullable LocationStatusForm locationStatusForm) throws IOException;

    Event getEvent(Long eventId);

    List<EventCategory> getEventCategoryList();

    Model getDeviceEventEntryForm(Model model);

    Model modelForEventController(Model model);

    Model getEventDetailModel(Model model, Long eventId);

    Model getEventListModel(Model model);

    Model getEventListByCategoryModel(Model model, int categoryId);

    void updateEvent(EventRegisterForm eventRegisterForm, Event event) throws IOException;

    Model getRelatedDeviceEventModel(Model model, @Nullable @ModelAttribute("eventRegisterForm") EventRegisterForm eventRegisterForm
            , @Nullable EventRegisterForm fromImportantDevicePmForm);

    List<Event> getPendingEventList();

    long getEventCount();

    long getActiveEventCount();

    List<Long> getEventTypeCount();

    int getWeeklyRegisteredPercentage();

    int getActiveEventPercentage();

}
