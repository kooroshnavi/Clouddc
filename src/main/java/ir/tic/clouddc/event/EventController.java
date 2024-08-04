package ir.tic.clouddc.event;

import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.center.LocationStatus;
import ir.tic.clouddc.resource.DeviceStatus;
import ir.tic.clouddc.resource.Firewall;
import ir.tic.clouddc.resource.Server;
import ir.tic.clouddc.resource.Switch;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;

    @Autowired
    EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/category")
    public String eventForm(Model model) {
        return "eventLandingPage";   /// 1. Category Selection
    }

    @GetMapping("/category/target/{target}/form")
    public String showEventLandingForm(Model model, @PathVariable String target) {
        EventLandingForm eventLandingForm = new EventLandingForm();
        eventLandingForm.setTarget(target);
        model.addAttribute("eventLandingForm", eventLandingForm);

        switch (target) {
            case "Center" -> {
                List<CenterService.CenterIdNameProjection> centerIdAndNameList = eventService.getCenterIdAndNameList();
                model.addAttribute("centerIdAndNameList", centerIdAndNameList);
            }
            case "Location" -> model.addAttribute("centerList", eventService.getCenterList());
        }

        return "eventLandingForm";    /// 2.    Input desired serial number (device) OR choose location for status OR center for visit
    }

    @PostMapping("/form/detail")
    public String showEventStatusModel(Model model
            , @Nullable @ModelAttribute("eventLandingForm") EventLandingForm eventLandingForm
            , @Nullable EventLandingForm fromImportantDevicePmForm) throws SQLException {

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
                                    model.addAttribute("utilizerList", eventService.deviceUtilizerEventData(device.get().getUtilizer()));
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

        return "eventStatusForm";   /// 3.  update status form
    }

    @PostMapping("/register")
    public String eventRegisterPost(Model model, @ModelAttribute("eventRegisterForm") EventLandingForm eventLandingForm   /// general event
            , @Nullable @ModelAttribute("deviceStatusForm") DeviceStatusForm deviceStatusForm   /// device status event
            , @Nullable @ModelAttribute("locationStatusForm") LocationStatusForm locationStatusForm /// location status event
            , @RequestParam("attachment") MultipartFile file) throws IOException, SQLException {

        if (!file.isEmpty()) {
            if (!Objects.isNull(locationStatusForm)) {
                locationStatusForm.setFile(file);
            } else if (!Objects.isNull(deviceStatusForm)) {
                deviceStatusForm.setFile(file);
            } else {
                eventLandingForm.setFile(file);
            }
        }
        eventService.eventSetup(eventLandingForm, deviceStatusForm, locationStatusForm);         //    4.  Event register
        eventService.getEventList(null);
        return "redirect:eventListView";
    }

    @GetMapping("/category/{categoryId}/list")
    public String showCategoryEventList(Model model, @PathVariable int categoryId) {
        List<Event> eventList = eventService.getEventList(categoryId);
        model.addAttribute("eventList", eventList);
        return "eventListView";
    }

    @GetMapping("/list")
    public String showEventList(Model model) {
        List<Event> eventList = eventService.getEventList(null);
        model.addAttribute("eventList", eventList);
        return "eventListView";
    }

    @GetMapping("/{eventId}/detail")
    public String viewEventDetail(Model model, @PathVariable Long eventId) {
        Event baseEvent = eventService.getEventHistory(eventId);
        model.addAttribute("metaData", List.of(eventService.getRelatedMetadata(baseEvent.getEventDetail().getPersistence().getId())));

        model.addAttribute("baseEvent", baseEvent);

        if (baseEvent instanceof VisitEvent event) {
            model.addAttribute("visitEvent", event);
        }
        if (baseEvent instanceof LocationStatusEvent event) {
            model.addAttribute("locationStatusEvent", event);
        }
        if (baseEvent instanceof DeviceUtilizerEvent event) {
            model.addAttribute("deviceUtilizerEvent", event);
        }
        if (baseEvent instanceof DeviceMovementEvent event) {
            model.addAttribute("deviceMovementEvent", event);
        }
        if (baseEvent instanceof DeviceStatusEvent event) {
            model.addAttribute("deviceStatusEvent", event);
        }

        return "eventDetailList";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        eventService.modelForEventController(model);
    }
}
