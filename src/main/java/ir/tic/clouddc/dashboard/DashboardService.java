package ir.tic.clouddc.dashboard;

import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.event.EventService;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.report.ReportService;
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
    private final CenterService centerService;
    private final ReportService reportService;

    @Autowired
    public DashboardService(PersonService personService, TaskService taskService, EventService eventService, CenterService centerService, ReportService reportService) {
        this.personService = personService;
        this.taskService = taskService;
        this.eventService = eventService;
        this.centerService = centerService;
        this.reportService = reportService;
    }

    public Model prepareDashboardData(Model model) {

        return overallStatistic(model);
    }

    private Model overallStatistic(Model model) {
        long finishedTaskCount = taskService.getFinishedTaskCount(); // Number of finished tasks
        long activeTaskCount = taskService.getActiveTaskCount();
        long onTimeTaskCount = taskService.getOnTimeTaskCount();   // Number of finished tasks with zero delay
        long eventCount = eventService.getEventCount(); // Number of Events
        long activeEventCount = eventService.getActiveEventCount();
        model.addAttribute("totalTaskCount", finishedTaskCount);
        model.addAttribute("activeTaskCount", activeTaskCount);
        model.addAttribute("onTimeTaskCount", onTimeTaskCount);
        model.addAttribute("eventCount", eventCount);
        model.addAttribute("activeEventCount", activeEventCount);
        model.addAttribute("eventTypeCount", eventService.getEventTypeCount());

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
