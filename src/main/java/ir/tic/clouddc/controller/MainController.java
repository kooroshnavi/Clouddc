package ir.tic.clouddc.controller;

import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.utils.UtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = {"/", ""})
public class MainController {

    private final PersonService personService;
    @Autowired
    public MainController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public String index(Model model) {
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = personService.getPerson(personName);
        model.addAttribute("person", person);
        model.addAttribute("date", UtilityService.getCurrentPersianDate());
        return "mainDashboard";
    }

}
