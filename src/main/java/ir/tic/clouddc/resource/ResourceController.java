package ir.tic.clouddc.resource;


import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/resource")
@Slf4j
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
    public String newDeviceView(Model model) {

        List<DeviceCategory> deviceCategoryList = resourceService.getdeviceCategoryList();
        List<ResourceService.DeviceIdSerialCategoryVendor_Projection1> unassignedDeviceList = resourceService.getNewDeviceList();

        model.addAttribute("deviceRegisterForm", new DeviceRegisterForm());
        model.addAttribute("deviceCategoryList", deviceCategoryList);
        model.addAttribute("unassignedDeviceList", unassignedDeviceList);

        return "newDeviceView";
    }

    @PostMapping("/register")
    public String registerDevice(RedirectAttributes redirectAttributes, @ModelAttribute("deviceRegisterForm") DeviceRegisterForm deviceRegisterForm) {

        boolean exist = resourceService.checkDeviceExistence(deviceRegisterForm.getSerialNumber());

        if (!exist) {
            log.info("Registering new device");
            resourceService.registerUnassignedDevice(deviceRegisterForm);
            redirectAttributes.addFlashAttribute("newDevice", true);
        }
        else {
            redirectAttributes.addFlashAttribute("exist", true);
        }
        return "redirect:/resource/device/unassigned";
    }

}
