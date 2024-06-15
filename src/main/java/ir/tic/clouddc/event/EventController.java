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
        return "eventLandingPage";   /// 1. Category Selection
    }

    @GetMapping("/category/target/{target}/form")
    public String showDeviceEventForm(Model model, @RequestParam("target") short target) {

        eventService.getEventLandingForm(model , target);

        return "eventLandingForm";    /// 2.  Input desired serial number (device) OR choose location for status OR center for visit
    }

    @PostMapping("/device/detail")
    public String showEventStatusModel(Model model
            , @Nullable @ModelAttribute("eventEntryForm") EventRegisterForm eventRegisterForm
            , @Nullable EventRegisterForm fromImportantDevicePmForm) {

        eventService.getEventStatusModel(model, eventRegisterForm, fromImportantDevicePmForm);

        return "eventStatusForm";   /// 3.    update status

    }

    @PostMapping("/register")
    public String eventPost(     ///    4.  Event register
            Model model
            , @Nullable @ModelAttribute("eventRegisterForm") EventRegisterForm eventRegisterForm   /// general event
            , @Nullable @ModelAttribute("deviceStatusForm") DeviceStatusForm deviceStatusForm   /// device status event
            , @Nullable @ModelAttribute("locationStatusForm") LocationStatusForm locationStatusForm /// location status event
            , @RequestParam("attachment") MultipartFile file) throws IOException {

        if (!file.isEmpty()) {
            if (!Objects.isNull(eventRegisterForm)) {
                eventRegisterForm.setFile(file);
            } else {
            //    assert deviceStatusForm != null;
                deviceStatusForm.setFile(file);
            }
        }
        eventService.eventRegister(eventRegisterForm, deviceStatusForm, locationStatusForm);
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
