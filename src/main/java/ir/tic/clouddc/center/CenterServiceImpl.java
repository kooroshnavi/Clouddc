package ir.tic.clouddc.center;

import com.github.mfathi91.time.PersianDate;
import ir.tic.clouddc.notification.NotificationService;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.report.ReportService;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@EnableScheduling
public class CenterServiceImpl implements CenterService {

    private final CenterRepository centerRepository;

    private final TemperatureRepository temperatureRepository;

    private final PersonService personService;

    private final ReportService reportService;

    private final NotificationService notificationService;

    @Autowired
    CenterServiceImpl(CenterRepository centerRepository, TemperatureRepository temperatureRepository, PersonService personService, ReportService reportService, NotificationService notificationService) {
        this.centerRepository = centerRepository;
        this.temperatureRepository = temperatureRepository;
        this.personService = personService;
        this.reportService = reportService;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 14 * * SAT,SUN,MON,TUE,WED")
    public void dailyTemperatureCheck() {
        var dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        var todayReport = reportService.findActive(true);
        var todayTemperatureSalon1 = temperatureRepository.existsBydailyReportAndCenter(todayReport.get(), getCenter(1));

        if (!todayTemperatureSalon1) {
            log.info("Salon 1 is absent");
            notificationService.sendTemperatureReminderMessage(dateTime);
        } else {
            var todayTemperatureSalon2 = temperatureRepository.existsBydailyReportAndCenter(todayReport.get(), getCenter(2));
            if (!todayTemperatureSalon2) {
                log.info("Salon 2 is absent");
                notificationService.sendTemperatureReminderMessage(dateTime);
            }
        }
    }

    @Override
    public Center getCenter(int centerId) {
        return centerRepository.findById(centerId).get();
    }

    @Override
    public List<Center> getDefaultCenterList() {
        List<Integer> centerIds = Arrays.asList(1, 2, 3);
        return centerRepository.findAllByIdIn(centerIds);
    }

    @Override
    public List<Center> getCenterList() {
        return centerRepository.findAll();
    }

    @Override
    public Model modelForCenterController(Model model) {
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = personService.getPerson(personName);
        model.addAttribute("person", person);
        model.addAttribute("role", authenticated.getAuthorities());
        model.addAttribute("date", UtilService.getCurrentDate());
        return model;
    }

    @Override
    public List<Temperature> saveDailyTemperature(TemperatureForm temperatureForm) {
        var time = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
        var person = personService.getPerson(SecurityContextHolder.getContext().getAuthentication().getName());
        var todayReport = reportService.findActive(true).get();
        var temp1 = temperatureForm.getSalon1Temp();
        var temp2 = temperatureForm.getSalon2Temp();
        List<Temperature> dailyTemps = new ArrayList<>();

        if (!temp1.isEmpty() && Float.parseFloat(temperatureForm.getSalon1Temp()) >= 5.0 && Float.parseFloat(temperatureForm.getSalon1Temp()) <= 50.0) {
            Temperature salon1Temperature = new Temperature();
            salon1Temperature.setTime(time);
            salon1Temperature.setValue(Float.parseFloat(temperatureForm.getSalon1Temp()));
            salon1Temperature.setCenter(getCenter(1));
            salon1Temperature.setPerson(person);
            salon1Temperature.setDailyReport(todayReport);
            dailyTemps.add(salon1Temperature);
        }

        if (!temp2.isEmpty() && Float.parseFloat(temperatureForm.getSalon2Temp()) >= 5.0 && Float.parseFloat(temperatureForm.getSalon2Temp()) <= 50.0) {
            Temperature salon2Temperature = new Temperature();
            salon2Temperature.setTime(time);
            salon2Temperature.setValue(Float.parseFloat(temperatureForm.getSalon2Temp()));
            salon2Temperature.setCenter(getCenter(2));
            salon2Temperature.setPerson(person);
            salon2Temperature.setDailyReport(todayReport);
            dailyTemps.add(salon2Temperature);
        }

        if (!dailyTemps.isEmpty()) {
            temperatureRepository.saveAll(dailyTemps);
        }

        return getTemperatureHistoryList();
    }

    @Override
    public List<Temperature> getTemperatureHistoryList() {
        List<Temperature> temperatureList = temperatureRepository
                .findAll()
                .stream()
                .sorted(Comparator.comparing(Temperature::getId).reversed())
                .toList();

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        for (Temperature temperature : temperatureList) {
            var origDate = temperature.getDailyReport().getDate();
            temperature.getDailyReport().setPersianDate(date.format(PersianDate.fromGregorian(origDate)));
        }
        return temperatureList;
    }

}
