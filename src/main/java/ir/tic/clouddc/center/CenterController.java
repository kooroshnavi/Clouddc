package ir.tic.clouddc.center;

import ir.tic.clouddc.event.EventService;
import ir.tic.clouddc.pm.CatalogForm;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@Controller
@RequestMapping("/center")
public class CenterController {

    private final CenterService centerService;

    private final EventService eventService;


    @Autowired
    public CenterController(CenterService centerService, EventService eventService) {
        this.centerService = centerService;
        this.eventService = eventService;
    }

    @GetMapping("/overview")
    public String showCenterLandingPage(Model model) {
        UtilService.setDate();
        centerService.getCenterLandingPageModel(model);
        return "centerLandingPage";
    }

    @GetMapping("/location/{locationId}/detail") // Covers Room Rack and salon
    public String showLocationDetail(Model model, @PathVariable Long locationId) {
        var baseLocation = centerService.getLocation(locationId);
        model.addAttribute("location", baseLocation);
        var catalogList = baseLocation.getLocationPmCatalogList();
        model.addAttribute("catalogList", catalogList);
        var eventList = eventService.getLocationEventList(baseLocation);
        model.addAttribute("eventList", eventList);


        if (baseLocation instanceof Hall location) {
            model.addAttribute("hall", location);
            model.addAttribute("rackList", location.getRackList());
        } else if (baseLocation instanceof Rack location) {
            model.addAttribute("rack", location);
            model.addAttribute("rackDeviceList", location.getDeviceList());
        } else if (baseLocation instanceof Room location) {
            model.addAttribute("room", location);
            model.addAttribute("roomDeviceList", location.getDeviceList());
        }
        return "locationDetail";

    }

    @PostMapping("/location/catalog/register")
    public String registerCatalog(@ModelAttribute("catalogForm") CatalogForm catalogForm) {
        var nextDue = catalogForm.getNextDue();
        var validDate = LocalDate.parse(nextDue);
        log.info(String.valueOf(validDate));
        if (validDate.isBefore(LocalDate.now())) {
            return "403";
        }

        centerService.registerNewCatalog(catalogForm, validDate);

        //  redirectAttributes.addAttribute("locationId", catalogForm.getLocationId());
        return "500";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        centerService.modelForCenterController(model);
    }

}
