package com.navi.dcim.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TaskController {
    private TaskService taskService;

    @Autowired
    public TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @RequestMapping(value = {"/app/main", "", "/"}, method = {RequestMethod.GET})
    public String index(Model model) {
        taskService.modelForMainPage(model);
        return "home";
    }


    @GetMapping("/app/main/pm/register/form")
    public String pmForm(Model model) {
        taskService.modelForRegisterTask(model);
        return "pmRegisterForm";
    }

    @PostMapping("/app/main/pm/register/form/submit")
    public String pmPost(
            Model model,
            @ModelAttribute("pmRegister") PmRegisterForm pmRegisterForm) {
        taskService.taskRegister(pmRegisterForm);
        taskService.modelForMainPage(model);
        return "home";
    }


    @GetMapping("/app/main/mytask")
    private String getUserTask(Model model) {

        taskService.modelForPersonTaskList(model);

        return "userTaskList";
    }

    @GetMapping("/app/main/task/{id}")
    public String getTaskList(@PathVariable("id") int id, Model model) {
        List<Task> tasks = taskService.getTaskListById(id);
        if (!tasks.isEmpty()) {
            var name = tasks.get(0).getTaskStatus().getName();
            model.addAttribute("name", name);
            model.addAttribute("taskList", tasks);
        }
        return "taskListUi2";
    }


    @GetMapping("/app/main/task/{id}/detail")
    public String getTaskDetail(@PathVariable("id") int id, Model model) {

        taskService.modelForTaskDetail(model, id);

        return "taskDetailUi2";
    }


    @GetMapping("/app/main/task/detail/{username}/{id}/form")
    @PreAuthorize("#username == authentication.name")
    public String showAssignForm(@PathVariable("id") int id,
                                 @PathVariable("username") String username, Model model) {

        taskService.modelForActionForm(model, id, username);

        return "actionForm";
    }


    @PostMapping("/app/main/task/detail/form/submit/{id}")
    public String assignTaskDetail(@PathVariable("id") int id,
                                   Model model,
                                   @ModelAttribute("assignForm") AssignForm assignForm) {

        taskService.updateTaskDetail(id, assignForm);
        taskService.modelForPersonTaskList(model);

        return "userTaskList";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        taskService.modelForTaskController(model);
    }


}
