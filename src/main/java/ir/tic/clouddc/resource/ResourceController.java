package ir.tic.clouddc.resource;


import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.utils.UtilService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

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
        Map<ModuleInventory, Integer> deviceModuleOverviewMap = resourceService.getDeviceModuleOverview(device.getModulePackList());
        var sortedKeySet = deviceModuleOverviewMap
                .keySet()
                .stream()
                .sorted(Comparator.comparing(ModuleInventory::getClassification)).toList();

        model.addAttribute("sortedKeyList", sortedKeySet);
        model.addAttribute("moduleMap", deviceModuleOverviewMap);
        model.addAttribute("device", device);
        model.addAttribute("catalogList", device.getDevicePmCatalogList());

        return "deviceDetail";
    }

    @GetMapping("/device/{deviceId}/module/form")
    public String showDeviceModuleUpdateForm(Model model, @PathVariable long deviceId) {
        Device device = resourceService.getReferencedDevice(deviceId);
        List<ModuleInventory> compatibleModuleInventoryList = new ArrayList<>(resourceService
                .getDeviceCompatibleModuleInventoryList(device.getDeviceCategory().getId())
                .stream()
                .sorted(Comparator.comparing(ModuleInventory::getClassification))
                .toList());
        Map<ModuleInventory, Integer> deviceModuleMap = new HashMap<>();
        var packList = device.getModulePackList();
        for (ModulePack modulePack : packList) {
            deviceModuleMap.put(modulePack.getModuleInventory(), modulePack.getQty());
        }
        for (ModuleInventory moduleInventory : compatibleModuleInventoryList) {
            if (!deviceModuleMap.containsKey(moduleInventory)) {
                deviceModuleMap.put(moduleInventory, 0);
            }
        }

        compatibleModuleInventoryList.removeIf(inventory -> inventory.getAvailable() == 0 && deviceModuleMap.getOrDefault(inventory, 0) == 0);
        List<ModuleInventory> compatibleStorageInventoryList = compatibleModuleInventoryList
                .stream()
                .filter(moduleInventory -> moduleInventory.getClassification().equals("Storage"))
                .toList();
        List<Storage> deviceAssignedAndSpareStorageList = resourceService.getDeviceAssignedAndSpareStorageList(deviceId, compatibleStorageInventoryList);

        if (!model.containsAttribute("success")) {
            model.addAttribute("success", false);
        }

        model.addAttribute("device", device);
        model.addAttribute("compatibleModuleInventoryList", compatibleModuleInventoryList);
        model.addAttribute("deviceModuleMap", deviceModuleMap);
        model.addAttribute("deviceAssignedAndSpareStorageList", deviceAssignedAndSpareStorageList);
        model.addAttribute("updateForm", new ModuleUpdateForm());

        return "deviceModuleForm2";
    }

    @PostMapping("/device/module/update")
    public String updateDeviceModule(RedirectAttributes redirectAttributes, @ModelAttribute("updateForm") ModuleUpdateForm moduleUpdateForm) {
        long result = resourceService.updateDeviceModule(moduleUpdateForm);

        redirectAttributes.addFlashAttribute("success", true);
        redirectAttributes.addAttribute("deviceId", result);

        return "redirect:/resource/device/{deviceId}/module/form";
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

    @GetMapping("/module/inventory")
    public String showModuleInventory(Model model) {
        Map<ModuleInventory, Integer> moduleOverviewMap = resourceService.getModuleOverviewMap();
        var sortedKeySet = moduleOverviewMap
                .keySet()
                .stream()
                .sorted(Comparator.comparing(ModuleInventory::getClassification));

        model.addAttribute("sortedKeySet", sortedKeySet);
        model.addAttribute("moduleOverviewMap", moduleOverviewMap);

        if (!model.containsAttribute("newModuleRegistered")) {
            model.addAttribute("newModuleRegistered", false);
        }

        if (!model.containsAttribute("availabilityUpdated")) {
            model.addAttribute("availabilityUpdated", false);
        }

        return "moduleInventory";
    }

    @GetMapping("/module/category/{categoryId}/detail")
    public String showInventoryDetail_1(Model model, @PathVariable Integer categoryId) {
        List<ModuleInventory> inventoryDetailList = resourceService.getRelatedModuleInventoryList(categoryId);
        var category = inventoryDetailList.stream().findFirst();
        category.ifPresent(moduleInventory -> model.addAttribute("theCategory", moduleInventory));
        model.addAttribute("inventoryDetailList", inventoryDetailList);
        model.addAttribute("updateForm", new ModuleUpdateForm());

        return "moduleDetail";
    }

    @PostMapping("/module/update")
    public String decreaseInventoryAvailability(RedirectAttributes redirectAttributes, @ModelAttribute("updateForm") ModuleUpdateForm moduleUpdateForm) {
        resourceService.inventoryUpdate(moduleUpdateForm);
        redirectAttributes.addFlashAttribute("availabilityUpdated", true);

        return "redirect:/resource/module/inventory";
    }

    @GetMapping("/module/storage/{inventoryId}/detail")
    public String showInventoryDetail_2(Model model, @PathVariable Integer inventoryId) {
        List<Storage> storageList = resourceService.getRelatedSpareStorageList(inventoryId);
        var inventory = storageList.stream().map(Storage::getModuleInventory).findAny();
        inventory.ifPresent(moduleInventory -> model.addAttribute("inventory", moduleInventory));
        model.addAttribute("storageList", storageList);
        model.addAttribute("room1Id", CenterService.ROOM_1_ID);
        model.addAttribute("room2Id", CenterService.ROOM_2_ID);
        model.addAttribute("room412Id", CenterService.ROOM_412_ID);
        model.addAttribute("updateForm", new ModuleUpdateForm());

        return "storageDetail";
    }

    @GetMapping("/module/register/form")
    public String showModuleRegisterForm(Model model) {
        List<ModuleInventory> moduleCategoryList = resourceService
                .getModuleCategoryList()
                .stream()
                .sorted(Comparator.comparing(ModuleInventory::getCategoryId))
                .toList();

        model.addAttribute("moduleCategoryList", moduleCategoryList);
        model.addAttribute("moduleRegisterForm", new ResourceRegisterForm());
        model.addAttribute("room1Id", CenterService.ROOM_1_ID);
        model.addAttribute("room2Id", CenterService.ROOM_2_ID);
        model.addAttribute("room412Id", CenterService.ROOM_412_ID);

        if (!model.containsAttribute("exist")) {
            model.addAttribute("exist", false);
        }

        return "newModuleRegView";
    }

    @PostMapping("/module/register")
    public String registerModuleHandler(RedirectAttributes redirectAttributes, @ModelAttribute("moduleRegisterForm") ResourceRegisterForm resourceRegisterForm) {
        if (resourceRegisterForm.getResourceCategoryId() >= 1047 && resourceRegisterForm.getResourceCategoryId() <= 1075) { // check storage existence
            boolean exist = resourceService.checkResourceExistence(resourceRegisterForm.getSerialNumber(), 2);
            if (exist) {
                redirectAttributes.addFlashAttribute("exist", true);

                return "redirect:/resource/module/register/form";
            }
        }
        resourceService.resourceRegister(resourceRegisterForm, 2);
        redirectAttributes.addFlashAttribute("newModuleRegistered", true);

        return "redirect:/resource/module/inventory";
    }

    @PostMapping("/device/register")
    public String registerDeviceHandler(RedirectAttributes redirectAttributes, @ModelAttribute("deviceRegisterForm") ResourceRegisterForm resourceRegisterForm) {

        boolean exist = resourceService.checkResourceExistence(resourceRegisterForm.getSerialNumber(), 1);

        if (!exist) {
            resourceService.resourceRegister(resourceRegisterForm, 1);
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
