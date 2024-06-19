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
            , @Nullable EventLandingForm fromImportantDevicePmForm) {

        if (!Objects.isNull(eventLandingForm)) {
            switch (eventLandingForm.getTarget()) {
                case "Center" -> {
                    var center = eventService.getCenter(eventLandingForm.getCenterId());
                    if (center.isPresent()) {
                        eventLandingForm.setCenter(center.get());
                        eventLandingForm.setEventCategoryId((short) 1);
                    } else {
                        return "redirect:404";
                    }
                }
                case "Location" -> {
                    var location = eventService.getLocation(eventLandingForm.getLocationId());
                    if (location.isPresent()) { // Location status event
                        eventLandingForm.setLocation(location.get());
                        eventLandingForm.setEventCategoryId((short) 2);

                        LocationStatusForm locationStatusForm = eventService.getLocationStatusForm(location.get());
                        LocationStatus locationStatus = eventService.getCurrentLocationStatus(location.get());
                        model.addAttribute("locationStatusForm", locationStatusForm);
                        model.addAttribute("locationStatus", locationStatus);
                    } else {
                        return "redirect:404";
                    }
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
            , @RequestParam("attachment") MultipartFile file) throws IOException {

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
        eventService.getEventListModel(model);
        return "redirect:eventListView";
    }

    @GetMapping("/category/{categoryId}/list")
    public String showCategoryEventList(Model model, @Nullable @RequestParam("categoryId") Short categoryId) {
        if (categoryId != null) {
            eventService.getEventListByCategoryModel(model, categoryId);
        }
        return "eventListView";
    }

    @GetMapping("/list")
    public String showEventList(Model model) {
        eventService.getEventListModel(model);
        return "eventListView";
    }

    @GetMapping("/{eventId}/detail")
    public String viewEventDetail(@RequestParam Long eventId,
                                  Model model) {
        eventService.getEventDetailModel(model, eventId);
        return "eventDetailList";
    }

    @PostMapping("/update")
    public String updateEvent(Model model
            , @ModelAttribute("eventForm") EventLandingForm eventLandingForm
            , @RequestParam("attachment") MultipartFile file)
            throws IOException {

        if (!file.isEmpty()) {
            eventLandingForm.setFile(file);
        }
        Event event = eventService.getEvent(eventLandingForm.getEventId());
        eventService.updateEvent(eventLandingForm, event);
        eventService.getEventListModel(model);
        return "redirect:eventListView";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        eventService.modelForEventController(model);
    }

}
