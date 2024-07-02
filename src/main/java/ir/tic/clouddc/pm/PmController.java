package ir.tic.clouddc.pm;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping(value = {"/"})
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

    @GetMapping("/{pmInterfaceId}/register/form")
    public String showPmInterfaceForm(Model model, @Nullable @PathVariable Short pmInterfaceId) {
        if (pmInterfaceId == null) {
            var pmCategoryList = pmService.getPmInterfaceFormData();
            model.addAttribute("pmCategoryList", pmCategoryList);
            model.addAttribute("pmInterfaceRegisterForm", new PmInterfaceRegisterForm());
        } else {
            var pmInterfaceForm = pmService.pmInterfaceEditFormData(pmInterfaceId);
            var pmCategoryList = pmService.getPmInterfaceFormData();
            model.addAttribute("pmCategoryList", pmCategoryList);
            model.addAttribute("pmInterfaceRegisterForm", pmInterfaceForm);
        }

        return "pmRegisterView";
    }

    @PostMapping("/register")  /// General Pm only
    public String pmInterfacePost(
            Model model,
            @Valid @ModelAttribute("pmInterfaceRegisterForm") PmInterfaceRegisterForm pmInterfaceRegisterForm,
            @RequestParam("attachment") MultipartFile file,
            Errors errors) throws IOException {

        if (errors.hasErrors()) {
            log.error("Failed to register task due to validation error on input data: " + errors);
            return "pmRegisterView";
        }

        if (!file.isEmpty()) {
            pmInterfaceRegisterForm.setFile(file);
        }

        pmService.pmInterfaceRegister(pmInterfaceRegisterForm);
        return "redirect:\\pmInterfaceList";
    }

    @GetMapping("/{pmInterfaceId}/active/{active}/location/{locationId}")
    public String showPmInterfacePmList(Model model
            , @PathVariable boolean active
            , @PathVariable short pmInterfaceId
            , @Nullable @PathVariable(required = false) Integer locationId) {
        var pmList = pmService.getPmInterfacePmList(pmInterfaceId, active, locationId);
        model.addAttribute("pmList", pmList);
        model.addAttribute("pmInterface", pmList.get(0).getPmInterface());
        model.addAttribute("active", active);

        return "pmInterfacePmList";
    }

    @GetMapping("/{pmId}/detailList")
    public String showPmDetailPage(Model model, @PathVariable int pmId) {
        var pm = pmService.getPmDetail_1(pmId);
        var pmDetailList = pmService.getPmDetail_2(pm);
        var metadataList = pmService.getPmDetail_3(pm);
        var activeDetail = pmDetailList.stream().filter(PmDetail::isActive).findFirst();

        if (activeDetail.isPresent()) {
            var permission = pmService.getPmDetail_4(activeDetail.get());
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

        model.addAttribute("pmInterface", pm.getPmInterface());
        model.addAttribute("pm", pm);
        model.addAttribute("metadataList", metadataList);

        return "pmDetail";
    }

    @GetMapping("/{pmId}/form")
    public String showPmUpdateForm(Model model, @PathVariable int pmId) {
        var pm = pmService.getPm(pmId);
        var pmOwnerUsername = pmService.getPmOwnerUsername(pmId);
        var pmUpdateForm = pmService.getPmUpdateForm(pm, pmOwnerUsername);
        var assignPersonList = pmService.getAssignPersonList(pmOwnerUsername);

        model.addAttribute("pmInterface", pm.getPmInterface());
        model.addAttribute("pm", pm);
        model.addAttribute("pmUpdateForm", pmUpdateForm);
        model.addAttribute("assignPersonList", assignPersonList);

        return "pmUpdateForm";
    }

    @PostMapping("/update")
    public String updatePm(Model model,
                           @RequestParam("attachment") MultipartFile file,
                           @ModelAttribute("pmUpdateForm") PmUpdateForm pmUpdateForm) throws IOException {
        if (!file.isEmpty()) {
            pmUpdateForm.setFile(file);
        }
        pmService.pmUpdate(pmUpdateForm, pmUpdateForm.getPm(), pmUpdateForm.getOwnerUsername());
        // pmService.modelForActivePersonTaskList(model);
        return "redirect:activePmList";
    }

    @GetMapping("/workspace")
    private String showWorkspace(Model model) {
        var activePmList = pmService.getActivePmList(true, true);
        model.addAttribute("workspace", true);
        model.addAttribute("activePmList", activePmList);

        return "activePmList";
    }

    @GetMapping("/active/list")
    private String showActivePmList(Model model) {
        var activePmList = pmService.getActivePmList(true, false);
        model.addAttribute("workspace", false);
        model.addAttribute("activePmList", activePmList);

        return "activePmList";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        pmService.modelForTaskController(model);
    }
}
