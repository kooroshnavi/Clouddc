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


       /* var eventList = eventService.getLocationEventList(baseLocation);
        model.addAttribute("eventList", eventList);*/
        return "404";

    }

}
