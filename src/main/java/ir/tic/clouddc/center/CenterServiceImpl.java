package ir.tic.clouddc.center;

import com.github.mfathi91.time.PersianDate;
import ir.tic.clouddc.log.PersistenceService;
import ir.tic.clouddc.notification.NotificationService;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.report.DailyReport;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@EnableScheduling
public class CenterServiceImpl implements CenterService {

    private final CenterRepository centerRepository;

    private final TemperatureRepository temperatureRepository;

    private final PersonService personService;

    private final NotificationService notificationService;

    private final PersistenceService persistenceService;

    @Autowired
    CenterServiceImpl(CenterRepository centerRepository, TemperatureRepository temperatureRepository, PersonService personService, NotificationService notificationService, PersistenceService persistenceService) {
        this.centerRepository = centerRepository;
        this.temperatureRepository = temperatureRepository;
        this.personService = personService;
        this.notificationService = notificationService;

        this.persistenceService = persistenceService;
    }
/*
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
    }*/

    @Override
    public Salon getCenter(int centerId) {
        return centerRepository.findById(centerId).get();
    }

    @Override
    public List<Salon> getDefaultCenterList() {
        List<Integer> centerIds = Arrays.asList(1, 2, 3);
        return centerRepository.findAllByIdIn(centerIds);
    }

    @Override
    public List<Salon> getCenterList() {
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
    public List<Temperature> saveDailyTemperature(TemperatureForm temperatureForm, DailyReport dailyReport) {
        var time = UtilService.getTime();
        var personId = personService.getPersonId(personService.getCurrentUsername());
        var temp1 = temperatureForm.getSalon1Temp();
        var temp2 = temperatureForm.getSalon2Temp();
        List<Temperature> dailyTemps = new ArrayList<>();

        if (!temp1.isEmpty() && Float.parseFloat(temperatureForm.getSalon1Temp()) >= 5.0 && Float.parseFloat(temperatureForm.getSalon1Temp()) <= 50.0) {
            Temperature salon1Temperature = new Temperature();
            var persistence = persistenceService.persistenceSetup(UtilService.getDATE(), UtilService.getTime(), ' ', new Person(personId), true);
            salon1Temperature.setPersistence(persistence);
            salon1Temperature.setTime(time);
            salon1Temperature.setValue(Float.parseFloat(temperatureForm.getSalon1Temp()));
            salon1Temperature.setSalon(getCenter(1));
            salon1Temperature.setDailyReport(dailyReport);
            dailyTemps.add(salon1Temperature);

        }

        if (!temp2.isEmpty() && Float.parseFloat(temperatureForm.getSalon2Temp()) >= 5.0 && Float.parseFloat(temperatureForm.getSalon2Temp()) <= 50.0) {
            Temperature salon2Temperature = new Temperature();
            var persistence = persistenceService.persistenceSetup(UtilService.getDATE(), UtilService.getTime(), ' ', new Person(personId), true);
            salon2Temperature.setPersistence(persistence);
            salon2Temperature.setTime(time);
            salon2Temperature.setValue(Float.parseFloat(temperatureForm.getSalon2Temp()));
            salon2Temperature.setSalon(getCenter(2));
            salon2Temperature.setDailyReport(dailyReport);
            dailyTemps.add(salon2Temperature);
        }

        if (!dailyTemps.isEmpty()) {
            temperatureRepository.saveAll(dailyTemps);
        }

        return getTemperatureHistoryList();
    }

    @Override
    public void setDailyTemperatureReport(DailyReport currentReport) {
        DecimalFormat df = new DecimalFormat("##.#");
        var salon1 = getCenter(1);
        var salon2 = getCenter(2);
        List<Float> salon1DailyTemperatures = temperatureRepository.getDailytemperatureList(salon1, currentReport);
        List<Float> salon2DailyTemperatures = temperatureRepository.getDailytemperatureList(salon2, currentReport);

        float salonAverage = 0;
        List<Salon> salonList = new ArrayList<>();

        if (!salon1DailyTemperatures.isEmpty()) {
            for (float temp : salon1DailyTemperatures) {
                salonAverage += temp;
            }
            salonAverage /= salon1DailyTemperatures.size();

            if (salon1.getAverageTemperature() == null) {
                Map<LocalDate, Float> averageMap1 = new HashMap<>();
                averageMap1.put(currentReport.getDate(), Float.parseFloat(df.format(salonAverage)));
                salon1.setAverageTemperature(averageMap1);
            } else {
                salon1.getAverageTemperature().put(currentReport.getDate(), Float.parseFloat(df.format(salonAverage)));
            }
            salonList.add(salon1);
        }

        if (!salon2DailyTemperatures.isEmpty()) {
            salonAverage = 0;
            for (float temp : salon2DailyTemperatures) {
                salonAverage += temp;
            }
            salonAverage /= salon2DailyTemperatures.size();

            if (salon2.getAverageTemperature() == null) {
                Map<LocalDate, Float> averageMap2 = new HashMap<>();
                averageMap2.put(currentReport.getDate(), Float.parseFloat(df.format(salonAverage)));
                salon2.setAverageTemperature(averageMap2);
            } else {
                salon2.getAverageTemperature().put(currentReport.getDate(), Float.parseFloat(df.format(salonAverage)));
            }
            salonList.add(salon2);
        }

        if (!salonList.isEmpty()) {
            centerRepository.saveAllAndFlush(salonList);
        }
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

    @Override
    public List<Float> getWeeklyTemperature(List<LocalDate> weeklyDateList, int centerId) {
        var center = getCenter(centerId);
        List<Float> weeklyTemperature = new ArrayList<>();
        for (LocalDate date : weeklyDateList) {
            weeklyTemperature.add(center.getAverageTemperature().get(date));
        }
        return weeklyTemperature;
    }

}
