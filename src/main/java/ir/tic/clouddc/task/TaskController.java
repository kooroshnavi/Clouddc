package ir.tic.clouddc.task;

import ir.tic.clouddc.person.PersonService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping(value = {"/task"})
public class TaskController {

    private final TaskService taskService;
    private final PersonService personService;

    @Autowired
    public TaskController(TaskServiceImpl taskService, PersonService personService) {
        this.taskService = taskService;
        this.personService = personService;
    }

    @GetMapping("/pmList")
    public String getPmListRequest(Model model) {
        taskService.pmListService(model);
        return "/task/pmList";
    }

    @GetMapping("/list")
    public String getPmTaskListRequest(@RequestParam int id, Model model) {
        taskService.pmTaskListService(model, id);
        return "/task/taskList";
    }

    @GetMapping("/pm/edit")
    public String getPmEditFormRequest(@RequestParam int id, Model model) {
        taskService.pmEditFormService(model, id);
        return "/task/pmModifyForm";
    }

    @PostMapping("/pm/edit")
    public String postPmModifyForm(Model model,
                                   @Valid @ModelAttribute("pmEdit") PmRegisterForm editForm,
                                   @RequestParam int id,
                                   Errors errors) {

        if (errors.hasErrors()) {
            log.error("Failed to register task due to validation error on input data: " + errors);
            return getPmRegisterForm(model);
        }
        taskService.modifyPm(editForm, id);
        taskService.pmListService(model);
        return "/task/pmList";
    }

    @GetMapping("/list/active")
    public String getActivePmList(@RequestParam int id, Model model) {
        var pm = taskService.getPm(id);
        model.addAttribute("status", pm);

        return "/task/taskList";
    }

    @GetMapping("/detail")
    public String getTaskDetailList(@RequestParam Long id, Model model) {

        taskService.taskDetailListService(model, id);

        return "/task/taskDetail";
    }

    @GetMapping("/update")
    public String getTaskActionForm(@RequestParam("id") int pmId,
                                    Model model) {

        Pm pm = taskService.getPm(pmId);
        Optional<Task> activeTask = pm.getTaskList().stream().filter(Task::isActive).findFirst();
        if (activeTask.isPresent()) {
            Optional<TaskDetail> activeDetail = activeTask.get().getTaskDetailList().stream().filter(TaskDetail::isActive).findFirst();
            if (activeDetail.isPresent()) {
                long activeDetailId = activeDetail.get().getId();
                long activeDetailPersonId = activeDetail.get().getPerson().getId();
                long authenticatedPersonId = personService.getAuthenticatedPersonId();
                taskService.taskActionFormService(model, activeDetailId, authenticatedPersonId, activeDetailPersonId);
                return "/task/pmUpdateForm";
            } else {
                return "404";
            }
        } else {
            return "404";
        }
    }

    @PostMapping("/update")
    public String assignTaskDetail(Model model,
                                   @RequestParam("id") Long id,
                                   @ModelAttribute("assignForm") AssignForm assignForm) {
        System.out.println("Captured: " + model.getAttribute("assignForm").toString());

        taskService.updateTaskDetail(assignForm, id);
        taskService.personTaskListService(model);

        return "/task/personTask";
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

    @GetMapping("/pm/register")
    public String getPmRegisterForm(Model model) {
        taskService.pmRegisterFormService(model);
        return "/task/pmRegisterForm";
    }

    @PostMapping("/pm/register")
    public String pmPost(
            Model model,
            @Valid @ModelAttribute("pmRegister") PmRegisterForm pmRegisterForm,
            Errors errors) {

        if (errors.hasErrors()) {
            log.error("Failed to register task due to validation error on input data: " + errors);
            return getPmRegisterForm(model);
        }

        taskService.taskRegister(pmRegisterForm);
        taskService.pmListService(model);
        return "/task/pmList";
    }


    @GetMapping("/workspace")
    private String getPersonTaskList(Model model) {

        taskService.personTaskListService(model);

        return "/task/personTask";
    }


    @ModelAttribute
    public void addAttributes(Model model) {
        taskService.modelForTaskController(model);
    }

}
