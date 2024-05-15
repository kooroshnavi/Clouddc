package ir.tic.clouddc.pm;

import com.github.mfathi91.time.PersianDate;
import com.github.mfathi91.time.PersianDateTime;
import ir.tic.clouddc.center.Salon;
import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.event.EventService;
import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.log.PersistenceService;
import ir.tic.clouddc.notification.NotificationService;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.report.DailyReport;
import ir.tic.clouddc.report.ReportService;
import ir.tic.clouddc.utils.UtilService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@EnableScheduling
@Transactional
public class PmServiceImpl implements PmService {


    private final PmRepository pmRepository;
    private final TaskRepository taskRepository;
    private final TaskDetailRepository taskDetailRepository;
    private final PmTypeRepository pmTypeRepository;
    private final CenterService centerService;
    private final PersonService personService;
    private final ReportService reportService;
    private final EventService eventService;
    private final NotificationService notificationService;
    private final PersistenceService persistenceService;
    private static LocalDate CurrentDate;
    private static final int DEFAULT_ASSIGNEE_ID = 7;

    @Autowired
    PmServiceImpl(PmRepository pmRepository,
                  TaskRepository taskRepository,
                  TaskDetailRepository taskDetailRepository,
                  PmTypeRepository pmTypeRepository, CenterService centerService,
                  PersonService personService,
                  ReportService reportService,
                  @Lazy EventService eventService, NotificationService notificationService, PersistenceService persistenceService) {
        this.pmRepository = pmRepository;
        this.taskRepository = taskRepository;
        this.taskDetailRepository = taskDetailRepository;
        this.pmTypeRepository = pmTypeRepository;
        this.centerService = centerService;
        this.personService = personService;
        this.reportService = reportService;
        this.eventService = eventService;
        this.notificationService = notificationService;
        this.persistenceService = persistenceService;
    }

    public void updateTodayTasks() {
        CurrentDate = reportService.getTODAY();
        final List<Salon> defaultSalonList = centerService.getDefaultCenterList();
        final Person defaultPerson = personService.getPerson(DEFAULT_ASSIGNEE_ID);
        List<Pm> todayPmList = pmRepository.findBynextDue(CurrentDate);
        List<Task> activeTaskList = taskRepository.findByActive(true);

        var persistence = persistenceService.setupNewPersistence('3', null);

        delayCalculation(activeTaskList);


        if (!todayPmList.isEmpty()) {
            for (Pm pm : todayPmList
            ) {
                if (pm.getId() == 15) { // specific salon
                    pm.setActive(true);
                    var center = defaultSalonList.get(defaultSalonList.size() - 1);
                    Task todayTask = taskSetup(pm, center, dailyReport);
                    taskDetailSetup(todayTask, persistence);

                } else {  // other salons
                    for (int i = 0; i < 2; i++) {
                        pm.setActive(true);
                        Task todayTask = taskSetup(pm, defaultSalonList.get(i), dailyReport);
                        taskDetailSetup(todayTask, persistence);
                    }
                }
            }
            pmRepository.saveAll(todayPmList);
        }

        notificationService.sendScheduleUpdateMessage("09127016653", "Scheduler successful @: " + LocalDateTime.now());
    }



    private TaskDetail taskDetailSetup(Task todayTask, Persistence persistence) {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setTask(todayTask);
        taskDetail.setDescription("انتساب طبق زمان بندی");
        taskDetail.setActive(true);
        taskDetail.setPersistence(persistence);
        return taskDetail;
    }

    private Task taskSetup(Pm pm, Salon salon, DailyReport dailyReport) {
        Task todayTask = new Task();
        todayTask.setDelay(0);
        todayTask.setActive(true);
        todayTask.setSalon(salon);
        todayTask.setDailyReport(dailyReport);
        todayTask.setPm(pm);
        var pmTaskList = pm.getTaskList();
        if (pmTaskList != null) {
            pmTaskList.add(todayTask);
        } else {
            pmTaskList = new ArrayList<>();
            pmTaskList.add(todayTask);
        }

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
    public List<Pm> getPmList(int pmTypeId) {
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return pmRepository.fetchRelatedPmList(pmTypeId);
    }

    private Pm updateTask(Task task) {
        CurrentDate = LocalDate.now();
        Pm PM = task.getPM();
        Optional<DailyReport> report = reportService.findActive(true);

        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        task.setSuccessDate(LocalDateTime.now());
        task.setSuccessDatePersian(dateTime.format(PersianDateTime.fromGregorian(task.getSuccessDate())));
        task.setActive(false);
        task.setDailyReport(report.get());
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        PM.setLastSuccessful(task.getSuccessDate());
        PM.setLastSuccessfulPersian(dateTime.format(PersianDateTime.fromGregorian(PM.getLastSuccessful())));


        if (PM.getTaskList().stream().noneMatch(Task::isActive)) {
            PM.setActive(false);    // task is inactive til next due
            var possibleNextDue = CurrentDate.plusDays(PM.getPeriod());
            if (possibleNextDue.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()).equals("Thu")) {
                PM.setNextDue(LocalDate.now().plusDays(PM.getPeriod() + 2));
            } else if (possibleNextDue.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()).equals("Fri")) {
                PM.setNextDue(LocalDate.now().plusDays(PM.getPeriod() + 1));
            } else {
                PM.setNextDue(possibleNextDue);
            }
            PM.setNextDuePersian(date.format(PersianDate.fromGregorian(PM.getNextDue())));
        }
        return pmRepository.saveAndFlush(PM);
    }


    @Override
    public List<Task> getTaskListByPmId(int pmId) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        List<Task> taskList = taskRepository.findByPmId(pmId);
        for (Task task : taskList) {
            task.setDueDatePersian(dateFormatter.format(PersianDate.fromGregorian(task.getDailyReport().getDate())));
            if (!task.isActive()) {
                task.setSuccessDatePersian(dateTimeFormatter.format(PersianDateTime.fromGregorian(task.getCompleted())));
            }
        }

        return taskList;
    }

    @Override
    public Pm getPm(int pmId) {
        return pmRepository.findById(pmId).get();
    }

    @Override
    public Pm editPm(int pmId) {
        return null;
    }

    private List<TaskDetail> getTaskDetailById(Long taskId) {
        Task task = taskRepository.findById(taskId).get();

        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        for (TaskDetail taskDetail : task.getTaskDetailList()
        ) {
            taskDetail.setPersianRegisterDate(dateTime.format
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
        newTaskDetail.setPersianRegisterDate(dateTime.format(PersianDateTime.fromGregorian(newTaskDetail.getAssignedDate())));
        newTaskDetail.setPerson(person);
        newTaskDetail.setTask(thisTask);
        thisTask.getTaskDetailList().add(newTaskDetail);
        taskRepository.save(thisTask);

        notificationService.sendActiveTaskAssignedMessage(person.getAddress().getValue(), thisTask.getPM().getName(), thisTask.getDelay(), newTaskDetail.getAssignedDate());
        return thisTask.getTaskDetailList();
    }

    private List<Person> getOtherPersonList() {
        return personService.getPersonListNotIn(getCurrentPerson().getId());
    } // returns a list of users that will be shown in the drop-down list of assignForm.

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

    @Override
    public long getFinishedTaskCount() {
        return taskRepository.getTaskCountByActivation(false);
    }

    @Override
    public long getOnTimeTaskCount() {
        return taskRepository.getDelayedActiveTaskCount(0, false);
    }

    @Override
    public long getActiveTaskCount() {
        return taskRepository.getTaskCountByActivation(true);
    }

    @Override
    public int getWeeklyFinishedPercentage() {
        DecimalFormat decimalFormat = new DecimalFormat("##");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        var percent = (float) taskRepository.getWeeklyFinishedTaskCount(reportService.getWeeklyOffsetDate().atStartOfDay(), false) / getFinishedTaskCount() * 100;
        var formatted = decimalFormat.format(percent);
        return Integer.parseInt(formatted);
    }

    @Override
    public int getActiveDelayedPercentage() {
        DecimalFormat decimalFormat = new DecimalFormat("##");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
        var percent = (float) taskRepository.getDelayedActiveTaskCount(0, true) / taskRepository.getTaskCountByActivation(true) * 100;
        log.info(String.valueOf(percent));
        var formatted = decimalFormat.format(percent);
        return Integer.parseInt(formatted);
    }

    public Task getTask(Long taskDetailId) {
        return taskDetailRepository.findById(taskDetailId).get().getTask();
    }

    @Override
    public void taskRegister(PmRegisterForm pmRegisterForm) {
        var person = personService.getPerson(pmRegisterForm.getPersonId());
        var centers = centerService.getCenterList();
        var newPm = new Pm();
        newPm.setActive(true);
        newPm.setNextDue(LocalDate.now());
        newPm.setName(pmRegisterForm.getName());
        newPm.setPeriod(pmRegisterForm.getPeriod());
        newPm.setDescription(pmRegisterForm.getDescription());

        if (pmRegisterForm.getCenterId() == 0) { // both salons
            for (int i = 0; i < 2; i++) {
                Task newTask = taskSetup(newPm, centers.get(i), dailyReport);
                var newTaskDetail = taskDetailSetup(newTask, person);
                notificationService.sendNewTaskAssignedMessage(newTaskDetail.getPerson().getAddress().getValue(), newPm.getName(), newTaskDetail.getAssignedDate());
            }
        } else { // selected salon
            Task newTask = taskSetup(newPm, centerService.getCenter(pmRegisterForm.getCenterId()), dailyReport);
            var newTaskDetail = taskDetailSetup(newTask, person);
            notificationService.sendNewTaskAssignedMessage(newTaskDetail.getPerson().getAddress().getValue(), newPm.getName(), newTaskDetail.getAssignedDate());
        }

        pmRepository.saveAndFlush(newPm);
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
    public void editPm(PmRegisterForm editForm, int id) {
        var status = pmRepository.findById(id).get();
        status.setName(editForm.getName());
        status.setPeriod(editForm.getPeriod());
        status.setDescription(editForm.getDescription());
        pmRepository.saveAndFlush(status);
    }

    @Override
    public Optional<TaskDetail> activeTaskDetail(long taskId, boolean active) {
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        Optional<TaskDetail> activeTaskDetail = taskDetailRepository.findByTaskIdAndActive(taskId, active);
        if (activeTaskDetail.isPresent()) {
            var dueDate = activeTaskDetail.get().getTask().getDueDate();
            activeTaskDetail.get().getTask().setDueDatePersian(date.format(PersianDate.fromGregorian(dueDate)));
            return activeTaskDetail;
        }
        return Optional.empty();
    }


    @Override
    public Model modelForTaskController(Model model) {
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = personService.getPerson(personName);
        model.addAttribute("person", person);
        model.addAttribute("role", authenticated.getAuthorities());
        model.addAttribute("date", UtilService.getCurrentDate());

        return model;
    }

    @Override
    public Model PmTypeOverview(Model model) {
        List<PmType> pmTypeList = pmTypeRepository.findAll();
        model.addAttribute("pmTypeList", pmTypeList);
        return model;
    }

    @Override
    public Model modelForRegisterTask(Model model) {
        model.addAttribute("personList", personService.getPersonList());
        model.addAttribute("centerList", centerService.getCenterList());

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
        var taskStatusName = taskDetailList.get(0).getTask().getPM().getName();
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
        var taskName = thisTask.getPM().getName();
        var description = thisTask.getPM().getDescription();
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
        model.addAttribute("description", description);

        return taskDetailRepository.findById(taskDetailId).get();
    }

}
