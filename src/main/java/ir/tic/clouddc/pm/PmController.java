package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.individual.Person;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        return "pmRegisterView";
    }

    @PostMapping(value = "/register")  /// General Pm only
    public String pmInterfacePost(
            Model model,
            @Valid @ModelAttribute("pmInterfaceRegisterForm") PmInterfaceRegisterForm pmInterfaceRegisterForm,
            @RequestParam("attachment") MultipartFile file,
            Errors errors) throws IOException {

        if (errors.hasErrors()) {
            log.error("Failed to register task due to validation error on input data: {}", errors);
            return "pmRegisterView";
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
        var pmInterface = pmService.getPmInterface(pmInterfaceId);
        if (pmInterface != null) {
            model.addAttribute("pmInterface", pmInterface);
            var pmList = pmService.getPmInterfacePmList(pmInterface.getId(), active);
            model.addAttribute("pmList", pmList);
            model.addAttribute("active", active);
        } else {
            return "404";
        }

        return "pmInterfacePmList";
    }

    @GetMapping("/{pmId}/detailList")
    public String showPmDetailPage(Model model, @PathVariable Long pmId) {
        var pm = pmService.getPmDetail_1(pmId);
        var pmDetailList = pmService.getPmDetail_2(pm);
        var metadataList = pmService.getPmDetail_3(pm);
        var catalog = pm.getCatalog();
        var pmInterface = catalog.getPmInterface();
        var location = catalog.getLocation();

        var activeDetail = pmDetailList.stream().filter(PmDetail::isActive).findFirst();

        if (activeDetail.isPresent()) {
            var permission = pmService.getPmDetail_4(activeDetail.get());
            log.info(String.valueOf(permission));
            model.addAttribute("permission", permission);
        }

        if (pm instanceof TemperaturePm) {
            List<TemperaturePmDetail> temperaturePmDetailList = new ArrayList<>();
            for (PmDetail pmDetail : pmDetailList) {
                temperaturePmDetailList.add((TemperaturePmDetail) pmDetail);
            }
            model.addAttribute("temperaturePmDetailList", temperaturePmDetailList);
        } else {
            model.addAttribute("pmDetailList", pmDetailList);
        }

        model.addAttribute("pmInterface", pmInterface);
        model.addAttribute("location", location);
        model.addAttribute("pm", pm);
        model.addAttribute("metadataList", metadataList);

        return "pmDetail";
    }

    @GetMapping("/{pmId}/form")
    public String showPmUpdateForm(Model model, @PathVariable Long pmId) {
        var optionalPm = pmService.getPm(pmId);
        if (optionalPm.isEmpty()) {
            return "404";
        } else {
            var pm = optionalPm.get();
            var catalog = pm.getCatalog();
            var pmInterface = catalog.getPmInterface();
            var location = catalog.getLocation();
            var pmOwnerUsername = pmService.getPmOwnerUsername(pmId);
            var assignPersonList = pmService.getAssignPersonList(pmOwnerUsername);

            model.addAttribute("location", location);
            model.addAttribute("pmInterface", pmInterface);
            model.addAttribute("pm", pm);
            model.addAttribute("pmUpdateForm", new PmUpdateForm());
            model.addAttribute("assignPersonList", assignPersonList);
            model.addAttribute("pmOwnerUsername", pmOwnerUsername);

            return "pmUpdateForm";

        }
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
            pmService.pmUpdate(pmUpdateForm, pm.get(), pmUpdateForm.getOwnerUsername());
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

    @GetMapping("/catalog/{locationId}/form")
    public String showCatalogForm(Model model, @PathVariable Long locationId) {
        List<Person> defaultPersonList = pmService.getDefaultPersonList();
        Optional<Location> optionalLocation = pmService.getLocation(locationId);
        if (optionalLocation.isPresent()) {
            List<PmInterface> pmList = pmService.getNonCatalogedPmList(optionalLocation.get());
            model.addAttribute("defaultPersonList", defaultPersonList);
            model.addAttribute("pmInterfaceList", pmList);
            model.addAttribute("catalogForm", new CatalogForm());
            model.addAttribute("update", false);
            model.addAttribute("location", optionalLocation.get());

            return "catalogForm";
        } else {
            return "404";
        }
    }

    @PostMapping("/catalog/register")
    public String pmCatalogRegister(Model model, @ModelAttribute CatalogForm catalogForm) {
        var nextDue = catalogForm.getNextDue();
        var validDate = LocalDate.parse(nextDue);
        log.info(String.valueOf(validDate));
        if (validDate.isBefore(LocalDate.now())) {
            return "403";
        }
        pmService.registerNewCatalog(catalogForm, validDate);


        return "redirect:/pm/workspace";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        pmService.modelForTaskController(model);
    }
}
