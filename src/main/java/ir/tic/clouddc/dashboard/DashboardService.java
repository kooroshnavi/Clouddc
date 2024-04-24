package ir.tic.clouddc.dashboard;

import ir.tic.clouddc.event.EventService;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.task.TaskService;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Slf4j
@Service
public class DashboardService {

    private final PersonService personService;
    private final TaskService taskService;
    private final EventService eventService;

    @Autowired
    public DashboardService(PersonService personService, TaskService taskService, EventService eventService) {
        this.personService = personService;
        this.taskService = taskService;
        this.eventService = eventService;
    }

    public Model prepareDashboardData(Model model) {

        return overallStatistic(model);
    }

    private Model overallStatistic(Model model) {
        long finishedTaskCount = taskService.getFinishedTaskCount(); // Number of finished tasks
        long onTimeTaskCount = taskService.getOnTimeTaskCount();   // Number of finished tasks with zero delay
        long eventCount = eventService.getEventCount(); // Number of Events
        model.addAttribute("totalTaskCount", finishedTaskCount);
        model.addAttribute("onTimeTaskCount", onTimeTaskCount);
        model.addAttribute("eventCount", eventCount);

        return model;
    }


    public Model setDefaultAttributes(Model model) {
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = personService.getPerson(personName);
        model.addAttribute("person", person);
        model.addAttribute("role", authenticated.getAuthorities());
        model.addAttribute("date", UtilService.getCurrentDate());

        return model;
    }
}
