package ir.tic.clouddc.center;

import ir.tic.clouddc.event.EventService;
import ir.tic.clouddc.pm.CatalogForm;
import ir.tic.clouddc.report.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/center")
public class CenterController {

    private final CenterService centerService;

    private final EventService eventService;

    private final ReportService reportService;

    @Autowired
    public CenterController(CenterService centerService, EventService eventService, ReportService reportService) {
        this.centerService = centerService;
        this.eventService = eventService;
        this.reportService = reportService;
    }

    @GetMapping("/overview")
    public String showCenterLandingPage(Model model) {

        centerService.getCenterLandingPageModel(model);
        return "centerLandingPage";
    }

    @GetMapping("/location/{locationId}/detail") // Covers Room Rack and salon
    public String showLocationDetail(Model model, @PathVariable int locationId) {
        var optionalLocation = centerService.getLocation(locationId);
        if (optionalLocation.isPresent()) {
            var baseLocation = optionalLocation.get();
            model.addAttribute("location", baseLocation);
            var catalog = baseLocation.getLocationPmCatalogList();
            model.addAttribute("catalog", catalog);
            var eventList = eventService.getLocationEventList(baseLocation);
            model.addAttribute("eventList", eventList);

            CatalogForm catalogForm = new CatalogForm();
            catalogForm.setLocation(baseLocation);
            model.addAttribute("catalogForm", catalogForm);

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
        } else {
            return "404";
        }
    }

    @PostMapping("/location/catalog/register")
    public String registerCatalog(@ModelAttribute("catalogForm") CatalogForm catalogForm) {
        centerService.updateCatalog(catalogForm);

        return "redirect:locationDetail";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        centerService.modelForCenterController(model);
    }

}
