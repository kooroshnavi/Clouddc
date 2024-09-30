package ir.tic.clouddc.person;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/person")
@Slf4j
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

        if (!model.containsAttribute("personRegisterForm")) {
            model.addAttribute("personRegisterForm", new PersonRegisterForm());
        }
        if (!model.containsAttribute("exist")) {
            model.addAttribute("exist", false);
        }
        if (!model.containsAttribute("newPerson")) {
            model.addAttribute("newPerson", false);
        }
        if (!model.containsAttribute("registerInitialized")) {
            model.addAttribute("registerInitialized", false);
        }
        if (!model.containsAttribute("badOTP")) {
            model.addAttribute("badOTP", false);
        }
        if (!model.containsAttribute("update")) {
            model.addAttribute("update", false);
        }

        return "userView";
    }

    @RequestMapping(value = "/OTPForm", method = {RequestMethod.GET, RequestMethod.POST})
    public String OTPForm(Model model, RedirectAttributes redirectAttributes, @Valid @ModelAttribute("personRegisterForm") PersonRegisterForm personRegisterForm) throws ExecutionException {

        var exist = personService.checkPhoneExistence(personRegisterForm);
        if (exist) {
            redirectAttributes.addFlashAttribute("exist", true);
            redirectAttributes.addFlashAttribute("personRegisterForm", personRegisterForm);

            return "redirect:/person/list";
        }
        String expiryTime = personService.initPhoneRegister(personRegisterForm.getPhoneNumber());
        LocalDateTime expiry = LocalDateTime.parse(expiryTime);
        redirectAttributes.addFlashAttribute("secondsLeft", LocalDateTime.now().until(expiry, ChronoUnit.SECONDS));
        redirectAttributes.addFlashAttribute("registerInitialized", true);
        redirectAttributes.addFlashAttribute("personRegisterForm", personRegisterForm);
        if (!model.containsAttribute("badOTP")) {
            redirectAttributes.addFlashAttribute("badOTP", false);
        } else {
            redirectAttributes.addFlashAttribute("badOTP", true);
        }

        return "redirect:/person/list";
    }

    @PostMapping("/register")
    private String registerPerson(RedirectAttributes redirectAttributes, @Valid @ModelAttribute("personRegisterForm") PersonRegisterForm personRegisterForm) throws ExecutionException {
        if (personRegisterForm.getPersonId() != null) {
            log.info("Updating Person");// Update Person Info
            personService.registerNewPerson(personRegisterForm);
        } else {
            var exist = personService.checkPhoneExistence(personRegisterForm);
            if (exist) {
                redirectAttributes.addFlashAttribute("exist", true);
                redirectAttributes.addFlashAttribute("personRegisterForm", personRegisterForm);

                return "redirect:/person/list";
            }
            String OTPValidate = personService.validateOTP(personRegisterForm);
            if (OTPValidate.equals("0")) {
                personService.registerNewPerson(personRegisterForm);
            } else {
                redirectAttributes.addFlashAttribute("personRegisterForm", personRegisterForm);
                redirectAttributes.addFlashAttribute("badOTP", true);

                return "redirect:/person/OTPForm";
            }
        }
        redirectAttributes.addFlashAttribute("newPerson", true);

        return "redirect:/person/list";
    }

    @GetMapping("/{personId}/detail")
    public String getPersonDetailUpdate(RedirectAttributes redirectAttributes, @PathVariable Integer personId) {
        var person = personService.getReferencedPerson(personId);
        PersonRegisterForm personRegisterForm = new PersonRegisterForm();
        personRegisterForm.setPersonId(person.getId());
        personRegisterForm.setEnabled(person.isEnabled());
        personRegisterForm.setRoleCode(person.getRole());
        personRegisterForm.setFreeWorkSpace(person.getWorkSpaceSize() <= 0);
        redirectAttributes.addFlashAttribute("update", true);
        redirectAttributes.addFlashAttribute("personRegisterForm", personRegisterForm);

        return "redirect:/person/list";
    }

}
