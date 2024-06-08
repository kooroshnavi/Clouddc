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
    public String showPmInterfacePmTaskList(@RequestParam short pmInterfaceId,
                                            @RequestParam boolean active,
                                            @RequestParam(required = false) int locationId,
                                            Model model) {

        pmService.getPmInterfacePmListModel(model, pmInterfaceId, active, locationId);
        return "pmInterfacePmList";
    }


    @GetMapping("/{pmId}/detailList")
    public String showPmDetailPage(@RequestParam("pmId") int pmId, Model model) {
        pmService.getPmDetailList(model, pmId);
        return "pmDetail";
    }

    @GetMapping("/{pmId}/form")
    public String showPmUpdateForm(@RequestParam("pmId") int pmId,
                                   Model model) {
        pmService.getPmUpdateForm(model, pmService.getPm(pmId), pmService.getPmOwnerUsername(pmId));

        return "pmUpdateForm";
    }

    @PostMapping("/update")
    public String updatePm(Model model,
                           @RequestParam("attachment") MultipartFile file,
                           @ModelAttribute("assignForm") PmUpdateForm pmUpdateForm) throws IOException {
        if (!file.isEmpty()) {
            pmUpdateForm.setFile(file);
        }

        pmService.updatePmDetail(pmUpdateForm, pmService.getPm(pmUpdateForm.getPmId()), pmService.getPmOwnerUsername(pmUpdateForm.getPmId()));
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
