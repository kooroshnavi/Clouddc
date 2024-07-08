package ir.tic.clouddc.resource;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/resource")
public class ResourceController {

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/device/{deviceId}/detail")
    public String showDeviceDetail(Model model, @RequestParam("deviceId") long deviceId) {

        var device = resourceService.getDeviceDetailModel(deviceId);

        if (device instanceof Server server) {
            model.addAttribute("server", server);
        } else if (device instanceof Switch sw) {
            model.addAttribute("sw", sw);
        } else if (device instanceof Firewall fw) {
            model.addAttribute("fw", fw);
        }
        model.addAttribute("device", device);

        return "deviceDetail";
    }

}
