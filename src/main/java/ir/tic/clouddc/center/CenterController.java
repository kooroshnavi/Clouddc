package ir.tic.clouddc.center;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/location/{locationId}/detail") // Covers Room Rack and Hall
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

    @PostMapping("/rack/position/update")
    public String updateRackDevicePosition(RedirectAttributes redirectAttributes, @ModelAttribute("rackDeviceOrderForm") RackDeviceOrderForm rackDeviceOrderForm) {
        var order = rackDeviceOrderForm.getOrderList().get(0);
        var stringDeviceOrderIdSet = StringUtils.commaDelimitedListToSet(order);
        if (!stringDeviceOrderIdSet.isEmpty()) {
            centerService.updateRackDevicePosition(rackDeviceOrderForm.getRackId(), stringDeviceOrderIdSet);
            redirectAttributes.addFlashAttribute("devicePositionUpdated", true);

        } else {
            redirectAttributes.addFlashAttribute("devicePositionUpdated", false);
        }

        redirectAttributes.addAttribute("locationId", rackDeviceOrderForm.getRackId());

        return "redirect:/center/location/{locationId}/detail";
    }


    @ModelAttribute
    public void addAttributes(Model model) {
        centerService.modelForCenterController(model);
    }
}
