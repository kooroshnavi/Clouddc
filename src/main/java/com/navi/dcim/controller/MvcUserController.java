package com.navi.dcim.controller;

import com.github.mfathi91.time.PersianDate;
import com.navi.dcim.form.AssignForm;
import com.navi.dcim.model.Person;
import com.navi.dcim.model.Task;
import com.navi.dcim.model.TaskDetail;
import com.navi.dcim.model.TaskStatus;
import com.navi.dcim.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        model.addAttribute("person", 2);
        model.addAttribute("statusList", taskStatusList);
        model.addAttribute("date", fullDate);
        return "statusList";
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
        var taskId = taskDetailList.get(0).getTask().getId();
        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().toString();
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName.toString();
        model.addAttribute("taskDetailList", taskDetailList);
        model.addAttribute("name", taskStatusName);
        model.addAttribute("taskId", taskId);
        model.addAttribute("date", fullDate);
        return "taskDetail";
    }

    @GetMapping("/app/assignForm/{id}")
    public String showAssignForm(@PathVariable("id") int id, Model model) {
        List<Person> personList = taskService.getOtherPersonList(id);
        Task thisTask = taskService.getTask(id);
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        var taskName = thisTask.getTaskStatus().getName();
        var dueDate = date.format(PersianDate.fromGregorian(thisTask.getDueDate()));
        var center = thisTask.getCenter().getNamePersian();
        var delay = thisTask.getDelay();
        String person = "";
        for (TaskDetail taskdetail : thisTask.getTaskDetailList()
        ) {
            if (taskdetail.getId() == id) {
                person = taskdetail.getPerson().getName();
                break;
            }
        }
        AssignForm assignForm = new AssignForm();
        model.addAttribute("id", id);
        model.addAttribute("taskName", taskName);
        model.addAttribute("dueDate", dueDate);
        model.addAttribute("center", center);
        model.addAttribute("personName", person);
        model.addAttribute("personList", personList);
        model.addAttribute("delay", delay);
        model.addAttribute("assignForm", assignForm);

        return "assign";


    }

    @PostMapping("/app/taskdetail/{id}")
    public String assignTaskDetail(@PathVariable("id") int id,
                                   Model model,
                                   @ModelAttribute("assignForm") AssignForm assignForm) {
        taskService.updateTaskDetail(id, assignForm);

        model.addAttribute("statusList",  taskService.getTaskStatus());
        return "statusList";
    }

    @GetMapping("/app/usertasks/{id}")
    private String getUserTask(@PathVariable int id, Model model) {
        model.addAttribute("taskList", taskService.getUserTask(id)) ;
        return "taskList";
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
