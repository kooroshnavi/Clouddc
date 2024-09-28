package ir.tic.clouddc.person;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/person")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/list")
    public String showPersonList(Model model) {
        List<PersonService.PersonProjection_1> personProjection1List = personService
                .getRegisteredPerosonList()
                .stream()
                .sorted(Comparator.comparing(PersonService.PersonProjection_1::getWorkspaceSize).reversed())
                .toList();
        model.addAttribute("personProjection1List", personProjection1List);
        model.addAttribute("personRegisterForm", new PersonRegisterForm());

        return "userView";
    }
}
