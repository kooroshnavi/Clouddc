package ir.tic.clouddc.center;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/center")
public class CenterController {
    private final CenterService centerService;

    @Autowired
    public CenterController(CenterService centerService) {
        this.centerService = centerService;
    }

    @GetMapping("/overview")
    public String showCenterLandingPage(Model model) {
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
                centerService.verifyRackDevicePosition(location.getRackList());
                model.addAttribute("hall", location);
                model.addAttribute("rackList", location.getRackList());
            } else if (baseLocation.get() instanceof Rack location) {
                centerService.verifyRackDevicePosition(List.of(location));
                model.addAttribute("rack", location);
                model.addAttribute("rackDevicePositionMap", location.getDevicePositionMap());
            } else if (baseLocation.get() instanceof Room location) {
                model.addAttribute("room", location);
                model.addAttribute("roomDeviceList", location.getDeviceList());
            }

            if (!model.containsAttribute("devicePositionUpdated")) {
                model.addAttribute("devicePositionUpdated", false);
            }

            return "locationDetail";
        }

        return "404";
    }

    @GetMapping("/rack/{rackId}/deviceOrder")
    private String rackDeviceOrderingView(Model model, @PathVariable Long rackId) {
        var location = Hibernate.unproxy(centerService.getRefrencedLocation(rackId), Location.class);
        if (location instanceof Rack rack) {
            model.addAttribute("rack", rack);
            model.addAttribute("rackDeviceOrderForm", new RackDeviceOrderForm());

            return "rackDeviceOrdering";
        } else {
            return "404";
        }
    }


    @ModelAttribute
    public void addAttributes(Model model) {
        centerService.modelForCenterController(model);
    }
}
