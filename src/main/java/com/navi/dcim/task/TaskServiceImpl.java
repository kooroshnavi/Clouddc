package com.navi.dcim.task;

import com.github.mfathi91.time.PersianDate;
import com.github.mfathi91.time.PersianDateTime;
import com.navi.dcim.center.Center;
import com.navi.dcim.center.CenterService;
import com.navi.dcim.event.EventService;
import com.navi.dcim.notification.NotificationService;
import com.navi.dcim.person.Person;
import com.navi.dcim.person.PersonService;
import com.navi.dcim.report.DailyReport;
import com.navi.dcim.report.ReportService;
import lombok.extern.slf4j.Slf4j;
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
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

import static com.navi.dcim.utils.UtilService.getCurrentDate;

@Slf4j
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
    private final NotificationService notificationService;
    private static LocalDate CurrentDate;
    private static final int DEFAULT_ASSIGNEE_ID = 5;


    @Autowired
    TaskServiceImpl(TaskStatusRepository taskStatusRepository,
                    TaskRepository taskRepository,
                    TaskDetailRepository taskDetailRepository,
                    CenterService centerService,
                    PersonService personService,
                    ReportService reportService,
                    @Lazy EventService eventService, NotificationService notificationService) {
        this.taskStatusRepository = taskStatusRepository;
        this.taskRepository = taskRepository;
        this.taskDetailRepository = taskDetailRepository;
        this.centerService = centerService;
        this.personService = personService;
        this.reportService = reportService;
        this.eventService = eventService;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "@midnight")
    public void updateTodayTasks() {
        CurrentDate = LocalDate.now();

        final List<Center> defaultCenterList = centerService.getDefaultCenterList();
        final Person defaultPerson = personService.getPerson(DEFAULT_ASSIGNEE_ID);
        List<TaskStatus> todayPmList = taskStatusRepository.findBynextDue(CurrentDate);
        List<Task> activeTaskList = taskRepository.findByActive(true);

        reportService.setTodayReport();

        delayCalculation(activeTaskList);

        if (!todayPmList.isEmpty()) {
            for (TaskStatus status : todayPmList
            ) {
                if (status.getId() == 15) { // specific salon
                    Task todayTask = taskSetup(status, defaultCenterList.get(defaultCenterList.size() - 1));
                    taskDetailSetup(todayTask, defaultPerson);
                    status.setActive(true);
                } else {  // other salons
                    for (int i = 0; i < 2; i++) {
                        Task todayTask = taskSetup(status, defaultCenterList.get(i));
                        taskDetailSetup(todayTask, defaultPerson);
                    }
                    status.setActive(true);
                }
            }
        }

        taskStatusRepository.saveAll(todayPmList);
        notificationService.sendScheduleUpdateMessage("09127016653", "Scheduler successful @: " + LocalDateTime.now());
    }

    private TaskDetail taskDetailSetup(Task todayTask, Person person) {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setPerson(person);
        taskDetail.setAssignedDate(LocalDateTime.now());
        taskDetail.setActive(true);
        taskDetail.setTask(todayTask);
        todayTask.setTaskDetailList(taskDetail);
        return taskDetail;
    }

    private Task taskSetup(TaskStatus status, Center center) {
        Task todayTask = new Task();
        todayTask.setTaskStatus(status);
        status.setTasks(todayTask);
        todayTask.setDelay(0);
        todayTask.setActive(true);
        todayTask.setCenter(center);
        todayTask.setDueDate(status.getNextDue());
        return todayTask;
    }

    private void delayCalculation(List<Task> taskList) {
        if (!taskList.isEmpty()) {
            for (Task task : taskList
            ) {
                var delay = task.getDelay();
                delay += 1;
                task.setDelay(delay);
            }
            taskRepository.saveAll(taskList);
        }
    }

    @Override
    public TaskStatus getStatus(int id) {
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        var status = taskStatusRepository.findById(id).get();
        status.setNextDuePersian(date.format(PersianDate.fromGregorian(status.getNextDue())));
        if (status.getLastSuccessful() != null) {
            status.setLastSuccessfulPersian(date.format(PersianDateTime.fromGregorian(status.getLastSuccessful())));
        }
        return status;
    }

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

        var possibleNextDue = CurrentDate.plusDays(taskStatus.getPeriod());
        if (possibleNextDue.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()).equals("Thu")) {
            taskStatus.setNextDue(LocalDate.now().plusDays(taskStatus.getPeriod() + 2));
        } else if (possibleNextDue.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()).equals("Fri")) {
            taskStatus.setNextDue(LocalDate.now().plusDays(taskStatus.getPeriod() + 1));
        } else {
            taskStatus.setNextDue(possibleNextDue);
        }

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        taskStatus.setLastSuccessful(task.getSuccessDate());
        taskStatus.setLastSuccessfulPersian(dateTime.format(PersianDateTime.fromGregorian(taskStatus.getLastSuccessful())));
        taskStatus.setNextDuePersian(date.format(PersianDate.fromGregorian(taskStatus.getNextDue())));
        taskStatus.setActive(false);// task is inactive til next due
        return taskStatusRepository.saveAndFlush(taskStatus);
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


    private List<TaskDetail> getTaskDetailById(Long taskId) {
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

        notificationService.sendActiveTaskAssignedMessage(person.getAddress().getValue(), thisTask.getTaskStatus().getName(), thisTask.getDelay(), newTaskDetail.getAssignedDate());
        return thisTask.getTaskDetailList();
    }

    private List<Person> getOtherPersonList() {
        return personService.getPersonListNotIn(getCurrentPerson().getId());
    }// returns a list of users that will be shown in the drop-down list of assignForm.

    @Override
    public void updateTaskDetail(AssignForm assignForm, Long id) {
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

    public Task getTask(Long taskDetailId) {
        return taskDetailRepository.findById(taskDetailId).get().getTask();
    }


    @Override
    public void taskRegister(PmRegisterForm pmRegisterForm) {
        var person = personService.getPerson(pmRegisterForm.getPersonId());
        var centers = centerService.getCenterList();
        var newPm = new TaskStatus();
        newPm.setActive(true);
        newPm.setNextDue(LocalDate.now());
        newPm.setName(pmRegisterForm.getName());
        newPm.setPeriod(pmRegisterForm.getPeriod());
        newPm.setDescription(pmRegisterForm.getDescription());

        if (pmRegisterForm.getCenterId() == 0) { // both salons
            for (int i = 0; i < 2; i++) {
                Task newTask = taskSetup(newPm, centers.get(i));
                var newTaskDetail = taskDetailSetup(newTask, person);
                notificationService.sendNewTaskAssignedMessage(newTaskDetail.getPerson().getAddress().getValue(), newPm.getName(), newTaskDetail.getAssignedDate());
            }
        } else { // selected salon
            Task newTask = taskSetup(newPm, centerService.getCenter(pmRegisterForm.getCenterId()));
            var newTaskDetail = taskDetailSetup(newTask, person);
            notificationService.sendNewTaskAssignedMessage(newTaskDetail.getPerson().getAddress().getValue(), newPm.getName(), newTaskDetail.getAssignedDate());
        }

        taskStatusRepository.saveAndFlush(newPm);
    }

    private Person getCurrentPerson() {
        return personService.getPerson(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private boolean checkPermission(String authenticatedName, Optional<TaskDetail> taskDetail) {
        if (!taskDetail.isPresent()) {
            return false;
        }
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
    public List<Task> getActiveTaskList(int statusId) {
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        List<Task> activeTaskList = taskRepository.findByActiveAndTaskStatusId(true, statusId);
        for (Task task : activeTaskList
        ) {
            task.setDueDatePersian(date.format(PersianDate.fromGregorian(task.getDueDate())));
        }
        return activeTaskList;
    }

    @Override
    public void updateStatus(PmRegisterForm editForm, int id) {
        var status = taskStatusRepository.findById(id).get();
        status.setName(editForm.getName());
        status.setPeriod(editForm.getPeriod());
        status.setDescription(editForm.getDescription());
        taskStatusRepository.saveAndFlush(status);
    }


    @Override
    public Model modelForTaskController(Model model) {
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = personService.getPerson(personName);
        model.addAttribute("person", person);
        model.addAttribute("role", authenticated.getAuthorities());
        model.addAttribute("pending", getPersonTask().size());
        model.addAttribute("pendingEvents", eventService.getPendingEventList().size());
        model.addAttribute("date", getCurrentDate());

        return model;
    }

    @Override
    public Model modelForMainPage(Model model) {
        List<TaskStatus> taskStatusList = getTaskStatus();
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
    public Model modelForTaskDetail(Model model, Long taskId) {
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        List<TaskDetail> taskDetailList = getTaskDetailById(taskId);
        var task = taskRepository.findById(taskId).get();
        var active = task.isActive();
        var delay = taskDetailList.get(0).getTask().getDelay();
        var duedate = date.format(PersianDate.fromGregorian(taskDetailList.get(0).getTask().getDueDate()));
        var taskStatusName = taskDetailList.get(0).getTask().getTaskStatus().getName();
        var currentDetailId = taskDetailList.get(0).getId();
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        var permission = checkPermission(personName, taskDetailList.stream().findAny().filter(TaskDetail::isActive));
        model.addAttribute("currentDetailId", currentDetailId);
        model.addAttribute("permission", permission);
        model.addAttribute("taskDetailList", taskDetailList);
        model.addAttribute("name", taskStatusName);
        model.addAttribute("taskId", taskId);
        model.addAttribute("delay", delay);
        model.addAttribute("duedate", duedate);
        model.addAttribute("active", active);
        model.addAttribute("task", task);


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
    public TaskDetail modelForActionForm(Model model, Long taskDetailId) {
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
