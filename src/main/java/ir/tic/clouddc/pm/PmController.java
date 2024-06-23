package ir.tic.clouddc.pm;

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


    @GetMapping("/list")
    public String showPmInterfaceList(Model model) {
        List<PmInterface> pmInterfaceList = pmService.getPmInterfaceList();
        model.addAttribute("pmInterfaceList", pmInterfaceList);
        return "pmInterfaceList";
    }

    @GetMapping("/register/form")    /// General Pm only
    public String showPmInterfaceRegisterForm(Model model) {
        pmService.getPmInterfaceFormData(model);
        return "pmInterfaceRegister";
    }

    @GetMapping("/{pmInterfaceId}/edit/form")
    public String showPmInterfaceEditForm(@RequestParam short pmInterfaceId, Model model) {
        pmService.pmInterfaceEditFormData(model, pmInterfaceId);
        return "pmInterfaceRegister";
    }

    @PostMapping("/register")  /// General Pm only
    public String pmInterfacePost(
            Model model,
            @Valid @ModelAttribute("pmRegister") pmInterfaceRegisterForm pmInterfaceRegisterForm,
            @RequestParam("attachment") MultipartFile file,
            Errors errors) throws IOException {

        if (errors.hasErrors()) {
            log.error("Failed to register task due to validation error on input data: " + errors);
            return showPmInterfaceRegisterForm(model);
        }

        if (!file.isEmpty()) {
            pmInterfaceRegisterForm.setFile(file);
        }

        pmService.pmInterfaceRegister(pmInterfaceRegisterForm);
        return "pmList";
    }

    @GetMapping("/{pmInterfaceId}/active/{active}/location/{locationId}")
    public String showPmInterfacePmTaskList(Model model, @PathVariable boolean active, @PathVariable(required = false) int locationId, @PathVariable short pmInterfaceId) {
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

        if (pm instanceof GeneralPm) {
            List<GeneralPmDetail> generalPmDetailList = new ArrayList<>();
            for (PmDetail pmDetail : pmDetailList) {
                generalPmDetailList.add((GeneralPmDetail) pmDetail);
            }
            model.addAttribute("pmDetailList", generalPmDetailList);
        } else if (pm instanceof TemperaturePm) {
            List<TemperaturePmDetail> temperaturePmDetailList = new ArrayList<>();
            for (PmDetail pmDetail : pmDetailList) {
                temperaturePmDetailList.add((TemperaturePmDetail) pmDetail);
            }
            model.addAttribute("pmDetailList", temperaturePmDetailList);
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
        var pmUpdateForm = pmService.getPmUpdateForm(model, pm, pmService.getPmOwnerUsername(pmId));
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
                           @ModelAttribute("assignForm") PmUpdateForm pmUpdateForm) throws IOException {
        if (!file.isEmpty()) {
            pmUpdateForm.setFile(file);
        }
        pmService.updatePm(pmUpdateForm, pmUpdateForm.getPm(), pmService.getPmOwnerUsername(pmUpdateForm.getPm().getId()));
        // pmService.modelForActivePersonTaskList(model);
        return "redirect:activePmList";
    }

    @GetMapping("/workspace")
    private String showWorkspace(Model model) {

        pmService.getActivePmList(model, true, true);

        return "activePmList";
    }

    @GetMapping("/list")
    private String showActivePmList(Model model) {

        pmService.getActivePmList(model, true, false);

        return "activePmList";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        pmService.modelForTaskController(model);
    }

}
