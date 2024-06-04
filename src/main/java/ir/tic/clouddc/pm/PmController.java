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

    @GetMapping("/type")
    public String showPmTypeList(Model model) {
        pmService.PmTypeOverview(model);
        return "pmTypeView";
    }

    @GetMapping("/list")
    public String showPmInterfaceList(Model model) {
        List<PmInterface> pmInterfaceList = pmService.getPmInterfaceList();
        model.addAttribute("pmInterfaceList", pmInterfaceList);
        return "pmListView";
    }

    @GetMapping("/register/form")    /// General Pm only
    public String showPmForm(Model model) {
        pmService.getPmInterfaceFormData(model);
        return "pmRegisterView";
    }

    @GetMapping("/{pmInterfaceId}/edit/form")
    public String showEditForm(@RequestParam int pmInterfaceId, Model model) {
        pmService.pmInterfaceEditFormData(model, pmInterfaceId);
        return "pmEditView";
    }

    @PostMapping("/register")  /// General Pm only
    public String pmInterfacePost(
            Model model,
            @Valid @ModelAttribute("pmRegister") pmInterfaceRegisterForm pmInterfaceRegisterForm,
            @RequestParam("attachment") MultipartFile file,
            Errors errors) throws IOException {

        if (errors.hasErrors()) {
            log.error("Failed to register task due to validation error on input data: " + errors);
            return showPmForm(model);
        }

        if (!file.isEmpty()) {
            pmInterfaceRegisterForm.setFile(file);
        }

        pmService.pmInterfaceRegister(pmInterfaceRegisterForm);
        return "pmList";
    }

    @GetMapping("/{pmInterfaceId}/active/{active}/{locationId}")
    public String showPmInterfacePmTaskList(@RequestParam short pmInterfaceId,
                                        @RequestParam boolean active,
                                        @RequestParam(required = false) int locationId,
                                        Model model) {

        pmService.getPmInterfacePmListModel(model, pmInterfaceId, active, locationId);
        return "pmInterfacePmList";
    }


    @GetMapping("/{pmId}/detailList")
    public String showPmDetailPage(@RequestParam int pmId, Model model) {
        pmService.getPmDetailList(model, pmId);
        return "pmDetail";
    }

    @GetMapping("/{pmId}/form")
    public String showPmUpdateForm(@RequestParam("taskId") int pmId,
                                     Model model) {
        pmService.getPmUpdateForm(model, pmService.getPm(pmId), pmService.getOwnerUsername(pmId));

        return "pmUpdateForm";
    }

    @PostMapping("/update")
    public String updatePm(Model model,
                             @RequestParam("attachment") MultipartFile file,
                             @ModelAttribute("assignForm") PmUpdateForm pmUpdateForm) throws IOException {
        if (!file.isEmpty()) {
            pmUpdateForm.setFile(file);
        }

        pmService.updatePm(pmUpdateForm, pmService.getPm(pmUpdateForm.getPmId()), pmService.getOwnerUsername(pmUpdateForm.getPmId()));
        // pmService.modelForActivePersonTaskList(model);
        return "redirect:activePersonTaskList";
    }

    @GetMapping("/myTask")
    private String showPersonTaskList(Model model) {

        pmService.getPersonTaskList(model);

        return "activePersonTaskList";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        pmService.modelForTaskController(model);
    }

}
