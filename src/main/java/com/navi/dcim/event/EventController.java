package com.navi.dcim.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@ControllerAdvice
@RequestMapping("/app/main/event")
public class EventController {

    private final EventService eventService;
    @Autowired
    EventController(EventService eventService) {
        this.eventService = eventService;
    }


    @GetMapping("/register/form")
    public String eventForm(Model model) {
        eventService.modelForEventRegisterForm(model);
        return "eventRegisterForm";
    }
    @PostMapping("/register/form/submit")
    public String eventPost(
            Model model,
            @ModelAttribute("eventRegister") EventForm eventForm) {

        eventService.eventRegister(eventForm);
        eventService.modelForEventList(model);
        return "eventList";
    }

    @GetMapping("/view")
    public String viewEvent(Model model) {
        eventService.modelForEventList(model);
        return "eventList";
    }

    @GetMapping("/{id}")
    public String viewEvent(Model model, @PathVariable int id) {
        eventService.modelForEventUpdate(model, id);
        return "eventUpdate";
    }

    @PostMapping("/form/update/{id}")
    public String updateEvent(Model model
            , @PathVariable int id
            , @ModelAttribute("eventForm") EventForm eventForm) {
        eventService.updateEvent(id, eventForm);
        eventService.modelForEventList(model);
        return "eventList";
    }


    @ModelAttribute
    public void addAttributes(Model model) {
        eventService.modelForEventController(model);
    }

}
