package com.navi.dcim.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @Bean
    public UserDetailsService users() {
        // The builder will ensure the passwords are encoded before saving in memory
        User.UserBuilder users = User.withDefaultPasswordEncoder();
        UserDetails navi = users
                .username("navi")
                .password("123456")
                .build();
        UserDetails vijeh = users
                .username("vijeh")
                .password("123456")
                .build();
        UserDetails nikoo = users
                .username("nikooei")
                .password("123456")
                .build();
        return new InMemoryUserDetailsManager(navi, vijeh, nikoo);
    }

    @GetMapping("/app/main")
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


    @GetMapping("/app/main/task/detail/{id}/form")
    public String showAssignForm(@PathVariable("id") int id, Model model) {

        taskService.modelForActionForm(model, id);

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
