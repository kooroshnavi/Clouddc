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
        List<PmInterface> pmInterfaceList = pmService.getPmList();
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

    @GetMapping("/{pmInterfaceId}/archive/list")
    public String showArchivePmTaskList(@RequestParam int pmInterfaceId, Model model) {
        pmService.getPmInterfacePmListModel(model, pmInterfaceId, false);
        return "pmInterfacePmList";
    }

    @GetMapping("/{pmInterfaceId}/active/list")
    public String showActivePmTaskList(@RequestParam int pmInterfaceId, Model model) {
        pmService.getPmInterfacePmListModel(model, pmInterfaceId, true);
        return "pmInterfacePmList";
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
