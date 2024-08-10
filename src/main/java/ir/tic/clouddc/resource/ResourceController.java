package ir.tic.clouddc.resource;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/resource")
public class ResourceController {

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/device/{deviceId}/detail")
    public String showDeviceDetail(Model model, @PathVariable Long deviceId) throws EntityNotFoundException {
        var device = resourceService.getDevice(deviceId);
        if (device.isPresent()) {
            model.addAttribute("device", device.get());
            model.addAttribute("catalogList", device.get().getDevicePmCatalogList());
            return "deviceDetail";
        }

        return "404";
    }

    @GetMapping("/utilizer/{utilizerId}/detail")
    public String showUtilizerDetail(Model model, @PathVariable Integer utilizerId) throws EntityNotFoundException {
        var utilizer = resourceService.getUtilizer(utilizerId);
        if (utilizer != null) {
            model.addAttribute("utilizer", utilizer);
            model.addAttribute("rackList", utilizer.getRackList());
            model.addAttribute("deviceList", utilizer.getDeviceList());
            return "utilizerDetail";
        }

        return "404";
    }

    @GetMapping("/device/unassigned")
    public String newDeviceView(Model model){

        return "newDeviceView";

    }

}
