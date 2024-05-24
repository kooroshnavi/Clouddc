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
@RequestMapping(value = {"/pm"})
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
    public String showPmList(Model model) {
        List<Pm> pmList = pmService.getPmList();
        model.addAttribute("pmList", pmList);
        return "pmListView";
    }

    @GetMapping("/archive/list")
    public String showArchivePmTaskList(@RequestParam int pmId, Model model) {
        List<Task> archiveTaskList = pmService.getPmTaskList(pmId, false);
        var pm = archiveTaskList.get(0).getPm();
        model.addAttribute("pm", pm);
        model.addAttribute("archiveTaskList", archiveTaskList);
        return "archiveTaskListView";
    }

    @GetMapping("/active/list")
    public String showActivePmTaskList(@RequestParam int pmId, Model model) {
        List<Task> activeTaskList = pmService.getPmTaskList(pmId, true);
        var pm = activeTaskList.get(0).getPm();
        model.addAttribute("pm", pm);
        model.addAttribute("activeTaskList", activeTaskList);
        return "pmListView";
    }

    @GetMapping("/edit")   // 1
    public String showEditForm(@RequestParam int pmId, Model model) {
        Pm selectedPm = pmService.getPm(pmId);
        PmRegisterForm pmEdit = new PmRegisterForm();
        pmEdit.setName(selectedPm.getName());
        pmEdit.setDescription(selectedPm.getDescription());
        pmEdit.setPeriod(selectedPm.getPeriod());
        model.addAttribute("pmEdit", pmEdit);
        model.addAttribute("taskSize", selectedPm.getTaskList().size());
        model.addAttribute("pmId", selectedPm.getId());
        pmService.modelForRegisterTask(model);
        return "pmEditView";
    }

    //@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISOR', 'MANAGER')")
    @PostMapping("/edit")
    public String pmEdit(Model model,
                         @Valid @ModelAttribute("pmEdit") PmRegisterForm editForm,
                         @RequestParam int id,
                         @RequestParam("attachment") MultipartFile file,
                         Errors errors) {

        if (errors.hasErrors()) {
            log.error("Failed to register task due to validation error on input data: " + errors);
            return pmForm(model);
        }
        pmService.editPm(editForm, id);
        pmService.PmTypeOverview(model);
        return "pmEditView";
    }

    @GetMapping("/task")
    public String showTaskList(@RequestParam int pmId, Model model) {
        List<Task> taskList = pmService.getTaskList(pmId);
        model.addAttribute("taskList", taskList);
        model.addAttribute("pm", taskList.get(0).getPm());
        return null;
    }

    @GetMapping("/task/detailList")
    public String showTaskDetailPage(@RequestParam Long taskId, Model model) {
        pmService.modelForTaskDetail(model, taskId);

        return "taskDetail";
    }

    @GetMapping("/register")
    public String pmForm(Model model) {
        pmService.modelForRegisterTask(model);
        model.addAttribute("pmRegister", new PmRegisterForm());
        return "pmRegister";
    }

    @PostMapping("/register/form/submit")
    public String pmPost(
            Model model,
            @Valid @ModelAttribute("pmRegister") PmRegisterForm pmRegisterForm,
            Errors errors) throws IOException {

        if (errors.hasErrors()) {
            log.error("Failed to register task due to validation error on input data: " + errors);
            return pmForm(model);
        }

        pmService.pmRegister(pmRegisterForm);
        pmService.PmTypeOverview(model);
        return "pmList";
    }


    @GetMapping("/myTask")
    private String showActivePersonTaskList(Model model) {

        pmService.modelForActivePersonTaskList(model);

        return "activePersonTaskList";
    }


    @GetMapping("/task/form")
    public String showAssignForm(@RequestParam("id") Long id,
                                 Model model) {

        pmService.modelForActionForm(model, id);

        return "pmUpdateForm";
    }


    @PostMapping("/task/form/update")
    public String assignTaskDetail(Model model,
                                   @RequestParam("id") Long id,
                                   @ModelAttribute("assignForm") AssignForm assignForm) throws IOException {
        System.out.println("Captured: " + model.getAttribute("assignForm").toString());

        pmService.updateTaskDetail(assignForm, id);
        pmService.modelForActivePersonTaskList(model);

        return "userTask";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        pmService.modelForTaskController(model);
    }

}
