package ir.tic.clouddc.dashboard;


import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.pm.PmService;
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

    private final PmService pmService;

    @Autowired
    public GlobalModelAttribute(PersonService personService, PmService pmService) {
        this.personService = personService;
        this.pmService = pmService;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        //long workspaceSize = pmService.getWorkspaceSize();
        model.addAttribute("person", personService.getCurrentPerson());
        model.addAttribute("date", UtilService.getCurrentDate());
        // model.addAttribute("workspaceSize", workspaceSize);
    }

}