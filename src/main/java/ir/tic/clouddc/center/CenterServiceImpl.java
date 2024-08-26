package ir.tic.clouddc.center;

import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.notification.NotificationService;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.utils.UtilService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.*;

@Slf4j
@Service
public class CenterServiceImpl implements CenterService {

    private final CenterRepository centerRepository;

    private final PersonService personService;

    private final NotificationService notificationService;

    private final LogService logService;

    private final LocationRepository locationRepository;

    private final LocationPmCatalogRepository locationPmCatalogRepository;

    @Autowired
    CenterServiceImpl(CenterRepository centerRepository, PersonService personService, NotificationService notificationService, LogService logService, LocationRepository locationRepository, LocationPmCatalogRepository locationPmCatalogRepository) {
        this.centerRepository = centerRepository;
        this.personService = personService;
        this.notificationService = notificationService;
        this.logService = logService;
        this.locationRepository = locationRepository;
        this.locationPmCatalogRepository = locationPmCatalogRepository;
    }

    @Override
    public Location getRefrencedLocation(Long locationId) throws EntityNotFoundException {
        return locationRepository.getReferenceById(locationId);
    }

    @Override
    public List<Location> getLocationListExcept(List<Long> locationId) {
        return locationRepository.getLocationListNotIn(locationId);
    }

    @Override
    public List<Location> getLocationList() {
        return locationRepository.getLocationList();
    }


    @Override
    public void verifyRackDevicePosition(List<Rack> rackList) {
        if (!rackList.isEmpty()) {
            List<Rack> modifiedRackList = new ArrayList<>();
            for (Rack rack : rackList) {
                if (rack.getDevicePositionMap().isEmpty()) {
                    var deviceList = rack.getDeviceList();
                    if (!deviceList.isEmpty()) {
                        Map<Integer, Device> rackDevicePositionMap = new HashMap<>();
                        int position = 0;
                        for (Device device : deviceList) {
                            position += 1;
                            rackDevicePositionMap.put(position, device);
                        }
                        rack.setDevicePositionMap(rackDevicePositionMap);
                        modifiedRackList.add(rack);
                    }
                }
            }
            if (!modifiedRackList.isEmpty()) {
                locationRepository.saveAllAndFlush(modifiedRackList);
            }
        }
    }

    @Override
    public void updateRackDevicePosition(Long rackId, Set<String> newPositionStringList) {
        var location = Hibernate.unproxy(getRefrencedLocation(rackId), Location.class);
        Rack rack = (Rack) location;
        var oldPositionMap = rack.getDevicePositionMap();
        Map<Integer, Device> newPositionMap = new HashMap<>();

        int newPosition = 0;
        for (String stringPosition : newPositionStringList) {
            int oldPosition = Integer.parseInt(stringPosition);
            newPosition += 1;
            newPositionMap.put(newPosition, oldPositionMap.get(oldPosition));
        }

        rack.setDevicePositionMap(newPositionMap);

        locationRepository.save(rack);
    }

    @Override
    public Optional<Location> getLocation(Long locationId) {
        Optional<Location> optionalLocation = locationRepository.findById(locationId);

        if (optionalLocation.isPresent()) {
            if (optionalLocation.get().getLocationPmCatalogList() != null) {
                for (LocationPmCatalog locationPmCatalog : optionalLocation.get().getLocationPmCatalogList()) {
                    locationPmCatalog.setPersianNextDue(UtilService.getFormattedPersianDate(locationPmCatalog.getNextDueDate()));
                }
            }
        }

        return optionalLocation;
    }

    @Override
    public LocationStatus getCurrentLocationStatus(Location location) {
        //var locationStatus = locationStatusRepository.findByLocationAndActive(location, true);
     /*   if (locationStatus.isPresent()) {
            return locationStatus.get();
        } else {
            LocationStatus defaultLocationStatus = new LocationStatus();
            defaultLocationStatus.setDoor(true);
            defaultLocationStatus.setVentilation(true);
            defaultLocationStatus.setPower(true);
            return defaultLocationStatus;
        }*/
        return null;
    }

    @Override
    public Model getCenterLandingPageModel(Model model) {
        List<Center> centerList = centerRepository.getCenterList(List.of(1000, 1001));

        var center1 = centerList.get(0);
        var center2 = centerList.get(1);

        var center1SalonList = center1
                .getLocationList()
                .stream()
                .filter(location -> location.getLocationCategory().getLocationCategoryID() == 1)
                .toList();
        var center1RoomList = center1
                .getLocationList()
                .stream()
                .filter(location -> location.getLocationCategory().getLocationCategoryID() == 3)
                .toList();

        var center2SalonList = center2
                .getLocationList()
                .stream()
                .filter(location -> location.getLocationCategory().getLocationCategoryID() == 1)
                .toList();
        var center2RoomList = center2
                .getLocationList()
                .stream()
                .filter(location -> location.getLocationCategory().getLocationCategoryID() == 3)
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
    public Model modelForCenterController(Model model) {
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = personService.getPersonByUsername(personName);
        model.addAttribute("person", person);
        model.addAttribute("role", authenticated.getAuthorities());
        model.addAttribute("date", UtilService.getCurrentDate());
        return model;
    }
}
