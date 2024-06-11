package ir.tic.clouddc.event;

import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Controller
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;

    @Autowired
    EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/category")
    public String eventForm(Model model) {
        model.addAttribute("eventCategoryList", eventService.getEventCategoryList());
        return "eventLandingPage";
    }

    @GetMapping("/device/landingForm")
    public String showDeviceEventForm(Model model) {
        eventService.getDeviceEventEntryForm(model);
        return "deviceEventLandingForm";
    }

    @PostMapping("/device/detail")
    public String showDeviceEventDetailForm(Model model
            , @Nullable @ModelAttribute("eventEntryForm") EventRegisterForm eventRegisterForm
            , @Nullable EventRegisterForm fromImportantDevicePmForm) {

        eventService.getRelatedDeviceEventModel(model, eventRegisterForm, fromImportantDevicePmForm);

        return "deviceEventDetailForm";

    }

    @PostMapping("/register")
    public String eventPost(
            Model model
            , @Nullable @ModelAttribute("eventRegisterForm") EventRegisterForm eventRegisterForm
            , @Nullable @ModelAttribute("deviceCheckListForm") DeviceStatusForm deviceStatusForm
            , @RequestParam("attachment") MultipartFile file) throws IOException {

        if (!file.isEmpty()) {
            if (!Objects.isNull(eventRegisterForm)) {
                eventRegisterForm.setFile(file);
            } else {
                assert deviceStatusForm != null;
                deviceStatusForm.setFile(file);
            }
        }
        eventService.eventRegister(eventRegisterForm, deviceStatusForm);
        eventService.getEventListModel(model);
        return "redirect:eventListView";
    }

    @GetMapping("/category/{categoryId}/list")
    public String showCategoryEventList(Model model, @Nullable @RequestParam("categoryId") Short categoryId) {
        if (categoryId != null) {
            eventService.getEventListByCategoryModel(model, categoryId);
        }
        return "eventListView";
    }

    @GetMapping("/list")
    public String showEventList(Model model) {
        eventService.getEventListModel(model);
        return "eventListView";
    }

    @GetMapping("/{eventId}/detail")
    public String viewEventDetail(@RequestParam Long eventId,
                                  Model model) {
        eventService.getEventDetailModel(model, eventId);
        return "eventDetailList";
    }

    @PostMapping("/update")
    public String updateEvent(Model model
            , @ModelAttribute("eventForm") EventRegisterForm eventRegisterForm
            , @RequestParam("attachment") MultipartFile file)
            throws IOException {

        if (!file.isEmpty()) {
            eventRegisterForm.setFile(file);
        }
        Event event = eventService.getEvent(eventRegisterForm.getEventId());
        eventService.updateEvent(eventRegisterForm, event);
        eventService.getEventListModel(model);
        return "redirect:eventListView";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        eventService.modelForEventController(model);
    }

}
