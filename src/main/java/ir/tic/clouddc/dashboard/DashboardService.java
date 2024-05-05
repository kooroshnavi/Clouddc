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

import java.util.List;
import java.util.Map;

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
        Map<Integer, Float> salon1TemperatureMapping = centerService.getWeeklyTemperature(reportService.getWeeklyDate(), 1);
        Map<Integer, Float> salon2TemperatureMapping = centerService.getWeeklyTemperature(reportService.getWeeklyDate(), 2);
        List<Integer> salon1WeeklyDate = salon1TemperatureMapping.keySet().stream().toList();
        List<Float> salon1WeeklyTemp = salon1TemperatureMapping.values().stream().toList();

        List<Float> salon2WeeklyTemp = salon2TemperatureMapping.values().stream().toList();

        log.info("day:" + salon1WeeklyDate);

        model.addAttribute("salon1WeeklyDate", salon1WeeklyDate);
        model.addAttribute("salon1WeeklyTemp", salon1WeeklyTemp);
        model.addAttribute("salon2WeeklyTemp", salon2WeeklyTemp);
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
