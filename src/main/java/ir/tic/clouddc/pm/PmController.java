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
    public String showPmList(Model model) {
        List<Pm> pmList = pmService.getPmList();
        model.addAttribute("pmList", pmList);
        return "pmListView";
    }

    @GetMapping("/register/form")
    public String showPmForm(Model model) {
        pmService.getPmFormData(model);
        return "pmRegisterView";
    }

    @GetMapping("/{pmId}/edit/form")
    public String showEditForm(@RequestParam int pmId, Model model) {
        pmService.pmEditFormData(model, pmId);
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

    @GetMapping("/{pmId}/archive/taskList")
    public String showArchivePmTaskList(@RequestParam int pmId, Model model) {
        List<Task> archiveTaskList = pmService.getPmTaskList(pmId, false);
        var pm = archiveTaskList.get(0).getGeneralPm();
        model.addAttribute("pm", pm);
        model.addAttribute("archiveTaskList", archiveTaskList);
        return "archivePmTaskListView";
    }

    @GetMapping("/{pmId}/active/taskList")
    public String showActivePmTaskList(@RequestParam int pmId, Model model) {
        List<Task> activeTaskList = pmService.getPmTaskList(pmId, true);
        var pm = activeTaskList.get(0).getGeneralPm();
        model.addAttribute("pm", pm);
        model.addAttribute("activeTaskList", activeTaskList);
        return "activePmTaskListView";
    }

    @GetMapping("/activeTaskList")
    public String showAllActiveTaskList(Model model) {
        List<Task> activeTaskList = pmService.getAllActiveTaskList();
        model.addAttribute("activeTaskList", activeTaskList);
        model.addAttribute("size", activeTaskList.size());
        return "activeTaskListView";
    }

    @GetMapping("/task/{taskId}/detailList")
    public String showTaskDetailPage(@RequestParam Long taskId, Model model) {
        pmService.getTaskDetailList(model, taskId);
        return "taskDetail";
    }

    @GetMapping("/task/{taskId}/form")
    public String showTaskUpdateForm(@RequestParam("taskId") Long taskId,
                                     Model model) {
        pmService.prepareAssignForm(model, pmService.getTask(taskId), pmService.getOwnerUsername(taskId));

        return "taskUpdateForm";
    }

    @PostMapping("/task/{taskId}/update")
    public String updateTask(Model model,
                             @RequestParam("taskId") Long taskId,
                             @RequestParam("attachment") MultipartFile file,
                             @ModelAttribute("assignForm") AssignForm assignForm) throws IOException {
        if (!file.isEmpty()) {
            assignForm.setFile(file);
        }

        pmService.updateTaskDetail(assignForm, pmService.getTask(taskId), pmService.getOwnerUsername(taskId));
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
