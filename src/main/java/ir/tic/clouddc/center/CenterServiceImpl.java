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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@EnableScheduling
public class CenterServiceImpl implements CenterService {

    private final CenterRepository centerRepository;

    private final TemperatureRepository temperatureRepository;

    private final PersonService personService;

    private final NotificationService notificationService;

    private final PersistenceService persistenceService;

    private final DataCenterRepository dataCenterRepository;

    private final LocationRepository locationRepository;

    private final RackRepository rackRepository;

    @Autowired
    CenterServiceImpl(CenterRepository centerRepository, TemperatureRepository temperatureRepository, PersonService personService, NotificationService notificationService, PersistenceService persistenceService, DataCenterRepository dataCenterRepository, LocationRepository locationRepository, RackRepository rackRepository) {
        this.centerRepository = centerRepository;
        this.temperatureRepository = temperatureRepository;
        this.personService = personService;
        this.notificationService = notificationService;

        this.persistenceService = persistenceService;
        this.dataCenterRepository = dataCenterRepository;
        this.locationRepository = locationRepository;
        this.rackRepository = rackRepository;
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
    public Salon getSalon(long salonId) {
        return centerRepository.findById(salonId).get();
    }


    @Override
    public List<Salon> getSalonList() {
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


        return getTemperatureHistoryList();
    }

    @Override
    public void setDailyTemperatureReport(DailyReport currentReport) {

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
        var center = getSalon(centerId);
        List<Float> weeklyTemperature = new ArrayList<>();
        for (LocalDate date : weeklyDateList) {
            weeklyTemperature.add(center.getAverageTemperature().get(date));
        }
        return weeklyTemperature;
    }

    @Override
    public List<String> getAllDataCenterNameList() {
        return dataCenterRepository.fetchAllIdNameMap();
    }

    @Override
    public List<String> getSalonNameList() {
        return locationRepository.getNameList("Salon");
    }

    @Override
    public List<Rack> getRackList() {
        return locationRepository.findAllByRack();
    }

}
