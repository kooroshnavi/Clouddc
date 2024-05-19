package ir.tic.clouddc.pm;

import com.github.mfathi91.time.PersianDate;
import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.center.Salon;
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
                    Task todayTask = new Task(true, 0, pm, salon, UtilService.getDATE());
                    var persistence = persistenceService.setupNewPersistence(null, null, '3', defaultPerson, true);
                    TaskDetail taskDetail = new TaskDetail("", todayTask, persistence, true, 0, LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()));
                }
                pmRepository.saveAll(todayPmList);
            }
        }

        notificationService.sendScheduleUpdateMessage("09127016653", "Scheduler successful @: " + LocalDateTime.now());
    }

    @Override
    public Task getTask(Long taskId) {
        return null;
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
        task.setTime(UtilService.getTime());
        task.setActive(false);
        task.setDailyReport(new DailyReport(reportService.getActiveReportId()));

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

        if (pm.getTaskList().stream().noneMatch(Task::isActive)) {
            pm.setActive(false);    // pm is inactive til next due
        }


        return pmRepository.saveAndFlush(pm);
    }


    @Override
    public List<Task> getTaskList(int pmId) {
        List<Task> taskList = taskRepository.findByPmId(pmId);
        for (Task task : taskList) {
            task.setPersianDueDate(UtilService.getFormattedPersianDate(task.getDueDate()));
            if (!task.isActive()) {
                task.setPersianFinishedDate(UtilService.getFormattedPersianDateTime(LocalDateTime.of(task.getDailyReport().getDate(), task.getTime())));
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

    private List<TaskDetail> getPreparedTaskDetailList(Long taskId) {
        List<TaskDetail> taskDetailList = taskDetailRepository.findByTaskId(taskId);
        for (TaskDetail taskDetail : taskDetailList
        ) {
            taskDetail.setPersianRegisterDate(UtilService.getFormattedPersianDateTime(taskDetail.getAssignedTime()));
            taskDetail.setPersonName(persistenceService.getAssignedPerson(taskDetail.getPersistence().getId()).getName());
            if (!taskDetail.isActive()) {
                taskDetail.setPersianFinishedDate(UtilService.getFormattedPersianDateTime(taskDetail.getFinishedTime()));
            }
        }

        return taskDetailList
                .stream()
                .sorted(Comparator.comparing(TaskDetail::getId).reversed())
                .collect(Collectors.toList());
    }

    private List<TaskDetail> assignNewTaskDetail(TaskDetail taskDetail, int personId) {
        var persistence = persistenceService.setupNewPersistence(null, null, ' ', new Person(personId), true);
        TaskDetail newTaskDetail = new TaskDetail("", taskDetail.getTask(), persistence, true, 0, LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()));
        taskRepository.save(taskDetail.getTask());

        notificationService.sendActiveTaskAssignedMessage(personService.getPerson(personId).getAddress().getValue(), taskDetail.getTask().getPersianName(), taskDetail.getTask().getDelay(), taskDetail.getAssignedTime());
        return taskDetail.getTask().getTaskDetailList();
    }

    private List<Person> getOtherPersonList() {
        return personService.getPersonListNotIn(personService.getPersonId(personService.getCurrentUsername()));
    } // returns a list of users that will be shown in the drop-down list of assignForm.

    @Override
    public void updateTaskDetail(AssignForm assignForm, Long taskDetailId) {
        TaskDetail taskDetail = taskDetailRepository.findById(taskDetailId).get();
        taskDetail.setFinishedTime(LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()));
        taskDetail.setDescription(assignForm.getDescription());
        taskDetail.setActive(false);
        persistenceService.updatePersistence(
                taskDetail.getFinishedTime().toLocalDate()
                , taskDetail.getFinishedTime().toLocalTime()
                , taskDetail.getPersistence(), ' '
                , false);

        if (assignForm.getActionType() == 100) {
            endTask(taskDetail.getTask());
        } else {
            assignNewTaskDetail(taskDetail, assignForm.getActionType());
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


    @Override
    public List<Task> getActivePersonTaskList() {
        var personId = personService.getPersonId(personService.getCurrentUsername());
        List<Integer> activePersonPersistenceIdList = persistenceService.getActivePersonPersistenceIdList(personId, true);
        List<Task> activePersonTaskList;

        if (!activePersonPersistenceIdList.isEmpty()) {
             activePersonTaskList = taskDetailRepository.fetchRelatedActivePersonTaskList(activePersonPersistenceIdList);
            for (Task task : activePersonTaskList
            ) {
                task.setPersianDueDate(UtilService.getFormattedPersianDate(task.getDueDate()));
                task.setName(task.getPm().getName());
            }
            return activePersonTaskList
                    .stream()
                    .sorted(Comparator.comparing(Task::getDelay).reversed())
                    .collect(Collectors.toList());
        }
        return null;
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
        List<TaskDetail> taskDetailList = getPreparedTaskDetailList(taskId);
        var task = taskDetailList.get(0).getTask();
        task.setPersianDueDate(UtilService.getFormattedPersianDate(task.getDueDate()));
        //var currentDetailId = taskDetailList.get(0).getId();
        // model.addAttribute("currentDetailId", currentDetailId);
        model.addAttribute("taskDetailList", taskDetailList);
        model.addAttribute("task", task);
        model.addAttribute("permission", formPermission(taskDetailList));


        return model;
    }

    private boolean formPermission(List<TaskDetail> taskDetailList) {
        Optional<TaskDetail> activeTaskDetail = taskDetailList.stream().filter(TaskDetail::isActive).findFirst();
        return activeTaskDetail
                .filter
                        (taskDetail ->
                                persistenceService.getAssignedPerson(taskDetail.getPersistence().getId()).getId() == personService.getPersonId(personService.getCurrentUsername()))
                .isPresent();
    }

    @Override
    public Model modelForActivePersonTaskList(Model model) {
        List<Task> activePersonTaskList = getActivePersonTaskList();
        if (!activePersonTaskList.isEmpty()) {
            model.addAttribute("activePersonTaskList", activePersonTaskList);
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
