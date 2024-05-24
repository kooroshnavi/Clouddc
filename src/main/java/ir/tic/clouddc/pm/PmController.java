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

    @GetMapping("/register")
    public String showPmForm(Model model) {
        pmService.getPmFormData(model);
        return "pmRegister";
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
        pmService.getPmFormData(model);
        return "pmEditView";
    }

    @PostMapping("/register")
    public String pmPost(
            Model model,
            @Valid @ModelAttribute("pmRegister") PmRegisterForm pmRegisterForm,
            @RequestParam("attachment") MultipartFile file,
            Errors errors) throws IOException {

        if (errors.hasErrors()) {
            log.error("Failed to register task due to validation error on input data: " + errors);
            return showPmForm(model);
        }

        if (!file.isEmpty()) {
            pmRegisterForm.setFile(file);
        }
        pmService.pmRegister(pmRegisterForm);
        return "pmList";
    }


    @GetMapping("/taskList/archive")
    public String showArchivePmTaskList(@RequestParam int pmId, Model model) {
        List<Task> archiveTaskList = pmService.getPmTaskList(pmId, false);
        var pm = archiveTaskList.get(0).getPm();
        model.addAttribute("pm", pm);
        model.addAttribute("archiveTaskList", archiveTaskList);
        return "archivePmTaskListView";
    }

    @GetMapping("/taskList/active")
    public String showActivePmTaskList(@RequestParam int pmId, Model model) {
        List<Task> activeTaskList = pmService.getPmTaskList(pmId, true);
        var pm = activeTaskList.get(0).getPm();
        model.addAttribute("pm", pm);
        model.addAttribute("activeTaskList", activeTaskList);
        return "activePmTaskListView";
    }

    @GetMapping("/activeTaskList")
    public String showAllActiveTaskList(Model model) {
        List<Task> activeTaskList = pmService.getAllActiveTaskList();
        model.addAttribute("activeTaskList", activeTaskList);
        model.addAttribute("size", activeTaskList.size());
        return "activePmTaskListView";
    }

    @GetMapping("/task/detailList")
    public String showTaskDetailPage(@RequestParam Long taskId, Model model) {
        pmService.getTaskDetailList(model, taskId);
        return "taskDetail";
    }

    @GetMapping("/task/{taskId}/update")
    public String showAssignForm(@RequestParam("taskId") Long taskId,
                                 Model model) {
        pmService.prepareAssignForm(model, pmService.getTask(taskId), pmService.getOwnerUsername(taskId));

        return "pmUpdateForm";
    }

    @PostMapping("/task/update")
    public String updateTask(Model model,
                                   @RequestParam("taskId") Long taskId,
                                   @RequestParam("attachment") MultipartFile file,
                                   @ModelAttribute("assignForm") AssignForm assignForm) throws IOException {
        System.out.println("Captured: " + model.getAttribute("assignForm").toString());

        if (!file.isEmpty()) {
            assignForm.setFile(file);
        }
        pmService.updateTaskDetail(assignForm, pmService.getTask(taskId), pmService.getOwnerUsername(taskId));
       // pmService.modelForActivePersonTaskList(model);

        return "userTask";
    }

    @PostMapping("/edit")
    public String pmTypeEdit(Model model,
                         @Valid @ModelAttribute("pmEdit") PmRegisterForm editForm,
                         @RequestParam int id,
                         @RequestParam("attachment") MultipartFile file,
                         Errors errors) {

        if (errors.hasErrors()) {
            log.error("Failed to register task due to validation error on input data: " + errors);
            return showPmForm(model);
        }
        pmService.editPm(editForm, id);
        pmService.PmTypeOverview(model);
        return "pmEditView";
    }

    @GetMapping("/myTask")
    private String showActivePersonTaskList(Model model) {

        pmService.modelForActivePersonTaskList(model);

        return "activePersonTaskList";
    }


    @ModelAttribute
    public void addAttributes(Model model) {
        pmService.modelForTaskController(model);
    }

}
