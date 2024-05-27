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
        return "eventLandingPage";
    }

    @GetMapping("/category/{id}/form")
    public String ShowEventForm(Model model, @RequestParam("id") int id) {
        eventService.getEventRegisterFormModel(model, id);
        switch (id) {
            case 1 -> {
                return "eventFormForDeviceFailure";
            }
            case 2 -> {
                return "eventFormForCenterVisit";
            }
            case 3 -> {
                return "eventFormForSalon";
            }
        }
        return "eventRegister";
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

    @GetMapping("/list")
    public String viewEventList(Model model) {
        eventService.getEventListModel(model);
        return "eventListView";
    }

    @GetMapping("/view")
    public String viewEvent(@RequestParam Long eventId, Model model) {
        eventService.getEventDetailModel(model, eventId);
        return "eventDetailList";
    }

    @PostMapping("/update")
    public String updateEvent(Model model
            , @RequestParam Long eventId
            , @ModelAttribute("eventForm") EventForm eventForm
            , @RequestParam("attachment") MultipartFile file)
            throws IOException {

        eventForm.setFile(file);
        eventService.updateEvent(eventId, eventForm);
        eventService.getEventListModel(model);
        return "events";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        eventService.modelForEventController(model);
    }

}
