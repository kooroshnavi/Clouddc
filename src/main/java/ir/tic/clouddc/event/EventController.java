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
    public String showEventForm(Model model, @RequestParam("categoryId") int categoryId) {
        eventService.getEventRegisterFormModel(model, categoryId);
        return "eventRegister";
    }

    @GetMapping("/category/{categoryId}/list")
    public String showCategoryEventList(Model model, @RequestParam("categoryId") int categoryId) {
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
            , @ModelAttribute("eventForm") EventForm eventForm
            , @RequestParam("attachment") MultipartFile file) throws IOException {

        if (!file.isEmpty()) {
            eventForm.setFile(file);
        }
        eventService.eventRegister(eventForm);
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
            , @ModelAttribute("eventForm") EventForm eventForm
            , @RequestParam("attachment") MultipartFile file)
            throws IOException {

        if (!file.isEmpty()) {
            eventForm.setFile(file);
        }
        Event event = eventService.getEvent(eventForm.getEventId());
        eventService.updateEvent(eventForm, event);
        eventService.getEventListModel(model);
        return "redirect:eventListView";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        eventService.modelForEventController(model);
    }

}
