package com.navi.dcim.controller;

import com.github.mfathi91.time.PersianDate;
import com.navi.dcim.form.AssignForm;
import com.navi.dcim.form.EventForm;
import com.navi.dcim.form.PmRegisterForm;
import com.navi.dcim.model.Event;
import com.navi.dcim.model.Person;
import com.navi.dcim.model.Task;
import com.navi.dcim.model.TaskDetail;
import com.navi.dcim.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Controller
public class MvcUserController {

    @Autowired
    private TaskService taskService;


    @GetMapping("/login")
    public String login(Model model) {

        return "login";
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
        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName;
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = taskService.getPersonByName(personName);
        model.addAttribute("pending", taskService.getUserTask(person.getId()).size());
        model.addAttribute("pendingEvents", taskService.getPendingEventList(person.getId()).size());
        model.addAttribute("person", taskService.getPerson(person.getId()));
        model.addAttribute("date", fullDate);
        model.addAttribute("statusList", taskService.getTaskStatus());

        return "home";
    }

    @GetMapping("/app/main/pm/register/form")
    public String pmForm(Model model) {
        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName;
        var pmRegister = new PmRegisterForm();
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = taskService.getPersonByName(personName);
        model.addAttribute("pending", taskService.getUserTask(person.getId()).size());
        model.addAttribute("pendingEvents", taskService.getPendingEventList(person.getId()).size());
        model.addAttribute("person", taskService.getPerson(person.getId()));
        model.addAttribute("date", fullDate);
        model.addAttribute("personList", taskService.getPersonList());
        model.addAttribute("centerList", taskService.getCenterList());
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
        var dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName;
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = taskService.getPersonByName(personName);
        model.addAttribute("pending", taskService.getUserTask(person.getId()).size());
        model.addAttribute("pendingEvents", taskService.getPendingEventList(person.getId()).size());
        model.addAttribute("person", taskService.getPerson(person.getId()));

        model.addAttribute("date", fullDate);
        model.addAttribute("statusList", taskService.getTaskStatus());

        return "home";
    }

    @GetMapping("/app/main/event/register/form")
    public String eventForm(Model model) {
        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName;
        var eventRegister = new EventForm();
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = taskService.getPersonByName(personName);
        model.addAttribute("pending", taskService.getUserTask(person.getId()).size());
        model.addAttribute("pendingEvents", taskService.getPendingEventList(person.getId()).size());
        model.addAttribute("person", taskService.getPerson(person.getId()));
        model.addAttribute("date", fullDate);
        model.addAttribute("centerList", taskService.getCenterList());
        model.addAttribute("eventTypeList", taskService.getEventType());
        model.addAttribute("eventRegister", eventRegister);

        return "eventRegisterForm";
    }

    @PostMapping("/app/main/event/register/form/submit")
    public String eventPost(
            Model model,
            @ModelAttribute("eventRegister") EventForm eventForm) {


        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName;
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = taskService.getPersonByName(personName);
        taskService.eventRegister(eventForm, person);
        model.addAttribute("pending", taskService.getUserTask(person.getId()).size());
        model.addAttribute("pendingEvents", taskService.getPendingEventList(person.getId()).size());
        model.addAttribute("person", taskService.getPerson(person.getId()));

        model.addAttribute("date", fullDate);
        model.addAttribute("statusList", taskService.getTaskStatus());
        model.addAttribute("eventList", taskService.getEventList());


        return "eventList";
    }


    @GetMapping("/app/main/event/view")
    public String viewEvent(Model model) {
        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName;

        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = taskService.getPersonByName(personName);
        model.addAttribute("pending", taskService.getUserTask(person.getId()).size());
        model.addAttribute("pendingEvents", taskService.getPendingEventList(person.getId()).size());
        model.addAttribute("person", taskService.getPerson(person.getId()));

        model.addAttribute("date", fullDate);
        model.addAttribute("centerList", taskService.getCenterList());
        model.addAttribute("eventTypeList", taskService.getEventType());
        model.addAttribute("eventList", taskService.getEventList());

        return "eventList";
    }

    @GetMapping("/app/main/event/{id}")
    public String viewEvent(Model model, @PathVariable int id) {
        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName.toString();
        var eventRegister = new EventForm();

        Event event = taskService.getEvent(id);
        EventForm eventForm = new EventForm();

        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = taskService.getPersonByName(personName);
        model.addAttribute("pending", taskService.getUserTask(person.getId()).size());
        model.addAttribute("pendingEvents", taskService.getPendingEventList(person.getId()).size());
        model.addAttribute("person", taskService.getPerson(person.getId()));

        model.addAttribute("event", event);
        model.addAttribute("id", id);
        model.addAttribute("eventForm", eventForm);
        model.addAttribute("date", fullDate);
        model.addAttribute("centerList", taskService.getCenterList());
        model.addAttribute("eventTypeList", taskService.getEventType());
        model.addAttribute("eventList", taskService.getEventList());

        return "eventUpdate";
    }

    @PostMapping("/app/main/event/form/update/{id}")
    public String updateEvent(Model model
            , @PathVariable int id
            , @ModelAttribute("eventForm") EventForm eventForm) {
        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName;
        var eventRegister = new EventForm();

        taskService.updateEvent(id, eventForm);

        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = taskService.getPersonByName(personName);
        model.addAttribute("pending", taskService.getUserTask(person.getId()).size());
        model.addAttribute("pendingEvents", taskService.getPendingEventList(person.getId()).size());
        model.addAttribute("person", taskService.getPerson(person.getId()));

        model.addAttribute("eventForm", eventForm);
        model.addAttribute("date", fullDate);
        model.addAttribute("centerList", taskService.getCenterList());
        model.addAttribute("eventTypeList", taskService.getEventType());
        model.addAttribute("eventList", taskService.getEventList());

        return "eventList";
    }


    @GetMapping("/app/main/mytask")
    private String getUserTask(Model model) {
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = taskService.getPersonByName(personName);
        List<Task> userTaskList = taskService.getUserTask(person.getId());
        if (!userTaskList.isEmpty()) {
            var name = userTaskList.get(0).getTaskStatus().getName();
            model.addAttribute("name", name);
            model.addAttribute("userTaskList", userTaskList);
        }
        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName;

        model.addAttribute("pending", taskService.getUserTask(person.getId()).size());
        model.addAttribute("pendingEvents", taskService.getPendingEventList(person.getId()).size());
        model.addAttribute("person", taskService.getPerson(person.getId()));
        model.addAttribute("date", fullDate);

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
        var dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName;

        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = taskService.getPersonByName(personName);
        model.addAttribute("pending", taskService.getUserTask(person.getId()).size());
        model.addAttribute("pendingEvents", taskService.getPendingEventList(person.getId()).size());
        model.addAttribute("person", taskService.getPerson(person.getId()));

        model.addAttribute("date", fullDate);

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
        var dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName;

        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = taskService.getPersonByName(personName);
        var permission = taskService.checkPermission(personName, taskDetailList.stream().findAny().filter(taskDetail -> taskDetail.isActive()));
        System.out.println(permission);
                model.addAttribute("permission", permission);
        model.addAttribute("pending", taskService.getUserTask(person.getId()).size());
        model.addAttribute("pendingEvents", taskService.getPendingEventList(person.getId()).size());
        model.addAttribute("person", taskService.getPerson(person.getId()));
        model.addAttribute("taskDetailList", taskDetailList);
        model.addAttribute("name", taskStatusName);
        model.addAttribute("taskId", taskId);
        model.addAttribute("date", fullDate);
        model.addAttribute("delay", delay);
        model.addAttribute("duedate", duedate);


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

        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person2 = taskService.getPersonByName(personName);
        model.addAttribute("pending", taskService.getUserTask(person2.getId()).size());
        model.addAttribute("pendingEvents", taskService.getPendingEventList(person2.getId()).size());
        model.addAttribute("person", taskService.getPerson(person2.getId()));
        AssignForm assignForm = new AssignForm();
        model.addAttribute("id", id);
        model.addAttribute("taskName", taskName);
        model.addAttribute("dueDate", dueDate);
        model.addAttribute("center", center);
        model.addAttribute("personName", person);
        model.addAttribute("personList", personList);
        model.addAttribute("delay", delay);
        model.addAttribute("assignForm", assignForm);

        return "actionForm";
    }


    @PostMapping("/app/main/task/detail/form/submit/{id}")
    public String assignTaskDetail(@PathVariable("id") int id,
                                   Model model,
                                   @ModelAttribute("assignForm") AssignForm assignForm) {
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person2 = taskService.getPersonByName(personName);

        taskService.updateTaskDetail(id, assignForm);
        List<Task> userTaskList = taskService.getUserTask(person2.getId());
        if (!userTaskList.isEmpty()) {
            var name = userTaskList.get(0).getTaskStatus().getName();
            model.addAttribute("name", name);
            model.addAttribute("userTaskList", userTaskList);
        }

        model.addAttribute("pending", taskService.getUserTask(person2.getId()).size());
        model.addAttribute("pendingEvents", taskService.getPendingEventList(person2.getId()).size());
        model.addAttribute("person", taskService.getPerson(person2.getId()));
        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
        var fullDate = year + "  -  " + month.toString() + "     " + day + "  -  " + dayName;

        model.addAttribute("date", fullDate);

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
