package ir.tic.clouddc.dashboard;

import com.github.mfathi91.time.PersianDate;
import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.event.EventService;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.report.ReportService;
import ir.tic.clouddc.pm.PmService;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class DashboardService {

    private final PersonService personService;
    private final PmService pmService;
    private final EventService eventService;
    private final CenterService centerService;
    private final ReportService reportService;


    @Autowired
    public DashboardService(PersonService personService, PmService pmService, EventService eventService, CenterService centerService, ReportService reportService) {
        this.personService = personService;
        this.pmService = pmService;
        this.eventService = eventService;
        this.centerService = centerService;
        this.reportService = reportService;
    }

    public Model prepareDashboardData(Model model) {

        return overallStatistic(model);
    }

    private Model overallStatistic(Model model) {

        //   model = taskStatistics(model);
        // model = eventStatistics(model);
        // model = centerStatistics(model);

        return model;
    }

    private Model centerStatistics(Model model) {

        List<LocalDate> weeklyDate = reportService.getWeeklyDate();

        List<Float> salon1TemperatureList = centerService.getWeeklyTemperature(weeklyDate, 1);
        List<Float> salon2TemperatureList = centerService.getWeeklyTemperature(weeklyDate, 2);

        List<String> persianWeeklyDateList = new ArrayList<>();

        for (LocalDate date : weeklyDate) {
            persianWeeklyDateList.add(UtilService.persianDay.get(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()))
                    + " - "
                    + PersianDate.fromGregorian(date).getDayOfMonth()
                    + " "
                    + PersianDate.fromGregorian(date).getMonth().getPersianName());
        }

        model.addAttribute("weeklyDate", persianWeeklyDateList);
        model.addAttribute("salon1WeeklyTemp", salon1TemperatureList);
        model.addAttribute("salon2WeeklyTemp", salon2TemperatureList);

        return model;

    }

    private Model taskStatistics(Model model) {
        long finishedTaskCount = pmService.getFinishedTaskCount(); // Number of finished tasks
        long activeTaskCount = pmService.getActiveTaskCount();
        long onTimeTaskCount = pmService.getOnTimeTaskCount();   // Number of finished tasks with zero delay
        int weeklyFinishedTaskPercentage = pmService.getWeeklyFinishedPercentage();
        model.addAttribute("totalTaskCount", finishedTaskCount);
        model.addAttribute("activeTaskCount", activeTaskCount);
        model.addAttribute("onTimeTaskCount", onTimeTaskCount);
        model.addAttribute("weeklyFinishedTaskPercentage", weeklyFinishedTaskPercentage);
        model.addAttribute("activeDelayedPercentage", pmService.getActiveDelayedPercentage());

        return model;
    }

    private Model eventStatistics(Model model) {
        model.addAttribute("eventCount", eventService.getEventCount());
        model.addAttribute("activeEventCount", eventService.getActiveEventCount());
        model.addAttribute("eventTypeCount", eventService.getEventTypeCount());
        model.addAttribute("weeklyRegisteredPercentage", eventService.getWeeklyRegisteredPercentage());
        model.addAttribute("activeEventPercentage", eventService.getActiveEventPercentage());
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
