package ir.tic.clouddc.resource;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;

@Controller
@RequestMapping("/resource")
public class ResourceController {

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/device/{deviceId}/detail")
    public String showDeviceDetail(Model model, @PathVariable Long deviceId) throws SQLException {

        var device = resourceService.getRefrencedDevice(deviceId);
        model.addAttribute("device", device);

        var catalogList = device.getDevicePmCatalogList();
        model.addAttribute("catalogList", catalogList);

       /* var eventList = eventService.getLocationEventList(baseLocation);
        model.addAttribute("eventList", eventList);*/

        return "deviceDetail";
    }

}
