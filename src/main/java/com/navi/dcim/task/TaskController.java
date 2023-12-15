package com.navi.dcim.task;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

@Slf4j
@Controller
@RequestMapping(value = {"", "/", "/app/main"})
public class TaskController {
    private TaskService taskService;

    @Autowired
    public TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @GetMapping(value = {"", "/",})
    public String index(Model model) {
        taskService.modelForMainPage(model);
        return "home";
    }

    @GetMapping("/pm/register/form")
    public String pmForm(Model model) {
        taskService.modelForRegisterTask(model);
        return "pmRegisterForm";
    }

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
        return "home";
    }


    @GetMapping("/mytask")
    private String getUserTask(Model model) {

        taskService.modelForPersonTaskList(model);

        return "userTaskList";
    }

    @GetMapping("/task")
    public String getTaskList(@RequestParam int id, Model model) {
        List<Task> tasks = taskService.getTaskListById(id);
        if (!tasks.isEmpty()) {
            var name = tasks.get(0).getTaskStatus().getName();
            model.addAttribute("name", name);
            model.addAttribute("taskList", tasks);
        }
        return "taskListUi2";
    }


    @GetMapping("/task/{id}/detail")
    public String getTaskDetail(@PathVariable("id") int id, Model model) {

        taskService.modelForTaskDetail(model, id);

        return "taskDetailUi2";
    }


    @GetMapping("/task/detail/{id}/form")
    public String showAssignForm(@PathVariable("id") int id,
                                 Model model) {

        taskService.modelForActionForm(model, id);

        return "actionForm";
    }


    @PostMapping("/task/detail/{id}/form")
    public String assignTaskDetail(Model model, @PathVariable("id") int id,
                                   @ModelAttribute("assignForm") AssignForm assignForm) {
        System.out.println("Captured: " + model.getAttribute("assignForm").toString());

        taskService.updateTaskDetail(assignForm, id);
        taskService.modelForPersonTaskList(model);

        return "userTaskList";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        taskService.modelForTaskController(model);
    }

}
