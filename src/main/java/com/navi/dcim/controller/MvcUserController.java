package com.navi.dcim.controller;

import com.github.mfathi91.time.PersianDate;
import com.navi.dcim.form.AssignForm;
import com.navi.dcim.form.EventForm;
import com.navi.dcim.form.PmRegisterForm;
import com.navi.dcim.model.Person;
import com.navi.dcim.model.Task;
import com.navi.dcim.model.TaskDetail;
import com.navi.dcim.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
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


    @GetMapping("/app/main")
    public String index(Model model) {
        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().toString();
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName.toString();

        model.addAttribute("date", fullDate);
        model.addAttribute("statusList", taskService.getTaskStatus());
        model.addAttribute("pending", taskService.getUserTask(2).size());
        model.addAttribute("person", taskService.getPerson(2));

        return "home";
    }

    @GetMapping("/app/main/pm/register/form")
    public String pmForm(Model model) {
        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().toString();
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName.toString();
        var pmRegister = new PmRegisterForm();
        model.addAttribute("date", fullDate);
        model.addAttribute("personList", taskService.getPersonList());
        model.addAttribute("centerList", taskService.getCenterList());
        model.addAttribute("pending", taskService.getUserTask(2).size());
        model.addAttribute("person", taskService.getPerson(2));
        model.addAttribute("pmRegister", pmRegister);

        return "pmRegisterForm";
    }

    @PostMapping("/app/main/pm/register/form/submit")
    public String pmPost(
            Model model,
            @ModelAttribute("pmRegister") PmRegisterForm pmRegisterForm) {
        taskService.pmRegister(pmRegisterForm);

        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().toString();
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName.toString();

        model.addAttribute("date", fullDate);
        model.addAttribute("pending", taskService.getUserTask(2).size());
        model.addAttribute("person", taskService.getPerson(2));
        model.addAttribute("statusList", taskService.getTaskStatus());

        return "home";
    }

    @GetMapping("/app/main/event/register/form")
    public String eventForm(Model model) {
        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().toString();
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName.toString();
        var eventRegister = new EventForm();
        model.addAttribute("date", fullDate);
        model.addAttribute("centerList", taskService.getCenterList());
        model.addAttribute("eventTypeList", taskService.getEventType());
        model.addAttribute("pending", taskService.getUserTask(2).size());
        model.addAttribute("person", taskService.getPerson(2));
        model.addAttribute("eventRegister", eventRegister);

        return "eventRegisterForm";
    }

    @PostMapping("/app/main/event/register/form/submit")
    public String eventPost(
            Model model,
            @ModelAttribute("eventRegister") EventForm eventForm) {
        taskService.eventRegister(eventForm);

        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().toString();
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName.toString();

        model.addAttribute("date", fullDate);
        model.addAttribute("pending", taskService.getUserTask(2).size());
        model.addAttribute("person", taskService.getPerson(2));
        model.addAttribute("statusList", taskService.getTaskStatus());

        return "home";
    }


    @GetMapping("/app/main/event/view")
    public String viewEvent(Model model) {
        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().toString();
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName.toString();
        var eventRegister = new EventForm();

        model.addAttribute("date", fullDate);
        model.addAttribute("centerList", taskService.getCenterList());
        model.addAttribute("eventTypeList", taskService.getEventType());
        model.addAttribute("pending", taskService.getUserTask(2).size());
        model.addAttribute("person", taskService.getPerson(2));
        model.addAttribute("eventList",  taskService.getEventList());

        return "eventList";
    }


    @GetMapping("/app/main/mytask")
    private String getUserTask(Model model) {
        List<Task> userTaskList = taskService.getUserTask(2);
        if (!userTaskList.isEmpty()) {
            var name = userTaskList.get(0).getTaskStatus().getName();
            model.addAttribute("name", name);
            model.addAttribute("userTaskList", userTaskList);
        }
        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().toString();
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName.toString();
        model.addAttribute("date", fullDate);
        model.addAttribute("pending", taskService.getUserTask(2).size());
        model.addAttribute("person", taskService.getPerson(2));

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

        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().toString();
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName.toString();

        model.addAttribute("date", fullDate);
        model.addAttribute("pending", taskService.getUserTask(2).size());
        model.addAttribute("person", taskService.getPerson(2));

        return "taskListUi2";
    }


    @GetMapping("/app/main/task/{id}/detail")
    public String getTaskDetail(@PathVariable("id") int id, Model model) {
        List<TaskDetail> taskDetailList = taskService.getTaskDetailById(id);
        var delay = taskDetailList.get(0).getTask().getDelay();
        var duedate = PersianDate.fromGregorian(taskDetailList.get(0).getTask().getDueDate());
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
        model.addAttribute("delay", delay);
        model.addAttribute("duedate", duedate);
        model.addAttribute("pending", taskService.getUserTask(2).size());
        model.addAttribute("person", taskService.getPerson(2));


        return "taskDetailUi2";
    }


    @GetMapping("/app/main/task/detail/{id}/form")
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
        model.addAttribute("pending", taskService.getUserTask(2).size());
        model.addAttribute("person", taskService.getPerson(2));


        return "actionForm";
    }


    @PostMapping("/app/main/task/detail/form/submit/{id}")
    public String assignTaskDetail(@PathVariable("id") int id,
                                   Model model,
                                   @ModelAttribute("assignForm") AssignForm assignForm) {

        taskService.updateTaskDetail(id, assignForm);
        List<Task> userTaskList = taskService.getUserTask(2);
        if (!userTaskList.isEmpty()) {
            var name = userTaskList.get(0).getTaskStatus().getName();
            model.addAttribute("name", name);
            model.addAttribute("userTaskList", userTaskList);
        }
        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().toString();
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName.toString();

        model.addAttribute("date", fullDate);
        model.addAttribute("pending", taskService.getUserTask(2).size());
        model.addAttribute("person", taskService.getPerson(2));

        return "userTaskList";
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
