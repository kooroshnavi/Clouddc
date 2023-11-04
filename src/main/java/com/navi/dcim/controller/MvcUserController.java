package com.navi.dcim.controller;

import com.github.mfathi91.time.PersianDate;
import com.navi.dcim.model.Task;
import com.navi.dcim.model.TaskDetail;
import com.navi.dcim.model.TaskStatus;
import com.navi.dcim.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;

@Controller
public class MvcUserController {

    @Autowired
    private TaskService taskService;


    @GetMapping("/app/statusList")
    public String getStatusList(Model model) {
        List<TaskStatus> taskStatusList = taskService.getTaskStatus();

        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().toString();
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName.toString();

        model.addAttribute("statusList", taskStatusList);
        model.addAttribute("date", fullDate);
        return "index";
    }

    @GetMapping("/app/taskstatus/{id}")
    public String getTaskList(@PathVariable("id") int id, Model model) {
        List<Task> tasks = taskService.getTaskListById(id);
        var name = tasks.get(0).getNamePersian();
        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().toString();
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName.toString();
        model.addAttribute("taskList", tasks);
        model.addAttribute("name", name);
        model.addAttribute("date", fullDate);
        return "taskList";
    }


    @GetMapping("/app/task/{id}")
    public String getTaskDetail(@PathVariable("id") int id, Model model) {
        List<TaskDetail> taskDetailList = taskService.getTaskDetailById(id);
        var taskStatusName = taskDetailList.get(0).getTask().getTaskStatus().getName();
        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().toString();
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName.toString();
        model.addAttribute("taskDetailList", taskDetailList);
        model.addAttribute("name", taskStatusName);
        model.addAttribute("date", fullDate);
        return "taskDetail";
    }

   /* @GetMapping("/app/registerForm")
    public String registerTaskForm(Model model){

        List<Center> centers = taskService.getCenterList();
        List<Person> personList = taskService.getPersonList();
        TaskStatus taskStatus = new TaskStatus();
        model.addAttribute("task", taskStatus);
        model.addAttribute("personList", personList);
        model.addAttribute("centers", centers);


        return "taskForm";

    }

  /*  @PostMapping("/app/addTask")
    public String addTask(@ModelAttribute("task") TaskStatus taskStatus){

        taskService.addNewTask(taskStatus);

        return "taskForm";

    }*/


}
