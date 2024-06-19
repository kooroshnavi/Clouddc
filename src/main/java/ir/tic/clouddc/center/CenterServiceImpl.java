package ir.tic.clouddc.center;

import com.github.mfathi91.time.PersianDate;
import ir.tic.clouddc.event.LocationStatusEvent;
import ir.tic.clouddc.event.LocationStatusForm;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.notification.NotificationService;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.pm.PmCategoryRepository;
import ir.tic.clouddc.pm.PmInterface;
import ir.tic.clouddc.report.DailyReport;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

@Slf4j
@Service
@EnableScheduling
public class CenterServiceImpl implements CenterService {

    private final CenterRepository centerRepository;

    private final PersonService personService;

    private final NotificationService notificationService;

    private final LogService logService;

    private final LocationRepository locationRepository;

    private final LocationPmCatalogRepository locationPmCatalogRepository;

    private final LocationStatusRepository locationStatusRepository;


    @Autowired
    CenterServiceImpl(CenterRepository centerRepository, PersonService personService, NotificationService notificationService, LogService logService, LocationRepository locationRepository, PmCategoryRepository pmCategoryRepository, LocationPmCatalogRepository locationPmCatalogRepository, LocationStatusRepository locationStatusRepository) {
        this.centerRepository = centerRepository;
        this.personService = personService;
        this.notificationService = notificationService;
        this.logService = logService;
        this.locationRepository = locationRepository;
        this.locationPmCatalogRepository = locationPmCatalogRepository;
        this.locationStatusRepository = locationStatusRepository;
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
    public LocationStatus getCurrentLocationStatus(Location location) {
       var locationStatus =  locationStatusRepository.findByLocationAndCurrent(location, true);
        if (locationStatus.isPresent()) {
            return locationStatus.get();
        } else {
            LocationStatus defaultLocationStatus = new LocationStatus();
            defaultLocationStatus.setDoor(true);
            defaultLocationStatus.setVentilation(true);
            defaultLocationStatus.setPower(true);
            return defaultLocationStatus;
        }
    }

    @Override
    public List<Location> getCustomizedLocationList(List<String> locationCategoryNameList) {
        return locationRepository.fetchCustomizedLocationList(locationCategoryNameList);
    }

    @Override
    public Model getCenterLandingPageModel(Model model) {
        List<Center> centerList = centerRepository.findAll();

        var center1 = centerList.get(0);
        var center2 = centerList.get(1);

        var center1SalonList = center1
                .getLocationList()
                .stream()
                .filter(location -> location.getLocationType().getId() == 1)
                .toList();
        var center1RoomList = center1
                .getLocationList()
                .stream()
                .filter(location -> location.getLocationType().getId() == 3)
                .toList();

        var center2SalonList = center2
                .getLocationList()
                .stream()
                .filter(location -> location.getLocationType().getId() == 1)
                .toList();
        var center2RoomList = center2
                .getLocationList()
                .stream()
                .filter(location -> location.getLocationType().getId() == 3)
                .toList();


        model.addAttribute("center1", center1);
        model.addAttribute("center1SalonList", center1SalonList);
        model.addAttribute("center1RoomList", center1RoomList);
        model.addAttribute("center2", center2);
        model.addAttribute("center2SalonList", center2SalonList);
        model.addAttribute("center2RoomList", center2RoomList);
        return model;
    }

    @Override
    public List<CenterIdNameProjection> getCenterIdAndNameList() {
        return centerRepository.fetchCenterIdNameList();
    }

    @Override
    public void updateLocationStatus(LocationStatusForm locationStatusForm, LocationStatusEvent event) {
        List<LocationStatus> locationStatusList = new ArrayList<>();
        var location = locationStatusForm.getLocation();
        var currentStatus = location.getLocationStatusList().stream().filter(LocationStatus::isCurrent).findFirst();
        if (currentStatus.isPresent()) {
            currentStatus.get().setCurrent(false);
            locationStatusList.add(currentStatus.get());
        }
        LocationStatus locationStatus = new LocationStatus();
        locationStatus.setLocation(locationStatusForm.getLocation());
        locationStatus.setEvent(event);
        locationStatus.setDoor(locationStatusForm.isDoor());
        locationStatus.setVentilation(locationStatusForm.isVentilation());
        locationStatus.setPower(locationStatusForm.isPower());
        locationStatus.setCurrent(true);

        locationStatusList.add(locationStatus);

        locationStatusRepository.saveAllAndFlush(locationStatusList);
    }

    @Override
    public List<LocationPmCatalog> getTodayCatalogList(LocalDate date) {
        return locationPmCatalogRepository.findAllByNextDueDate(date);
    }

    @Override
    public void updateCatalogDueDate(PmInterface pmInterface, Location location) {
        var catalog = locationPmCatalogRepository.findByPmInterfaceAndLocation(pmInterface, location);
        if (catalog.isPresent()) {
            catalog.get().setNextDueDate(validateNextDue(UtilService.getDATE().plusDays(pmInterface.getPeriod())));
            locationPmCatalogRepository.save(catalog.get());
        }
    }

    @Override
    public void updateNewlyEnabledCatalog(PmInterface pmInterface) {
        List<LocationPmCatalog> locationPmCatalogList = locationPmCatalogRepository.findAllByPmInterface(pmInterface);
        if (!locationPmCatalogList.isEmpty()) {
            for (LocationPmCatalog catalog : locationPmCatalogList) {
                var dueDate = catalog.getNextDueDate();
                if (dueDate.isBefore(UtilService.getDATE()) || dueDate.equals(UtilService.getDATE())) {  // expired due date
                    catalog.setNextDueDate(validateNextDue(UtilService.getDATE().plusDays(1)));
                }
            }
            locationPmCatalogRepository.saveAll(locationPmCatalogList);
        }
    }

    private LocalDate validateNextDue(LocalDate nextDue) {
        if (nextDue.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()).equals("Thu")) {
            return nextDue.plusDays(2);
        } else if (nextDue.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()).equals("Fri")) {
            return nextDue.plusDays(1);
        } else {
            return nextDue;
        }
    }


    @Override
    public Hall getHall(int hallId) {
        return null;
    }


    @Override
    public Optional<Location> getLocation(int locationId) {
        return locationRepository.findById(locationId);
    }

    @Override
    public Model getLocationDetailModel(int locationId, Model model) {
        Optional<Location> optionalLocation = locationRepository.findById(locationId);
        if (optionalLocation.isPresent()) {
            var baseLocation = optionalLocation.get();

            if (baseLocation instanceof Hall location) {
                model.addAttribute("location", location);
                model.addAttribute("rackList", location.getRackList());

            } else if (baseLocation instanceof Rack location) {
                model.addAttribute("location", location);
                model.addAttribute("deviceList", location.getDeviceList());
            } else if (baseLocation instanceof Room location) {
                model.addAttribute("location", location);
                model.addAttribute("deviceList", location.getDeviceList());
            }
            return model;
        }
        return model;
    }

    @Override
    public Optional<Center> getCenter(short centerId) {
        return centerRepository.findById(centerId);
    }

    @Override
    public List<Hall> getSalonList() {
        return null;
    }

    @Override
    public List<Center> getCenterList() {
        return centerRepository.findAll(Sort.by("name"));
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
        var center = getHall(centerId);
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

    @Override
    public List<Room> getRoomList() {
        return locationRepository.findAllByRoom();
    }

}
