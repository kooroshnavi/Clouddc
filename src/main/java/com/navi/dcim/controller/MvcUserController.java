package com.navi.dcim.controller;

import com.github.mfathi91.time.PersianDate;
import com.github.mfathi91.time.PersianDateTime;
import com.navi.dcim.model.Center;
import com.navi.dcim.model.Person;
import com.navi.dcim.model.Task;
import com.navi.dcim.model.TaskStatus;
import com.navi.dcim.repository.CenterRepository;
import com.navi.dcim.repository.PersonRepository;
import com.navi.dcim.service.TaskService;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
        var fullDate = year + "  -  " +  month.toString() + "     " +  day + "  -  " + dayName.toString();
        model.addAttribute("statusList", taskStatusList);
        model.addAttribute("date", fullDate);
        return "clock";
    }

    @GetMapping("/app/taskList")
    public String getTaskList(Model model) {
        List<Task> taskLists = taskService.getTaskList();
        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().toString();
        var fullDate = year + "  -  " +  month.toString() + "     " +  day + "  -  " + dayName.toString();
        model.addAttribute("taskList", taskLists);
        model.addAttribute("date", fullDate);
        return "task2";
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
