package com.navi.dcim.task;

import com.github.mfathi91.time.PersianDate;
import com.github.mfathi91.time.PersianDateTime;
import com.navi.dcim.center.CenterService;
import com.navi.dcim.event.EventService;
import com.navi.dcim.form.AssignForm;
import com.navi.dcim.form.PmRegisterForm;
import com.navi.dcim.model.*;
import com.navi.dcim.person.PersonService;
import com.navi.dcim.report.ReportService;
import com.navi.dcim.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
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

@Service
@EnableScheduling
public final class TaskServiceImpl implements TaskService {

    private final TaskStatusRepository taskStatusRepository;
    private final TaskRepository taskRepository;
    private final TaskDetailRepository taskDetailRepository;
    private final CenterService centerService;
    private final PersonService personService;
    private final ReportService reportService;
    private final EventService eventService;


    @Autowired
    TaskServiceImpl(TaskStatusRepository taskStatusRepository,
                    TaskRepository taskRepository,
                    TaskDetailRepository taskDetailRepository, CenterService centerService, PersonService personService, ReportService reportService, EventService eventService) {
        this.taskStatusRepository = taskStatusRepository;
        this.taskRepository = taskRepository;
        this.taskDetailRepository = taskDetailRepository;
        this.centerService = centerService;
        this.personService = personService;
        this.reportService = reportService;
        this.eventService = eventService;
    }


    @Scheduled(cron = "@midnight")
    public void updateTodayTasks() {
        List<TaskStatus> taskStatusList = taskStatusRepository.findAll();
        List<Center> centers = centerService.getCenterList();
        List<Person> personList = personService.getPersonList();
        DailyReport yesterday = reportService.findActive(true);
        List<Task> taskList = new ArrayList<>();
        for (TaskStatus taskStatus : taskStatusList
        ) {
            if (taskStatus.isActive()) {
                taskList.add(taskStatus.getTasks().stream().filter(Task::isActive).limit(1).findFirst().get());
            }
        }

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

        yesterday.setActive(false);
        DailyReport today = new DailyReport();
        today.setDate(LocalDate.now());
        today.setActive(true);
        List<DailyReport> reports = new ArrayList<>();
        reports.add(yesterday);
        reports.add(today);
        reportService.saveAll(reports);
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
        if (!taskList.isEmpty() && !isWeekend()) {
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

    private boolean isWeekend() {
        var currentDate = LocalDate.now();
        if (Objects.equals(currentDate.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault()), "Thu")
                || Objects.equals(currentDate.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault()), "Fri")) {
            return true;
        }
        return false;
    }

    public List<TaskStatus> getTaskStatus() {
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

    public List<Task> getTaskList() {
        List<Task> tasks = taskRepository.findAll(Sort.by("active").descending());
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        for (Task task : tasks
        ) {
            task.setDueDatePersian(date.format(PersianDate.fromGregorian(task.getDueDate())));
            if (task.getStatus()) {
                task.setSuccessDatePersian(dateTime.format(PersianDateTime.fromGregorian(task.getSuccessDate())));
            }
        }
        return tasks;
    }

    public TaskStatus updateTask(Task task) {
        TaskStatus taskStatus = task.getTaskStatus();
        DailyReport report = reportService.findActive(true);

        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        task.setSuccessDate(LocalDateTime.now());
        task.setSuccessDatePersian(dateTime.format(PersianDateTime.fromGregorian(task.getSuccessDate())));
        task.setActive(false);
        task.setDailyReport(report);
        System.out.println("Task Added" + report.getTaskList());


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

    public List<TaskDetail> getTaskDetailById(int taskId) {
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

    public List<TaskDetail> assignNewTaskDetail(int taskDetailId, int actionType) {
        Task thisTask = taskDetailRepository.findById(taskDetailId).get().getTask();
        Person person = personRepository.findById(actionType).get();
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

    public List<Person> getOtherPersonList(int taskDetailId) {
        var currentPersonId = taskDetailRepository
                .findById(taskDetailId).get()
                .getPerson().getId();
        List<Integer> ids = new ArrayList<>();
        ids.add(currentPersonId);
        return personRepository.findAllByIdNotIn(ids);
    }// returns a list of users that will be shown in the drop-down list of assignForm.

    public void updateTaskDetail(int taskDetailId, AssignForm assignForm) {
        TaskDetail taskDetail = taskDetailRepository.findById(taskDetailId).get();
        switch (assignForm.getActionType()) {
            case 1:     // Ends task. No assign
                taskDetail.setDescription(assignForm.getDescription());
                taskDetail.setActive(false);
                updateTask(taskDetail.getTask());
                break;

            default: // Updates current taskDetail ,creates a new taskDetail and assigns it to specified person
                taskDetail.setDescription(assignForm.getDescription());
                taskDetail.setActive(false);
                assignNewTaskDetail(taskDetailId, assignForm.getActionType());
                break;
        }
    }

    public Task getTask(int taskDetailId) {
        return taskDetailRepository.findById(taskDetailId).get().getTask();
    }


    public List<Task> getUserTask(int personId) {
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        List<TaskDetail> taskDetailList = taskDetailRepository.findAllByPerson_IdAndActive(personId, true);
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

    public Person getPerson(int i) {
        return personRepository.findById(i).get();
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


    public List<Person> getPersonList() {
        return personRepository.findAll();
    }

    public List<Center> getCenterList() {
        return centerRepository.findAll();
    }


    public List<Event> getPendingEventList() {
        return eventRepository.findAllByActive(true);
    }

    public Person getPersonByName(String personName) {
        return personRepository.findByUsername(personName);
    }

    public boolean checkPermission(String authenticatedName, Optional<TaskDetail> taskDetail) {
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
    public Model modelForTaskController(Model model) {
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = personService.getPerson(personName);
        model.addAttribute("person", person);
        model.addAttribute("pending", getPersonTask().size());
        model.addAttribute("pendingEvents", getPendingEventList().size());
        model.addAttribute("date", getCurrentDate());
        return model;
    }

    @Override
    public Model modelForTaskDetail(Model model) {
        return null;
    }

    @Override
    public Model modelForPersonTaskList(Model model) {
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = personService.getPerson(personName);
    }


    /*public List<Task> getUserTask2(int id) {
        List<Task> tasks = taskRepository.findAll(Sort.by("dueDate"));
        List<Task> userTasks = new ArrayList<>();
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter dateTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        for (Task task : tasks
        ) {
            if (task.getPerson().getId() == id) {
                userTasks.add(task);
                task.setDueDatePersian(date.format(PersianDate.fromGregorian(task.getDueDate())));
                if (task.getSuccessDate() != null) {
                    task.setSuccessDatePersian(dateTime.format(PersianDateTime.fromGregorian(task.getSuccessDate())));
                }
            }
        }
        return userTasks;
    }*/
}
