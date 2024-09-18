package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Hall;
import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.center.Rack;
import ir.tic.clouddc.center.Room;
import ir.tic.clouddc.document.MetaData;
import ir.tic.clouddc.resource.*;
import ir.tic.clouddc.utils.UtilService;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@Controller
@RequestMapping("/event")
@Slf4j
public class EventController {

    private final EventService eventService;

    @Autowired
    EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/category/{categoryId}/{targetId}/form")
    public String showEventForm(Model model, @PathVariable Long targetId, @PathVariable Integer categoryId) {

        switch (categoryId) {

            case 1 -> { // General Event
                Optional<Location> optionalLocation = eventService.getLocation(targetId);
                if (optionalLocation.isPresent()) {
                    var location = optionalLocation.get();
                    model.addAttribute("location", location);
                } else {
                    return "404";
                }
            }

            case 2 -> { // New Device Installation
                var newDeviceAvailable = eventService.newDevicePresentCheck();
                if (!newDeviceAvailable) {
                    return "redirect:/resource/device/unassigned";
                }

                var optionalLocation = eventService.getLocation(targetId);
                if (optionalLocation.isPresent()) {
                    var location = optionalLocation.get();
                    Utilizer currentUtilizer;
                    if (location instanceof Rack rack) {
                        currentUtilizer = rack.getUtilizer();
                    } else if (location instanceof Room room) {
                        currentUtilizer = room.getUtilizer();
                    } else {
                        return "404";
                    }
                    if (!currentUtilizer.isGenuineUtilizer()) {
                        List<ResourceService.UtilizerIdNameProjection> utilizerList = eventService.getUtilizerList(List.of(0));
                        model.addAttribute("utilizerList", utilizerList);
                    }
                    List<ResourceService.DeviceIdSerialCategoryVendor_Projection1> newDeviceList = eventService.getNewDeviceList();

                    model.addAttribute("currentUtilizer", currentUtilizer);
                    model.addAttribute("location", location);
                    model.addAttribute("deviceList", newDeviceList);
                } else {
                    return "404";
                }
            }

            case 3 -> { // Location Utilizer
                var optionalLocation = eventService.getLocation(targetId);
                if (optionalLocation.isPresent()) {
                    var location = optionalLocation.get();
                    Utilizer currentUtilizer;
                    if (location instanceof Rack rack) {
                        currentUtilizer = rack.getUtilizer();
                    } else if (location instanceof Room room) {
                        currentUtilizer = room.getUtilizer();
                    } else {
                        return "404";
                    }

                    List<ResourceService.UtilizerIdNameProjection> utilizerList = eventService.getUtilizerList(List.of(currentUtilizer.getId()));
                    model.addAttribute("currentUtilizer", currentUtilizer);
                    model.addAttribute("utilizerList", utilizerList);
                    model.addAttribute("location", location);
                } else {
                    return "404";
                }
            }

            case 4 -> {  // Device Movement from a location
                var optionalLocation = eventService.getLocation(targetId);
                if (optionalLocation.isPresent()) {
                    var location = optionalLocation.get();
                    List<Rack> rackList = new ArrayList<>();
                    List<Room> roomList = new ArrayList<>();
                    List<ResourceService.DeviceIdSerialCategoryVendor_Projection1> locationDeviceList = eventService.getLocationDeviceList(targetId);
                    List<Location> destinationList = eventService.getLocationListExcept(targetId);
                    for (Location destinationLocation : destinationList) {
                        if (destinationLocation instanceof Rack rack) {
                            rackList.add(rack);
                        } else if (destinationLocation instanceof Room room) {
                            roomList.add(room);
                        }
                    }
                    model.addAttribute("rackList", rackList);
                    model.addAttribute("roomList", roomList);
                    model.addAttribute("deviceList", locationDeviceList);
                    model.addAttribute("location", location);
                } else {
                    return "404";
                }
            }

            case 5 -> { // Device Utilizer
                Device device = eventService.getReferencedDevice(targetId);
                var currentUtilizer = device.getUtilizer();
                List<ResourceService.UtilizerIdNameProjection> utilizerList = eventService.getUtilizerList(List.of(currentUtilizer.getId()));
                model.addAttribute("currentUtilizer", currentUtilizer);
                model.addAttribute("utilizerList", utilizerList);
                model.addAttribute("device", device);
            }

            case 6 -> { // Device Module
                Device device = eventService.getReferencedDevice(targetId);
                List<ModuleInventory> deviceModuleInventoryList = eventService.getDeviceCompatibleModuleInventoryList(device.getDeviceCategory().getId());
                Map<ModuleInventory, Integer> deviceModuleMap = new HashMap<>();
                var packList = device.getModulePackList();
                if (!packList.isEmpty()) {
                    for (ModulePack modulePack : device.getModulePackList()) {
                        deviceModuleMap.put(modulePack.getModuleInventory(), modulePack.getQty());
                    }
                }
                Map<ModuleInventory, Integer> moduleOverviewMap = eventService.getDeviceModuleOverviewMap(packList);
                var sortedKeySet = moduleOverviewMap
                        .keySet()
                        .stream()
                        .sorted(Comparator.comparing(ModuleInventory::getClassification)).toList();

                model.addAttribute("device", device);
                model.addAttribute("deviceModuleMap", deviceModuleMap);
                model.addAttribute("sortedKeySet", sortedKeySet);
                model.addAttribute("moduleOverviewMap", moduleOverviewMap);
                model.addAttribute("deviceModuleInventoryList", deviceModuleInventoryList);

            }

            default -> {
                return "404";
            }

        }

        model.addAttribute("eventForm", new EventForm());
        model.addAttribute("categoryId", categoryId);

        return "movementEvent";
    }

    @PostMapping("/register")
    public String eventRegisterPost(RedirectAttributes redirectAttributes, @RequestParam("attachment") MultipartFile file
            , @ModelAttribute("eventForm") EventForm eventForm) throws IOException, DateTimeParseException {

        if (!file.isEmpty()) {
            eventForm.setMultipartFile(file);
        }

        if (eventForm.getEventId() == null) {
            LocalDate georgianDate;
            var nextDue = eventForm.getDate();
            georgianDate = LocalDate.parse(nextDue);
            if (georgianDate.isAfter(UtilService.getDATE())) {
                return "403";
            }
            eventService.registerEvent(eventForm, georgianDate);
        } else {
            eventService.updateGeneralEvent(eventForm);
        }

        redirectAttributes.addFlashAttribute("eventRegisterSuccessful", true);

        switch (eventForm.getEventCategoryId()) {
            case 1 -> {
                redirectAttributes.addAttribute("targetId", eventForm.getGeneral_locationId());
                redirectAttributes.addAttribute("typeId", 1);
            }
            case 2, 3 -> {
                redirectAttributes.addAttribute("targetId", eventForm.getUtilizer_locationId());
                redirectAttributes.addAttribute("typeId", 1);
            }
            case 4 -> {
                redirectAttributes.addAttribute("targetId", eventForm.getDeviceMovement_destLocId());
                redirectAttributes.addAttribute("typeId", 1);
            }
            case 5 -> {
                redirectAttributes.addAttribute("targetId", eventForm.getUtilizer_deviceId());
                redirectAttributes.addAttribute("typeId", 2);
            }
            default -> {
                return "404";
            }
        }
        return "redirect:/event/{typeId}/{targetId}/list";

    }

    @GetMapping("/{typeId}/{targetId}/list")
    public String targetEventList(Model model,
                                  @PathVariable Long targetId,
                                  @PathVariable Integer typeId) {
        List<Event> eventList;
        switch (typeId) {
            case 0 -> eventList = eventService.getEventList();
            case 1 -> {
                var location = eventService.getReferencedLocation(targetId);
                eventList = location.getEventList();
                model.addAttribute("locationCategoryName", location.getLocationCategory().getCategory() + " " + location.getName());
            }
            case 2 -> {
                var device = eventService.getReferencedDevice(targetId);
                eventList = device.getDeviceEventList();
                model.addAttribute("deviceCategorySerialNumber", device.getDeviceCategory().getCategory() + " " + device.getSerialNumber());

            }
            case 3 -> {
                var utilizer = eventService.getReferencedUtilizer(targetId.intValue());
                eventList = utilizer.getEventList();
                model.addAttribute("utilizerName", utilizer.getName());
            }
            default -> {
                return "404";
            }
        }
        List<Event> finalEventList = eventService
                .loadEventTransients_1(eventList)
                .stream()
                .sorted(Comparator.comparing(Event::getId).reversed()) // Newest to oldest
                .toList();
        model.addAttribute("eventList", finalEventList);
        model.addAttribute("typeId", typeId);
        model.addAttribute("targetId", targetId);

        if (!model.containsAttribute("eventRegisterSuccessful")) {
            model.addAttribute("eventRegisterSuccessful", false);
        }

        return "eventListView";
    }

    @GetMapping("/{eventId}/detail")
    public String viewEventDetail(Model model, @PathVariable Long eventId) {
        Event baseEvent = eventService.getEventHistory(eventId);
        var evetDetailList = baseEvent.getEventDetailList();
        List<MetaData> metaDataList = eventService.getRelatedMetadataList(evetDetailList);
        Map<Utilizer, Integer> balanceReferenceMap = eventService.getBalanceReference(baseEvent);

        var locationList = baseEvent.getLocationList();
        var listSize = locationList.size();

        if (listSize == 1) {
            var location = Hibernate.unproxy(locationList.get(0), Location.class);
            if (location instanceof Hall hall) {
                model.addAttribute("hall", hall);
            } else if (location instanceof Rack rack) {
                model.addAttribute("rack1", rack);
            } else if (location instanceof Room room) {
                model.addAttribute("room1", room);
            }
        } else {
            var source = Hibernate.unproxy(locationList.get(0), Location.class);
            var destination = Hibernate.unproxy(locationList.get(1), Location.class);

            if (source instanceof Rack rack) {
                model.addAttribute("sourceRack", rack);
            } else if (source instanceof Room room) {
                model.addAttribute("sourceRoom", room);
            }

            if (destination instanceof Rack rack) {
                model.addAttribute("destRack", rack);
            } else if (destination instanceof Room room) {
                model.addAttribute("destRoom", room);
            }
        }

        if (baseEvent instanceof GeneralEvent generalEvent) {
            generalEvent.setCategory(UtilService.GENERAL_EVENT_CATEGORY_ID.get(generalEvent.getGeneralCategoryId()));
            model.addAttribute("generalEvent", generalEvent);
        } else if (baseEvent instanceof NewDeviceInstallationEvent newDeviceInstallationEvent) {
            model.addAttribute("newDeviceInstallationEvent", newDeviceInstallationEvent);
        } else if (baseEvent instanceof LocationUtilizerEvent locationUtilizerEvent) {
            model.addAttribute("locationUtilizerEvent", locationUtilizerEvent);
        } else if (baseEvent instanceof DeviceMovementEvent deviceMovementEvent) {
            model.addAttribute("deviceMovementEvent", deviceMovementEvent);
        } else if (baseEvent instanceof DeviceUtilizerEvent deviceUtilizerEvent) {
            model.addAttribute("deviceUtilizerEvent", deviceUtilizerEvent);
        } else {
            return "404";
        }
        if (baseEvent.isActive()) {
            model.addAttribute("eventForm", new EventForm());
        }
        model.addAttribute("baseEvent", baseEvent);
        model.addAttribute("eventDetailList", evetDetailList);
        model.addAttribute("balanceReferenceMap", balanceReferenceMap);
        model.addAttribute("metaDataList", metaDataList);

        return "eventDetailView";
    }

    @PostMapping("/form/detail")
    public String showEventStatusModel(Model model
            , @Nullable @ModelAttribute("eventLandingForm") EventLandingForm eventLandingForm
            , @Nullable EventLandingForm fromImportantDevicePmForm) throws SQLException {
/*
        if (!Objects.isNull(eventLandingForm)) {
            switch (eventLandingForm.getTarget()) {
                case "Center" -> {
                    var center = eventService.getCenter(eventLandingForm.getCenterId());
                    if (center.isPresent()) {
                        eventLandingForm.setCenter(center.get());
                        eventLandingForm.setEventCategoryId(1);
                    } else {
                        return "redirect:404";
                    }
                }
                case "Location" -> {
                    var location = eventService.getRefrencedLocation(eventLandingForm.getLocationId());
                    eventLandingForm.setLocation(location);
                    eventLandingForm.setEventCategoryId(2);

                    LocationStatusForm locationStatusForm = eventService.getLocationStatusForm(location);
                    LocationStatus locationStatus = eventService.getCurrentLocationStatus(location);
                    locationStatusForm.setCurrentLocationStatus(locationStatus);
                    model.addAttribute("locationStatusForm", locationStatusForm);
                    model.addAttribute("locationStatus", locationStatus);

                }
                case "Device" -> {
                    var device = eventService.getDevice(eventLandingForm.getSerialNumber());
                    if (device.isPresent()) {   // table specific data
                        if (device.get() instanceof Server server) {
                            model.addAttribute("server", server);
                        } else if (device.get() instanceof Switch sw) {
                            model.addAttribute("sw", sw);
                        } else if (device.get() instanceof Firewall firewall) {
                            model.addAttribute("firewall", firewall);
                        }
                        eventLandingForm.setDevice(device.get());   // for table common data

                        switch (eventLandingForm.getEventCategoryId()) {
                            case 3 ->   // Device utilizer event
                                    model.addAttribute("utilizerList", eventService.getUtilizerList(List.of(device.get().getUtilizer().getId())));
                            case 4 ->
                                    model.addAttribute("centerList", eventService.getCenterList());   // Device movement event
                            case 5 -> { // Device status event
                                DeviceStatusForm deviceStatusForm = eventService.getDeviceStatusForm(device.get());
                                DeviceStatus deviceStatus = eventService.getCurrentDeviceStatus(device.get());
                                model.addAttribute("deviceStatusForm", deviceStatusForm);
                                model.addAttribute("deviceStatus", deviceStatus);
                            }
                        }
                    } else {
                        return "redirect:404";
                    }
                }
                default -> {
                    return "redirect:404";
                }
            }
            model.addAttribute("eventLandingForm", eventLandingForm);
        }


 */
        return "eventStatusForm";   /// 3.  update status form
    }

    @GetMapping("/category/{categoryId}/list")
    public String showCategoryEventList(Model model, @PathVariable int categoryId) {
        //   List<Event> eventList = eventService.getEventList(categoryId);
        // model.addAttribute("eventList", eventList);
        return "eventListView";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        eventService.modelForEventController(model);
    }
}
