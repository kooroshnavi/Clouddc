package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.center.LocationPmCatalog;
import ir.tic.clouddc.individual.Person;
import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.DevicePmCatalog;
import ir.tic.clouddc.security.ModifyProtection;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping(value = {"/pm"})
public class PmController {

    private final PmService pmService;

    @Autowired
    public PmController(PmServiceImpl taskService) {
        this.pmService = taskService;
    }

    @GetMapping("/interface/list")
    public String showPmInterfaceList(Model model) {
        List<PmInterface> pmInterfaceList = pmService.getPmInterfaceList();
        model.addAttribute("pmInterfaceList", pmInterfaceList);
        return "pmInterfaceList";
    }

    @GetMapping("/register/form")
    @ModifyProtection
    public String showPmInterfaceForm(Model model) {

        model.addAttribute("pmInterfaceRegisterForm", new PmInterfaceRegisterForm());
        model.addAttribute("isUpdate", false);
        model.addAttribute("enablementAccess", true);

        return "pmInterfaceRegisterView";
    }

    @GetMapping("/{pmInterfaceId}/edit")
    @ModifyProtection
    public String pmInterfaceEditForm(Model model, @PathVariable Integer pmInterfaceId) {
        if (Objects.equals(pmInterfaceId, null) || pmInterfaceId == 0 || pmInterfaceId < 0) {
            return "404";
        }
        var pmInterface = pmService.getReferencedPmInterface(pmInterfaceId);
        var activePmCount = pmService.getActivePmCount(pmInterface.getId());
        if (activePmCount > 0) {
            model.addAttribute("enablementAccess", false);
        } else {
            model.addAttribute("enablementAccess", true);
        }
        PmInterfaceRegisterForm pmInterfaceRegisterForm = getPmInterfaceRegisterForm(pmInterface);

        model.addAttribute("pmInterfaceRegisterForm", pmInterfaceRegisterForm);
        model.addAttribute("pmInterface", pmInterface);
        model.addAttribute("isUpdate", true);

        return "pmInterfaceRegisterView";
    }

    private static PmInterfaceRegisterForm getPmInterfaceRegisterForm(PmInterface pmInterface) {
        PmInterfaceRegisterForm pmInterfaceRegisterForm = new PmInterfaceRegisterForm();
        pmInterfaceRegisterForm.setTitle(pmInterface.getName());
        pmInterfaceRegisterForm.setDescription(pmInterface.getDescription());
        pmInterfaceRegisterForm.setPeriod(pmInterface.getPeriod());
        pmInterfaceRegisterForm.setEnabled(pmInterface.isEnabled());
        pmInterfaceRegisterForm.setStatelessRecurring(pmInterface.isStatelessRecurring());
        return pmInterfaceRegisterForm;
    }

    @PostMapping(value = "/register")  /// General Pm only
    public String pmInterfacePost(
            Model model,
            @Valid @ModelAttribute("pmInterfaceRegisterForm") PmInterfaceRegisterForm pmInterfaceRegisterForm,
            @RequestParam("attachment") MultipartFile file,
            Errors errors) throws IOException {

        if (errors.hasErrors()) {
            log.error("Failed to register task due to validation error on input data: {}", errors);
            return "redirect:/pm/register/form";
        }

        if (!file.isEmpty()) {
            pmInterfaceRegisterForm.setFile(file);
        }

        pmService.pmInterfaceRegister(pmInterfaceRegisterForm);
        return "redirect:/pm/interface/list";
    }

    @GetMapping("/{pmInterfaceId}/active/{active}")
    public String showPmInterfacePmList(Model model
            , @PathVariable boolean active
            , @PathVariable Integer pmInterfaceId) {

        var pmInterface = pmService.getReferencedPmInterface(pmInterfaceId);
        if (pmInterface != null) {

            var pmList = pmService.getPmInterfacePmList(pmInterface.getId(), active);

            if (!pmList.isEmpty() && pmInterface.getCategoryId() == 1) {
                var locationPmCatalog = (LocationPmCatalog) pmList.get(0).getPmInterfaceCatalog();
                model.addAttribute("locationPmCatalog", locationPmCatalog);
            } else if (!pmList.isEmpty() && pmInterface.getCategoryId() == 2) {
                var devicePmCatalog = (DevicePmCatalog) pmList.get(0).getPmInterfaceCatalog();
                model.addAttribute("devicePmCatalog", devicePmCatalog);
            }

            model.addAttribute("pmInterface", pmInterface);
            model.addAttribute("pmList", pmList);
            model.addAttribute("active", active);
        } else {
            return "404";
        }

        return "pmInterfacePmList";
    }

    @GetMapping("/{pmId}/detailList")
    public String showPmDetailPage(Model model, @PathVariable Long pmId) throws SQLException {
        var pm = pmService.getRefrencedPm(pmId);
        var pmDetailList = pmService.getPmDetail_2(pm);
        var metadataList = pmService.getPmDetail_3(pm);
        var catalog = pm.getPmInterfaceCatalog();
        var pmInterface = catalog.getPmInterface();

        var activeDetail = pmDetailList.stream().filter(PmDetail::isActive).findFirst();

        if (activeDetail.isPresent()) {
            var permission = pmService.getPmDetail_4(activeDetail.get());
            log.info(String.valueOf(permission));
            model.addAttribute("permission", permission);
        }

     /*   if (pm instanceof TemperaturePm) {
            List<TemperaturePmDetail> temperaturePmDetailList = new ArrayList<>();
            for (PmDetail pmDetail : pmDetailList) {
                temperaturePmDetailList.add((TemperaturePmDetail) pmDetail);
            }
            model.addAttribute("temperaturePmDetailList", temperaturePmDetailList);
        } else {
            model.addAttribute("pmDetailList", pmDetailList);
        }*/

        if (catalog instanceof LocationPmCatalog locationPmCatalog) {
            model.addAttribute("locationPmCatalog", locationPmCatalog);
        } else if (catalog instanceof DevicePmCatalog devicePmCatalog) {
            model.addAttribute("devicePmCatalog", devicePmCatalog);
        }

        model.addAttribute("pmDetailList", pmDetailList);
        model.addAttribute("pmInterface", pmInterface);
        model.addAttribute("pm", pm);
        model.addAttribute("metadataList", metadataList);

        return "pmDetail";
    }

    @GetMapping("/{pmId}/form")
    public String showPmUpdateForm(Model model, @PathVariable Long pmId) throws SQLException {
        var pm = pmService.getRefrencedPm(pmId);
        var pmOwnerUsername = pmService.getPmOwnerUsername(pmId, pm.isActive());
        var assignPersonList = pmService.getAssignPersonList(pmOwnerUsername);
        var catalog = pm.getPmInterfaceCatalog();
        var pmInterface = catalog.getPmInterface();

        if (catalog instanceof LocationPmCatalog locationPmCatalog) {
            model.addAttribute("locationPmCatalog", locationPmCatalog);
        } else if (catalog instanceof DevicePmCatalog devicePmCatalog) {
            model.addAttribute("devicePmCatalog", devicePmCatalog);
        }

        model.addAttribute("pmInterface", pmInterface);
        model.addAttribute("pm", pm);
        model.addAttribute("pmUpdateForm", new PmUpdateForm());
        model.addAttribute("assignPersonList", assignPersonList);
        model.addAttribute("pmOwnerUsername", pmOwnerUsername);

        return "pmUpdateForm";
    }

    @PostMapping(value = "/update")
    public String updatePm(Model model,
                           @RequestParam("attachment") MultipartFile file,
                           @Valid @ModelAttribute("pmUpdateForm") PmUpdateForm pmUpdateForm) throws IOException {
        if (!file.isEmpty()) {
            pmUpdateForm.setFile(file);
        }
        var pm = pmService.getPm(pmUpdateForm.getPmId());
        if (pm.isPresent()) {
            pmService.updatePm(pmUpdateForm, pm.get(), pmUpdateForm.getOwnerUsername());
            return "redirect:/pm/workspace";
        }

        return "404";
    }

    @GetMapping("/workspace")
    public String showWorkspace(Model model) {
        var activePmList = pmService.getActivePmList(true, true);
        model.addAttribute("workspace", true);
        model.addAttribute("activePmList", activePmList);

        return "activePmList";
    }

    @GetMapping("/active/list")
    public String showActivePmList(Model model) {
        var activePmList = pmService.getActivePmList(true, false);
        model.addAttribute("workspace", false);
        model.addAttribute("activePmList", activePmList);

        return "activePmList";
    }

    @GetMapping("/catalog/location/{locationId}/form")
    public String showLocationCatalogForm(Model model, @PathVariable Long locationId) throws SQLException {
        List<Person> defaultPersonList = pmService.getDefaultPersonList();
        Location location = pmService.getReferencedLocation(locationId);

        List<PmInterface> pmInterfaceList = pmService.getNonCatalogedPmInterfaceList(location, null);
        model.addAttribute("defaultPersonList", defaultPersonList);
        model.addAttribute("pmInterfaceList", pmInterfaceList);
        model.addAttribute("catalogForm", new CatalogForm());
        model.addAttribute("update", false);
        model.addAttribute("location", location);
        model.addAttribute("isLocation", true);
        model.addAttribute("isDevice", false);

        return "catalogForm";

    }

    @GetMapping("/catalog/device/{deviceId}/form")
    public String showDeviceCatalogForm(Model model, @PathVariable Long deviceId) throws SQLException {
        List<Person> defaultPersonList = pmService.getDefaultPersonList();
        Device device = pmService.getDevice(deviceId);

        List<PmInterface> pmInterfaceList = pmService.getNonCatalogedPmInterfaceList(null, device);
        model.addAttribute("defaultPersonList", defaultPersonList);
        model.addAttribute("pmInterfaceList", pmInterfaceList);
        model.addAttribute("catalogForm", new CatalogForm());
        model.addAttribute("update", false);
        model.addAttribute("device", device);
        model.addAttribute("isLocation", false);
        model.addAttribute("isDevice", true);

        return "catalogForm";

    }

    @PostMapping("/catalog/register")
    @ModifyProtection
    public String pmCatalogRegister(Model model, @ModelAttribute CatalogForm catalogForm) throws SQLException {
        var nextDue = catalogForm.getNextDue();
        var validDate = LocalDate.parse(nextDue);
        log.info(String.valueOf(validDate));
        if (validDate.isBefore(LocalDate.now())) {
            return "403";
        }
        pmService.registerNewCatalog(catalogForm, validDate);

        return "redirect:/pm/workspace";
    }
/*
    @ModelAttribute
    public void addAttributes(Model model) {
        pmService.modelForTaskController(model);
    }*/
}
