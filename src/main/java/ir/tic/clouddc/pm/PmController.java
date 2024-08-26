package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.center.LocationPmCatalog;
import ir.tic.clouddc.center.Rack;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.DevicePmCatalog;
import ir.tic.clouddc.security.ModifyProtection;
import ir.tic.clouddc.utils.UtilService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
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
        List<PmInterface> sortedList = pmInterfaceList.stream().sorted(Comparator.comparing(PmInterface::getId).reversed()).toList();
        model.addAttribute("pmInterfaceList", sortedList);

        if (!model.containsAttribute("pmInterfaceRegistered")) {
            model.addAttribute("pmInterfaceRegistered", false);
        }
        if (!model.containsAttribute("pmInterfaceUpdated")) {
            model.addAttribute("pmInterfaceUpdated", false);
        }

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
        var activePmCount = pmService.getPmInterfaceActivePmCount(pmInterface.getId());
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
        pmInterfaceRegisterForm.setTitle(pmInterface.getTitle());
        pmInterfaceRegisterForm.setDescription(pmInterface.getDescription());
        pmInterfaceRegisterForm.setPeriod(pmInterface.getPeriod());
        pmInterfaceRegisterForm.setEnabled(pmInterface.isEnabled());

        return pmInterfaceRegisterForm;
    }

    @PostMapping(value = "/register")  /// General Pm only
    public String pmInterfacePost(
            RedirectAttributes redirectAttributes,
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

        if (pmInterfaceRegisterForm.getPmInterfaceId() == null) {
            redirectAttributes.addFlashAttribute("pmInterfaceRegistered", true);
        } else {
            redirectAttributes.addFlashAttribute("pmInterfaceUpdated", true);
        }

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

        if (catalog instanceof LocationPmCatalog locationPmCatalog) {
            model.addAttribute("locationPmCatalog", locationPmCatalog);
            if (locationPmCatalog.getLocation() instanceof Rack rack) {
                model.addAttribute("rack", rack);
            }
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
            if (locationPmCatalog.getLocation() instanceof Rack rack) {
                model.addAttribute("rack", rack);
            }
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
    public String updatePm(@RequestParam("attachment") MultipartFile file,
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

    @RequestMapping(value = "/workspace", method = {RequestMethod.GET, RequestMethod.POST})
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
        model.addAttribute("enablementAccess", false);

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
        model.addAttribute("enablementAccess", false);

        return "catalogForm";
    }

    @GetMapping("/catalog/{catalogId}/edit")
    @ModifyProtection
    public String catalogEditForm(Model model, @PathVariable Long catalogId) {
        if (Objects.equals(catalogId, null) || catalogId == 0 || catalogId < 0) {
            return "404";
        }
        var catalog = pmService.getReferencedCatalog(catalogId);
        if (!catalog.getPmInterface().isEnabled()) {
            return "403";
        }
        var activePmCount = pmService.getCatalogActivePmCount(catalog.getId());
        if (activePmCount > 0) {
            model.addAttribute("enablementAccess", false);
        } else {
            model.addAttribute("enablementAccess", true);
        }
        List<Person> defaultPersonList = pmService.getDefaultPersonList();
        CatalogForm catalogForm = new CatalogForm();
        catalogForm.setEnabled(catalog.isEnabled());
        catalogForm.setDefaultPersonId(catalog.getDefaultPerson().getId());

        model.addAttribute("catalogForm", catalogForm);
        model.addAttribute("catalog", catalog);
        model.addAttribute("update", true);
        model.addAttribute("isLocation", false);
        model.addAttribute("isDevice", false);
        model.addAttribute("defaultPersonList", defaultPersonList);

        return "catalogForm";
    }

    @PostMapping("/catalog/register")
    @ModifyProtection
    public String pmCatalogRegister(@ModelAttribute CatalogForm catalogForm, RedirectAttributes redirectAttributes) throws SQLException {
        var nextDue = catalogForm.getNextDue();
        var validDate = LocalDate.parse(nextDue);
        log.info(String.valueOf(validDate));
        if (validDate.isBefore(UtilService.getDATE())) {
            return "403";
        }
        var pmInterfaceId = pmService.registerNewCatalog(catalogForm, validDate);

        if (catalogForm.getPmInterfaceCatalogId() == null) {
            redirectAttributes.addFlashAttribute("catalogRegistered", true);
        } else {
            redirectAttributes.addFlashAttribute("catalogUpdated", true);
        }
        redirectAttributes.addAttribute("pmInterfaceId", pmInterfaceId);

        return "redirect:/pm/{pmInterfaceId}/catalog/list";
    }

    @GetMapping("/{pmInterfaceId}/catalog/list")
    public String pmInterfaceCatalogView(Model model, @PathVariable Integer pmInterfaceId) {
        var pmInterface = Hibernate.unproxy(pmService.getReferencedPmInterface(pmInterfaceId), PmInterface.class);
        List<LocationPmCatalog> locationPmCatalogList = new ArrayList<>();
        List<DevicePmCatalog> devicePmCatalogList = new ArrayList<>();

        for (PmInterfaceCatalog pmInterfaceCatalog : pmInterface.getPmInterfaceCatalogList()) {
            if (pmInterfaceCatalog instanceof LocationPmCatalog locationPmCatalog) {
                locationPmCatalogList.add(locationPmCatalog);
            } else if (pmInterfaceCatalog instanceof DevicePmCatalog devicePmCatalog) {
                devicePmCatalogList.add(devicePmCatalog);
            }
        }
        if (!locationPmCatalogList.isEmpty()) {
            model.addAttribute("locationPmCatalogList", locationPmCatalogList.stream().sorted(Comparator.comparing(PmInterfaceCatalog::getId).reversed()));
        } else if (!devicePmCatalogList.isEmpty()) {
            model.addAttribute("devicePmCatalogList", devicePmCatalogList.stream().sorted(Comparator.comparing(PmInterfaceCatalog::getId).reversed()));
        }
        model.addAttribute("pmInterface", pmInterface);

        if (!model.containsAttribute("catalogRegistered")) {
            model.addAttribute("catalogRegistered", false);
        }
        if (!model.containsAttribute("catalogUpdated")) {
            model.addAttribute("catalogUpdated", false);
        }

        return "catalogList";
    }

    @GetMapping("/catalog/{catalogId}/pmList/{active}")
    public String showCatalogPmHistory(Model model, @PathVariable Long catalogId, @PathVariable boolean active) {
        var catalog = Hibernate.unproxy(pmService.getReferencedCatalog(catalogId), PmInterfaceCatalog.class);
        var pmList = pmService.getCatalogPmList(catalogId, active);

        if (catalog instanceof LocationPmCatalog locationPmCatalog) {
            model.addAttribute("locationPmCatalog", locationPmCatalog);
            if (locationPmCatalog.getLocation() instanceof Rack rack) {
                model.addAttribute("rack", rack);
            }
        } else if (catalog instanceof DevicePmCatalog devicePmCatalog) {
            model.addAttribute("devicePmCatalog", devicePmCatalog);
        }

        model.addAttribute("catalog", catalog);
        model.addAttribute("active", active);
        model.addAttribute("pmList", pmList);

        return "catalogPmList";
    }

}
