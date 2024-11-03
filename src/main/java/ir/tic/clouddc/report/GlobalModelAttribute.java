package ir.tic.clouddc.report;


import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@ControllerAdvice
public class GlobalModelAttribute {

    private final PersonService personService;

    @Autowired
    public GlobalModelAttribute(PersonService personService) {
        this.personService = personService;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        //long workspaceSize = pmService.getWorkspaceSize();
        model.addAttribute("person", personService.getCurrentPerson());
        model.addAttribute("date", UtilService.getCurrentDate());
        // model.addAttribute("workspaceSize", workspaceSize);
    }

}