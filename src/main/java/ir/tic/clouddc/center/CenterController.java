package ir.tic.clouddc.center;

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

    @Autowired
    public CenterController(CenterService centerService ) {
        this.centerService = centerService;
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
        if (baseLocation.isPresent()) {
            model.addAttribute("location", baseLocation.get());
            var catalogList = baseLocation.get().getLocationPmCatalogList();
            model.addAttribute("catalogList", catalogList);

            if (baseLocation.get() instanceof Hall location) {
                model.addAttribute("hall", location);
                model.addAttribute("rackList", location.getRackList());
            } else if (baseLocation.get() instanceof Rack location) {
                model.addAttribute("rack", location);
                model.addAttribute("rackDeviceList", location.getDeviceList());
            } else if (baseLocation.get() instanceof Room location) {
                model.addAttribute("room", location);
                model.addAttribute("roomDeviceList", location.getDeviceList());
            }
            return "locationDetail";
        }

        return "404";


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
