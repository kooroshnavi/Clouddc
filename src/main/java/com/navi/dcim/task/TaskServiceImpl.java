package com.navi.dcim.task;

import com.github.mfathi91.time.PersianDate;
import com.github.mfathi91.time.PersianDateTime;
import com.navi.dcim.center.Center;
import com.navi.dcim.center.CenterService;
import com.navi.dcim.event.EventService;
import com.navi.dcim.person.Person;
import com.navi.dcim.person.PersonService;
import com.navi.dcim.report.DailyReport;
import com.navi.dcim.report.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.navi.dcim.utils.UtilService.getCurrentDate;

@Service
@EnableScheduling
public class TaskServiceImpl implements TaskService {

    private final TaskStatusRepository taskStatusRepository;
    private final TaskRepository taskRepository;
    private final TaskDetailRepository taskDetailRepository;
    private final CenterService centerService;
    private final PersonService personService;
    private final ReportService reportService;
    private final EventService eventService;
    private static LocalDate CurrentDate;


    @Autowired
    TaskServiceImpl(TaskStatusRepository taskStatusRepository,
                    TaskRepository taskRepository,
                    TaskDetailRepository taskDetailRepository,
                    CenterService centerService,
                    PersonService personService,
                    ReportService reportService,
                    @Lazy EventService eventService) {
        this.taskStatusRepository = taskStatusRepository;
        this.taskRepository = taskRepository;
        this.taskDetailRepository = taskDetailRepository;
        this.centerService = centerService;
        this.personService = personService;
        this.reportService = reportService;
        this.eventService = eventService;
    }

    @Scheduled(cron = "@noon")
    public void updateTodayTasks() {

        CurrentDate = LocalDate.now();
        List<TaskStatus> taskStatusList = taskStatusRepository.findBynextDue(CurrentDate);
        List<Task> taskList = taskRepository.findByActive(true);
        List<Center> centers = centerService.getCenterList();
        List<Person> personList = personService.getPersonList();

        reportService.setTodayReport();

        delayCalculation(taskList);

        for (TaskStatus status : taskStatusList
        ) {
            if (isTodayTask(status)) {
                Task todayTask = setupTask(status, centers);
                TaskDetail taskDetail = setupTaskDetail(todayTask, personList);
                todayTask.setTaskDetailList(taskDetail);
                status.setTasks(todayTask);
                status.setActive(true);
            }
        }

        taskStatusRepository.saveAll(taskStatusList);
        System.out.println();
        System.out.println("Scheduler successful @: " + LocalDateTime.now());
        System.out.println();
    }

    private TaskDetail setupTaskDetail(Task todayTask, List<Person> personList) {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setPerson(personList.get(new Random().nextInt(personList.size())));
        taskDetail.setAssignedDate(LocalDateTime.now());
        taskDetail.setActive(true);
        taskDetail.setTask(todayTask);
        return taskDetail;
    }

    private Task setupTask(TaskStatus status, List<Center> centers) {
        Task todayTask = new Task();
        todayTask.setTaskStatus(status);
        todayTask.setDelay(0);
        todayTask.setActive(true);
        todayTask.setCenter(centers.get(new Random().nextInt(centers.size())));
        todayTask.setDueDate(status.getNextDue());
        return todayTask;
    }

    private boolean isTodayTask(TaskStatus status) {
        return status.getNextDue().equals(LocalDate.now());
    }

    private void delayCalculation(List<Task> taskList) {
        if (!taskList.isEmpty()) {
            for (Task task : taskList
            ) {
                if (task.isActive()) {
                    var delay = task.getDelay();
                    delay += 1;
                    task.setDelay(delay);
                }
            }
            taskRepository.saveAll(taskList);
        }
    }
/*
    private boolean isWeekend() {
        var currentDate = LocalDate.now();
        if (Objects.equals(currentDate.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault()), "Thu")
                || Objects.equals(currentDate.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault()), "Fri")) {
            return true;
        }
        return false;
    }*/

    private List<TaskStatus> getTaskStatus() {
        List<TaskStatus> taskStatusList = taskStatusRepository
                .findAll(Sort.by("active").descending());

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        for (TaskStatus taskStatus : taskStatusList
        ) {
            taskStatus.setNextDuePersian(date.format(PersianDate.fromGregorian(taskStatus.getNextDue())));
            if (taskStatus.getLastSuccessful() != null) {
                taskStatus.setLastSuccessfulPersian(dateTime.format(PersianDateTime.fromGregorian(taskStatus.getLastSuccessful())));
            }
        }
        return taskStatusList;
    }

    private TaskStatus updateTask(Task task) {
        TaskStatus taskStatus = task.getTaskStatus();
        Optional<DailyReport> report = reportService.findActive(true);

        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        task.setSuccessDate(LocalDateTime.now());
        task.setSuccessDatePersian(dateTime.format(PersianDateTime.fromGregorian(task.getSuccessDate())));
        task.setActive(false);
        task.setDailyReport(report.get());
        System.out.println("Task Added" + report.get().getTaskList());


        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        taskStatus.setLastSuccessful(task.getSuccessDate());
        taskStatus.setNextDue(taskStatus.getLastSuccessful().toLocalDate().plusDays(taskStatus.getPeriod()));
        taskStatus.setLastSuccessfulPersian(dateTime.format(PersianDateTime.fromGregorian(taskStatus.getLastSuccessful())));
        taskStatus.setNextDuePersian(date.format(PersianDate.fromGregorian(taskStatus.getNextDue())));
        taskStatus.setActive(false);// task is inactive til next due
        return taskStatusRepository.save(taskStatus);
    }


    @Override
    public List<Task> getTaskListById(int taskStatusId) {
        TaskStatus taskStatus = taskStatusRepository.findById(taskStatusId).get();
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        for (Task task : taskStatus.getTasks()
        ) {
            task.setDueDatePersian(date.format(PersianDate.fromGregorian(task.getDueDate())));
            if (!task.isActive()) {
                task.setSuccessDatePersian(dateTime.format(PersianDateTime.fromGregorian(task.getSuccessDate())));
            }
        }
        return taskStatus.getTasks();
    }


    private List<TaskDetail> getTaskDetailById(int taskId) {
        Task task = taskRepository.findById(taskId).get();

        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        for (TaskDetail taskDetail : task.getTaskDetailList()
        ) {
            taskDetail.setPersianDate(dateTime.format
                    (PersianDateTime
                            .fromGregorian
                                    (taskDetail.getAssignedDate())));
        }

        return task.getTaskDetailList()
                .stream()
                .sorted(Comparator.comparing(TaskDetail::getId).reversed())
                .collect(Collectors.toList());
    }

    private List<TaskDetail> assignNewTaskDetail(TaskDetail taskDetail, int actionType) {
        Task thisTask = taskDetail.getTask();
        Person person = personService.getPerson(actionType);
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        TaskDetail newTaskDetail = new TaskDetail();
        newTaskDetail.setAssignedDate(LocalDateTime.now());
        newTaskDetail.setActive(true);
        newTaskDetail.setPersianDate(dateTime.format(PersianDateTime.fromGregorian(newTaskDetail.getAssignedDate())));
        newTaskDetail.setPerson(person);
        newTaskDetail.setTask(thisTask);

        thisTask.getTaskDetailList().add(newTaskDetail);
        taskRepository.save(thisTask);
        return thisTask.getTaskDetailList();
    }

    private List<Person> getOtherPersonList() {
        return personService.getPersonListNotIn(getCurrentPerson().getId());
    }// returns a list of users that will be shown in the drop-down list of assignForm.

    @Override
    public void updateTaskDetail(AssignForm assignForm, int id) {
        TaskDetail taskDetail = taskDetailRepository.findById(id).get();
        switch (assignForm.getActionType()) {
            case 100:     // Ends task. No assign
                taskDetail.setDescription(assignForm.getDescription());
                taskDetail.setActive(false);
                updateTask(taskDetail.getTask());
                break;

            default: // Updates current taskDetail ,creates a new taskDetail and assigns it to specified person
                taskDetail.setDescription(assignForm.getDescription());
                taskDetail.setActive(false);
                assignNewTaskDetail(taskDetail, assignForm.getActionType());
                break;
        }
    }

    public Task getTask(int taskDetailId) {
        return taskDetailRepository.findById(taskDetailId).get().getTask();
    }


    @Override
    public void taskRegister(PmRegisterForm pmRegisterForm) {
        var newTaskStatus = new TaskStatus();
        newTaskStatus.setActive(true);
        newTaskStatus.setNextDue(LocalDate.now());
        newTaskStatus.setName(pmRegisterForm.getName());
        newTaskStatus.setPeriod(pmRegisterForm.getPeriod());

        Center center = centerService.getCenter(pmRegisterForm.getCenterId());

        Task todayTask = new Task();
        todayTask.setTaskStatus(newTaskStatus);
        todayTask.setDelay(0);
        todayTask.setActive(true);
        todayTask.setCenter(center);
        todayTask.setDueDate(newTaskStatus.getNextDue());

        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setTask(todayTask);
        if (pmRegisterForm.getPersonId() == 1) { // Random
            List<Person> personList = personService.getPersonList();
            taskDetail.setPerson(personList.get(new Random().nextInt(personList.size())));
        } else { // assign to specified user
            Person person = personService.getPerson(pmRegisterForm.getPersonId());
            taskDetail.setPerson(person);
        }

        taskDetail.setAssignedDate(LocalDateTime.now());
        taskDetail.setActive(true);

        todayTask.setTaskDetailList(taskDetail);
        newTaskStatus.setTasks(todayTask);
        taskStatusRepository.save(newTaskStatus);
    }

    private Person getCurrentPerson() {
        return personService.getPerson(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private boolean checkPermission(String authenticatedName, Optional<TaskDetail> taskDetail) {
        if (!taskDetail.isPresent()) {
            return false;
        }
        System.out.println(authenticatedName);
        System.out.println(taskDetail.get().getPerson().getUsername());
        if (Objects.equals(authenticatedName, taskDetail.get().getPerson().getUsername())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Task> getPersonTask() {
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        System.out.println(authenticated.getAuthorities().toString());
        System.out.println(personName);
        Person person = personService.getPerson(personName);
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        List<TaskDetail> taskDetailList = taskDetailRepository.findAllByPerson_IdAndActive(person.getId(), true);
        List<Task> userTasks = new ArrayList<>();
        for (TaskDetail taskDetail : taskDetailList

        ) {
            taskDetail.getTask().setDueDatePersian(date.format(PersianDate.fromGregorian(taskDetail.getTask().getDueDate())));
            userTasks.add(taskDetail.getTask());
        }
        return userTasks
                .stream()
                .sorted(Comparator.comparing(Task::getDelay).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Model modelForTaskController(Model model) {
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = personService.getPerson(personName);
        model.addAttribute("person", person);
        model.addAttribute("pending", getPersonTask().size());
        model.addAttribute("pendingEvents", eventService.getPendingEventList().size());
        model.addAttribute("date", getCurrentDate());

        return model;
    }

    @Override
    public Model modelForMainPage(Model model) {
        model.addAttribute("statusList", getTaskStatus());

        return model;
    }

    @Override
    public Model modelForRegisterTask(Model model) {
        model.addAttribute("personList", personService.getPersonList());
        model.addAttribute("centerList", centerService.getCenterList());
        model.addAttribute("pmRegister", new PmRegisterForm());

        return model;
    }

    @Override
    public Model modelForTaskDetail(Model model, int taskId) {
        List<TaskDetail> taskDetailList = getTaskDetailById(taskId);
        var delay = taskDetailList.get(0).getTask().getDelay();
        var duedate = PersianDate.fromGregorian(taskDetailList.get(0).getTask().getDueDate());
        var taskStatusName = taskDetailList.get(0).getTask().getTaskStatus().getName();
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        var permission = checkPermission(personName, taskDetailList.stream().findAny().filter(TaskDetail::isActive));
        model.addAttribute("permission", permission);
        model.addAttribute("taskDetailList", taskDetailList);
        model.addAttribute("name", taskStatusName);
        model.addAttribute("taskId", taskId);
        model.addAttribute("delay", delay);
        model.addAttribute("duedate", duedate);

        return model;
    }

    @Override
    public Model modelForPersonTaskList(Model model) {
        List<Task> userTaskList = getPersonTask();
        if (!userTaskList.isEmpty()) {
            model.addAttribute("userTaskList", userTaskList);
        }
        return model;
    }

    @Override
    @PostAuthorize("returnObject.person.username == authentication.name && returnObject.active")
    public TaskDetail modelForActionForm(Model model, int taskDetailId) {
        taskDetailRepository.findById(taskDetailId);
        List<Person> personList = getOtherPersonList();
        Task thisTask = getTask(taskDetailId);
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        var taskName = thisTask.getTaskStatus().getName();
        var dueDate = date.format(PersianDate.fromGregorian(thisTask.getDueDate()));
        var center = thisTask.getCenter().getNamePersian();
        var delay = thisTask.getDelay();
        String person = "";
        for (TaskDetail taskdetail : thisTask.getTaskDetailList()
        ) {
            if (taskdetail.getId() == taskDetailId) {
                person = taskdetail.getPerson().getName();
                break;
            }
        }
        AssignForm assignForm = new AssignForm();
        assignForm.setId(taskDetailId);
        System.out.println("Assigned: " + assignForm.getId());
        model.addAttribute("id", taskDetailId);
        model.addAttribute("taskDetail", taskDetailRepository.findById(taskDetailId));
        model.addAttribute("taskName", taskName);
        model.addAttribute("dueDate", dueDate);
        model.addAttribute("center", center);
        model.addAttribute("personName", person);
        model.addAttribute("personList", personList);
        model.addAttribute("delay", delay);
        model.addAttribute("assignForm", assignForm);
        return taskDetailRepository.findById(taskDetailId).get();
    }


}
