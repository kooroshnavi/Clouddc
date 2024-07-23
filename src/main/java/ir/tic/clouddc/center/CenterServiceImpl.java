package ir.tic.clouddc.center;

import ir.tic.clouddc.event.LocationStatusEvent;
import ir.tic.clouddc.event.LocationStatusForm;
import ir.tic.clouddc.individual.Person;
import ir.tic.clouddc.individual.PersonService;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.notification.NotificationService;
import ir.tic.clouddc.pm.CatalogForm;
import ir.tic.clouddc.pm.PmInterface;
import ir.tic.clouddc.report.DailyReport;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
public class CenterServiceImpl implements CenterService {

    private final CenterRepository centerRepository;

    private final PersonService personService;

    private final NotificationService notificationService;

    private final LogService logService;

    private final LocationRepository locationRepository;

    private final LocationPmCatalogRepository locationPmCatalogRepository;

    private final LocationStatusRepository locationStatusRepository;


    @Autowired
    CenterServiceImpl(CenterRepository centerRepository, PersonService personService, NotificationService notificationService, LogService logService, LocationRepository locationRepository, LocationPmCatalogRepository locationPmCatalogRepository, LocationStatusRepository locationStatusRepository) {
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
    public LocationPmCatalog registerNewCatalog(CatalogForm catalogForm, LocalDate nextDue) {
        LocationPmCatalog locationPmCatalog = new LocationPmCatalog();
        locationPmCatalog.setLocation(locationRepository.getReferenceById(catalogForm.getLocationId()));
        locationPmCatalog.setDefaultPerson(new Person(catalogForm.getDefaultPersonId()));
        locationPmCatalog.setPmInterface(new PmInterface(catalogForm.getPmInterfaceId()));
        locationPmCatalog.setEnabled(true);
        locationPmCatalog.setHistory(false);
        locationPmCatalog.setActive(false);
        locationPmCatalog.setNextDueDate(UtilService.validateNextDue(nextDue));

        return locationPmCatalogRepository.save(locationPmCatalog);
    }

    @Override
    public List<LocationPmCatalog> getLocationCatalogList(Location baseLocation) {
        return locationPmCatalogRepository.findAllByLocation(baseLocation);
    }

    @Override
    public LocationStatus getCurrentLocationStatus(Location location) {
        var locationStatus = locationStatusRepository.findByLocationAndCurrent(location, true);
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
                .filter(location -> location.getLocationCategory().getId() == 1)
                .toList();
        var center1RoomList = center1
                .getLocationList()
                .stream()
                .filter(location -> location.getLocationCategory().getId() == 3)
                .toList();

        var center2SalonList = center2
                .getLocationList()
                .stream()
                .filter(location -> location.getLocationCategory().getId() == 1)
                .toList();
        var center2RoomList = center2
                .getLocationList()
                .stream()
                .filter(location -> location.getLocationCategory().getId() == 3)
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
        return locationPmCatalogRepository.findAllByNextDueDateAndEnabled(date, true);
    }

    @Override
    public void updateCatalogDueDate(LocationPmCatalog catalog) {
        locationPmCatalogRepository.updateCatalogDueDate(UtilService.getDATE().plusDays(catalog.getPmInterface().getPeriod()), catalog.getId());
    }

    @Override
    public void updateNewlyEnabledCatalog(PmInterface pmInterface) {
        List<LocationPmCatalog> locationPmCatalogList = locationPmCatalogRepository.findAllByPmInterface(pmInterface);
        if (!locationPmCatalogList.isEmpty()) {
            for (LocationPmCatalog catalog : locationPmCatalogList) {
                var dueDate = catalog.getNextDueDate();
                if (dueDate.isBefore(UtilService.getDATE()) || dueDate.equals(UtilService.getDATE())) {  // expired due date
                    catalog.setNextDueDate(UtilService.validateNextDue(UtilService.getDATE().plusDays(1)));
                }
            }
            locationPmCatalogRepository.saveAll(locationPmCatalogList);
        }
    }

    @Override
    public Hall getHall(int hallId) {
        return null;
    }


    @Override
    public Optional<Location> getLocation(Long locationId) {
        Optional<Location> optionalLocation = locationRepository.findById(locationId);
        if (optionalLocation.isPresent()) {
            if (optionalLocation.get().getLocationPmCatalogList() != null) {
                for (LocationPmCatalog locationPmCatalog : optionalLocation.get().getLocationPmCatalogList()) {
                    if (locationPmCatalog.isHistory()) {
                        var finishedDate = locationPmCatalog.getLastFinishedDate();
                        locationPmCatalog.setPersianLastFinishedDate(UtilService.getFormattedPersianDate(finishedDate));
                        locationPmCatalog.setPersianLastFinishedDayTime(UtilService.getFormattedPersianDayTime(finishedDate, locationPmCatalog.getLastFinishedTime()));
                    }
                    locationPmCatalog.setPersianNextDue(UtilService.getFormattedPersianDate(locationPmCatalog.getNextDueDate()));
                }
            }
            return optionalLocation;
        }
        throw new NoSuchElementException();
    }

    @Override
    public Optional<Center> getCenter(int centerId) {
        return centerRepository.findById(centerId);
    }

    @Override
    public List<Hall> getHallList() {
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
    public void setDailyTemperatureReport(DailyReport currentReport) {

    }


    @Override
    public List<Float> getWeeklyTemperature(List<LocalDate> weeklyDateList, int centerId) {
        /*
        var center = getHall(centerId);
        List<Float> weeklyTemperature = new ArrayList<>();
        for (LocalDate date : weeklyDateList) {
            weeklyTemperature.add(center.getAverageTemperature().get(date));
        }*/
        return null;
    }


}
