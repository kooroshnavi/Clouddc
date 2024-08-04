package ir.tic.clouddc.dashboard;

import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.utils.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class indexController {

    private final PersonService personService;

    @Autowired
    public indexController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public String showIndex() {
        return "index";
    }

    @ModelAttribute
    public Model addAttributes(Model model) {
        model.addAttribute("person", personService.getCurrentPerson());
        model.addAttribute("date", UtilService.getCurrentDate());

        return model;
    }
}
