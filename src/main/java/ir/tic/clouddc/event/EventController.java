package ir.tic.clouddc.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    @GetMapping("/category/{categoryId}/form")
    public String showEventForm(Model model, @RequestParam("categoryId") short categoryId) {
        eventService.getEventRegisterFormModel(model, categoryId);
        return "eventRegister";
    }

    @GetMapping("/category/{categoryId}/list")
    public String showCategoryEventList(Model model, @RequestParam("categoryId") short categoryId) {
        eventService.getEventListByCategoryModel(model, categoryId);
        return "eventListView";
    }

    @GetMapping("/list")
    public String showEventList(Model model) {
        eventService.getEventListModel(model);
        return "eventListView";
    }

    @PostMapping("/register")
    public String eventPost(
            Model model
            , @ModelAttribute("eventForm") EventRegisterForm eventRegisterForm
            , @RequestParam("attachment") MultipartFile file) throws IOException {

        if (!file.isEmpty()) {
            eventRegisterForm.setFile(file);
        }
        eventService.eventRegister(eventRegisterForm);
        eventService.getEventListModel(model);
        return "redirect:eventListView";
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
