package ir.tic.clouddc.resource;


import ir.tic.clouddc.utils.UtilService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
        var device = resourceService.getReferencedDevice(deviceId);
        if (device.getDevicePmCatalogList() != null) {
            for (DevicePmCatalog catalog : device.getDevicePmCatalogList()) {
                catalog.setPersianNextDue(UtilService.getFormattedPersianDate(catalog.getNextDueDate()));
            }
        }
        Map<ModuleCategory, Long> moduleOverviewMap = resourceService.getDeviceModuleOverviewMap(deviceId);

        model.addAttribute("moduleMap", moduleOverviewMap);
        model.addAttribute("device", device);
        model.addAttribute("catalogList", device.getDevicePmCatalogList());

        return "deviceDetail";
    }

    @GetMapping("/utilizer/list")
    public String showUtilizerList(Model model) {
        List<Utilizer> utilizerList = new java.util.ArrayList<>(resourceService
                .getUtilierList()
                .stream()
                .sorted(Comparator.comparing(utilizer -> utilizer.getDeviceList().size()))
                .toList());
        Collections.reverse(utilizerList);

        model.addAttribute("utilizerList", utilizerList);

        return "UtilizerList";
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
        List<ResourceService.DeviceIdSerialCategoryVendor_Projection1> unassignedDeviceList =
                resourceService.getNewDeviceList()
                        .stream()
                        .sorted(Comparator.comparing(ResourceService.DeviceIdSerialCategoryVendor_Projection1::getId).reversed())
                        .toList();

        model.addAttribute("deviceRegisterForm", new ResourceRegisterForm());
        model.addAttribute("deviceCategoryList", deviceCategoryList);
        model.addAttribute("unassignedDeviceList", unassignedDeviceList);

        if (!model.containsAttribute("newDevice")) {
            model.addAttribute("newDevice", false);
        }
        if (!model.containsAttribute("exist")) {
            model.addAttribute("exist", false);
        }

        return "newDeviceView";
    }

    @PostMapping("/device/register")
    public String registerNewDevice(RedirectAttributes redirectAttributes, @ModelAttribute("deviceRegisterForm") ResourceRegisterForm resourceRegisterForm) {

        boolean exist = resourceService.checkDeviceExistence(resourceRegisterForm.getSerialNumber());

        if (!exist) {
            resourceService.registerUnassignedDevice(resourceRegisterForm);
            redirectAttributes.addFlashAttribute("newDevice", true);
        } else {
            var existedDeviceId = resourceService.getDeviceIdBySerialNumber(resourceRegisterForm.getSerialNumber());
            if (existedDeviceId.isPresent()) {
                redirectAttributes.addFlashAttribute("existedDeviceId", existedDeviceId.get());
                redirectAttributes.addFlashAttribute("existedUnassigned", false);
            } else {
                redirectAttributes.addFlashAttribute("existedUnassigned", true);
            }
            redirectAttributes.addFlashAttribute("exist", true);
            redirectAttributes.addFlashAttribute("existedSerialNumber", resourceRegisterForm.getSerialNumber());
        }
        return "redirect:/resource/device/unassigned";
    }
}
