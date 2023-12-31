package com.navi.dcim.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
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

    @GetMapping("/detail")
    public String viewEvent(@RequestParam Long eventId, Model model) {
        eventService.modelForEventDetail(model, eventId);
        return "eventDetail";
    }

    @GetMapping("/detail/form")
    public String viewDetail(Model model, @RequestParam Long eventId) {
        //addAttributes(model);
        model.addAttribute("eventForm", new EventForm());
        model.addAttribute("event",eventService.getEvent(eventId));
        return "eventUpdate";
    }

    @PostMapping("/update")
    public String updateEvent(Model model
            , @RequestParam Long eventId
            , @ModelAttribute("eventForm") EventForm eventForm) {
        eventService.updateEvent(eventId, eventForm);
        eventService.modelForEventList(model);
        return "eventList";
    }


    @ModelAttribute
    public void addAttributes(Model model) {
        eventService.modelForEventController(model);
    }

}
