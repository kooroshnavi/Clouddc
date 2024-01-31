package ir.tic.clouddc.task;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping(value = {"/task"})
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/pmList")
    public String pmList(Model model) {
        taskService.modelForMainPage(model);
        return "pmList";
    }

    @GetMapping("/pm")
    public String pmTask(@RequestParam int id, Model model) {
        var pm = taskService.getPm(id);
        model.addAttribute("status", pm);
        if (!pm.getTaskList().isEmpty()) {
            model.addAttribute("taskList", taskService.getTaskListById(id));
        }
        return "taskList";
    }

    @GetMapping("/pm/edit")
    public String pmEditForm(@RequestParam int id, Model model) {
        var status = taskService.getPm(id);
        PmRegisterForm pmEdit = new PmRegisterForm();
        pmEdit.setName(status.getTitle());
        pmEdit.setDescription(status.getDescription());
        pmEdit.setPeriod(status.getPeriod());
        model.addAttribute("pmEdit", pmEdit);
        model.addAttribute("taskSize", status.getTaskList().size());
        model.addAttribute("statusId", status.getId());
        taskService.modelForRegisterTask(model);
        return "pmUpdate";
    }

    //@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISOR', 'MANAGER')")
    @PostMapping("/pm/edit")
    public String pmEdit(Model model,
                         @Valid @ModelAttribute("pmEdit") PmRegisterForm editForm,
                         @RequestParam int id,
                         Errors errors) {

        if (errors.hasErrors()) {
            log.error("Failed to register task due to validation error on input data: " + errors);
            return pmForm(model);
        }
        taskService.modifyPm(editForm, id);
        taskService.modelForMainPage(model);
        return "pmList";
    }

    @GetMapping("/pm/active")
    public String getActivePmList(@RequestParam int id, Model model) {
        List<Task> activeTaskList = taskService.getActiveTaskList(id);
        var pm = taskService.getPm(id);
        model.addAttribute("status", pm);
        model.addAttribute("taskList", activeTaskList);

        return "taskList";
    }

    @GetMapping("/pm/task")
    public String getTaskDetail(@RequestParam Long id, Model model) {

        taskService.modelForTaskDetail(model, id);

        return "taskDetail";
    }
/*
    @GetMapping("/pm/task/edit")
    public String modifyTask(@RequestParam long id, Model model) {
        Optional<TaskDetail> taskDetail = taskService.activeTaskDetail(id, true);
        if (taskDetail.isPresent()) {
            model.addAttribute("modifyForm", new ModifyTaskForm());
            model.addAttribute("name", taskDetail.get().getTask().getTaskStatus().getName());
            model.addAttribute("taskPerson", taskDetail.get().getPerson().getName());
            model.addAttribute("delay", taskDetail.get().getTask().getDelay());
            model.addAttribute("dueDate", taskDetail.get().getTask().getDueDatePersian());
            model.addAttribute("center", taskDetail.get().getTask().getCenter().getNamePersian());
            taskService.modelForRegisterTask(model);
            return "taskModify";
        }
        return "404";
    }*/

    @GetMapping("/update")
    public String updateTask(Model model) {
        taskService.updateTodayTasks();
        taskService.modelForMainPage(model);
        return "index2";
    }

    @GetMapping("/pm/register")
    public String pmForm(Model model) {
        taskService.modelForRegisterTask(model);
        model.addAttribute("pmRegister", new PmRegisterForm());
        return "pmRegister";
    }

    //@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISOR', 'MANAGER')")
    @PostMapping("/pm/register/form/submit")
    public String pmPost(
            Model model,
            @Valid @ModelAttribute("pmRegister") PmRegisterForm pmRegisterForm,
            Errors errors) {

        if (errors.hasErrors()) {
            log.error("Failed to register task due to validation error on input data: " + errors);
            return pmForm(model);
        }

        taskService.taskRegister(pmRegisterForm);
        taskService.modelForMainPage(model);
        return "pmList";
    }


    @GetMapping("/pm/myTask")
    private String getUserTask(Model model) {

        taskService.modelForPersonTaskList(model);

        return "userTask";
    }


    @GetMapping("/task/form")
    public String showAssignForm(@RequestParam("id") Long id,
                                 Model model) {

        taskService.modelForActionForm(model, id);

        return "pmUpdateForm";
    }


    //@PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISOR', 'OPERATOR')")
    @PostMapping("/task/form/update")
    public String assignTaskDetail(Model model,
                                   @RequestParam("id") Long id,
                                   @ModelAttribute("assignForm") AssignForm assignForm) {
        System.out.println("Captured: " + model.getAttribute("assignForm").toString());

        taskService.updateTaskDetail(assignForm, id);
        taskService.modelForPersonTaskList(model);

        return "userTask";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        taskService.modelForTaskController(model);
    }

}
