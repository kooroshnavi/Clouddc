package ir.tic.clouddc.event;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.center.Rack;
import ir.tic.clouddc.center.Room;
import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.ResourceService;
import ir.tic.clouddc.resource.Utilizer;
import ir.tic.clouddc.utils.UtilService;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

            case 2 -> { // New Device Installation
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
                }

            }

            case 3 -> { // Location Utilizer
                var optionalLocation = eventService.getLocation(targetId);
                if (optionalLocation.isPresent()) {
                    var location = optionalLocation.get();
                    Utilizer currentUtilizer;
                    if (location instanceof Rack rack) {
                        log.info("Rack found");
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
                    List<Location> destinationList = eventService.getDeviceMovementEventData_2(targetId);
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

            case 5 -> {
                Device device = eventService.getReferencedDevice(targetId);
                var currentUtilizer = device.getUtilizer();
                List<ResourceService.UtilizerIdNameProjection> utilizerList = eventService.getUtilizerList(List.of(currentUtilizer.getId()));
                model.addAttribute("currentUtilizer", currentUtilizer);
                model.addAttribute("utilizerList", utilizerList);
                model.addAttribute("device", device);
            }

            default -> {
                return "404";
            }

        }

        model.addAttribute("eventForm", new EventForm());
        model.addAttribute("categoryId", categoryId);

        return "movementEvent";
    }

    @PostMapping("/register2")
    public String eventRegisterPost(Model model, @RequestParam("attachment") MultipartFile file
            , @ModelAttribute("eventForm") EventForm eventForm) throws IOException {

        var nextDue = eventForm.getDate();
        var georgianDate = LocalDate.parse(nextDue);
        if (georgianDate.isAfter(UtilService.getDATE())) {
            return "403";
        }

        if (!file.isEmpty()) {
            eventForm.setMultipartFile(file);
        }

        eventService.eventRegister(eventForm, georgianDate);

        log.info("Event Registered Successfully");

        return "redirect:/event/list";
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

    @GetMapping("/list")
    public String showEventList(Model model) {
        List<Event> eventList = eventService.getEventList();
        model.addAttribute("eventList", eventList);
        return "eventListView";
    }

    @GetMapping("/{eventId}/detail")
    public String viewEventDetail(Model model, @PathVariable Long eventId) {
        Event baseEvent = eventService.getEventHistory(eventId);
        model.addAttribute("metaData", List.of(eventService.getRelatedMetadata(baseEvent.getEventDetail().getPersistence().getId())));

        model.addAttribute("baseEvent", baseEvent);

        if (baseEvent instanceof LocationCheckList event) {
            model.addAttribute("locationStatusEvent", event);
        }
        if (baseEvent instanceof DeviceUtilizerEvent event) {
            model.addAttribute("deviceUtilizerEvent", event);
        }
        if (baseEvent instanceof DeviceMovementEvent event) {
            model.addAttribute("deviceMovementEvent", event);
        }
        if (baseEvent instanceof DeviceCheckList event) {
            model.addAttribute("deviceStatusEvent", event);
        }

        return "eventDetailList";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        eventService.modelForEventController(model);
    }
}
