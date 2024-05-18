package ir.tic.clouddc.pm;

import com.github.mfathi91.time.PersianDate;
import com.github.mfathi91.time.PersianDateTime;
import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.center.Salon;
import ir.tic.clouddc.event.EventService;
import ir.tic.clouddc.log.LogHistory;
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
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
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
    private final NotificationService notificationService;
    private final PersistenceService persistenceService;
    private static final int DEFAULT_ASSIGNEE_ID = 7;

    @Autowired
    PmServiceImpl(PmRepository pmRepository,
                  TaskRepository taskRepository,
                  TaskDetailRepository taskDetailRepository,
                  PmTypeRepository pmTypeRepository, CenterService centerService,
                  PersonService personService,
                  ReportService reportService,
                  NotificationService notificationService,
                  PersistenceService persistenceService) {
        this.pmRepository = pmRepository;
        this.taskRepository = taskRepository;
        this.taskDetailRepository = taskDetailRepository;
        this.pmTypeRepository = pmTypeRepository;
        this.centerService = centerService;
        this.personService = personService;
        this.reportService = reportService;
        this.notificationService = notificationService;
        this.persistenceService = persistenceService;
    }

    public void updateTodayTasks(DailyReport todayReport) {
        final List<Salon> defaultSalonList = centerService.getDefaultCenterList();
        final Person defaultPerson = personService.getPerson(DEFAULT_ASSIGNEE_ID);
        List<Pm> todayPmList = new ArrayList<>();
        List<Task> activeTaskList = taskRepository.findByActive(true);


        delayCalculation(activeTaskList);

        for (Salon salon : defaultSalonList) {

            for (var entry : salon.getPmDueMap().keySet()) {
                if (salon.getPmDueMap().get(entry) == UtilService.getDATE()) {
                    todayPmList.add(new Pm(entry));
                }
            }

            if (!todayPmList.isEmpty()) {
                for (Pm pm : todayPmList
                ) {
                    pm.setActive(true);
                    Task todayTask = new Task(true, 0, pm, salon);
                    var persistence = persistenceService.setupNewPersistence('3', defaultPerson);
                    TaskDetail taskDetail = new TaskDetail("", todayTask, persistence, true, 0);
                }
                pmRepository.saveAll(todayPmList);
            }
        }

        notificationService.sendScheduleUpdateMessage("09127016653", "Scheduler successful @: " + LocalDateTime.now());
    }


    private void delayCalculation(List<Task> taskList) {
        if (!taskList.isEmpty()) {
            for (Task task : taskList
            ) {
                var delay = task.getDelay();
                delay += 1;
                task.setDelay(delay);

                var activeFlow = task.getTaskDetailList().stream().filter(TaskDetail::isActive).findFirst().get();
                var activeLogHistory = activeFlow.getPersistence().getLogHistoryList().stream().filter(LogHistory::isActive).findFirst().get();
                LocalDateTime assignedDate = LocalDateTime.of(activeLogHistory.getDate(), activeLogHistory.getTime());
                activeFlow.setDelay((int) ChronoUnit.DAYS.between(LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()), assignedDate));
            }
            taskRepository.saveAll(taskList);
        }
    }

    @Override
    public List<Pm> getPmList(int pmTypeId) {
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return pmRepository.fetchRelatedPmList(pmTypeId);
    }

    private Pm endTask(Task task) {
        Pm pm = task.getPm();
        var salon = task.getSalon();
        var nextDue = UtilService.getDATE().plusDays(pm.getPeriod());
        if (nextDue.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()).equals("Thu")) {
            salon.getPmDueMap().put(pm.getId(), nextDue.plusDays(2));
        } else if (nextDue.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()).equals("Fri")) {
            salon.getPmDueMap().put(pm.getId(), nextDue.plusDays(1));
        } else {
            salon.getPmDueMap().put(pm.getId(), nextDue);
        }

        task.setActive(false);
        task.setDailyReport(new DailyReport(reportService.getActiveReportId()));

        if (pm.getTaskList().stream().noneMatch(Task::isActive)) {
            pm.setActive(false);    // pm is inactive til next due
        }

        return pmRepository.saveAndFlush(pm);
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

    private List<TaskDetail> assignNewTaskDetail(Task task, int actionType) {
        Person person = new Person(actionType);
        var persistence = persistenceService.setupNewPersistence(' ', person);
        TaskDetail newTaskDetail = new TaskDetail("", task, persistence, true, 0);
        taskRepository.save(task);

        notificationService.sendActiveTaskAssignedMessage(person.getAddress().getValue(), task.getPm().getName(), task.getDelay(), newTaskDetail.getAssignedDate());
        return task.getTaskDetailList();
    }

    private List<Person> getOtherPersonList() {
        return personService.getPersonListNotIn(personService.getPersonId(personService.getCurrentUsername()));
    } // returns a list of users that will be shown in the drop-down list of assignForm.

    @Override
    public void updateTaskDetail(AssignForm assignForm, Long taskDetailId) {
        TaskDetail taskDetail = taskDetailRepository.findById(taskDetailId).get();
        taskDetail.setDescription(assignForm.getDescription());
        taskDetail.setActive(false);
        persistenceService.updatePersistence(taskDetail.getPersistence(), '3', personService.getPersonId(personService.getCurrentUsername()));

        if (assignForm.getActionType() == 100) {
            endTask(taskDetail.getTask());
        } else {
            assignNewTaskDetail(taskDetail.getTask(), assignForm.getActionType());
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
    public void pmRegister(PmRegisterForm pmRegisterForm) {
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
