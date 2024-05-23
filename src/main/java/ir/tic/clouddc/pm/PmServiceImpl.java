package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.center.Salon;
import ir.tic.clouddc.document.FileService;
import ir.tic.clouddc.log.LogHistory;
import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.log.PersistenceService;
import ir.tic.clouddc.notification.NotificationService;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.report.DailyReport;
import ir.tic.clouddc.utils.UtilService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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
    private final NotificationService notificationService;
    private final PersistenceService persistenceService;
    private final FileService fileService;
    private static final int DEFAULT_ASSIGNEE_ID = 7;

    @Autowired
    PmServiceImpl(PmRepository pmRepository,
                  TaskRepository taskRepository,
                  TaskDetailRepository taskDetailRepository,
                  PmTypeRepository pmTypeRepository, CenterService centerService,
                  PersonService personService,
                  NotificationService notificationService,
                  PersistenceService persistenceService, FileService fileService) {
        this.pmRepository = pmRepository;
        this.taskRepository = taskRepository;
        this.taskDetailRepository = taskDetailRepository;
        this.pmTypeRepository = pmTypeRepository;
        this.centerService = centerService;
        this.personService = personService;
        this.notificationService = notificationService;
        this.persistenceService = persistenceService;
        this.fileService = fileService;
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
                    assignNewTaskDetail(new TaskDetail(todayTask), defaultPerson.getId(), '0', true);
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

                var activeTaskDetail = task.getTaskDetailList().stream().filter(TaskDetail::isActive).findFirst().get();
                LocalDateTime assignedDate = activeTaskDetail.getAssignedTime();
                activeTaskDetail.setDelay((int) ChronoUnit.DAYS.between(LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()), assignedDate));
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
        task.setDailyReport(new DailyReport(UtilService.getTodayReportId()));

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


    private List<TaskDetail> getPreparedTaskDetailList(Long taskId) {
        List<TaskDetail> taskDetailList = taskDetailRepository.findByTaskId(taskId);
        for (TaskDetail taskDetail : taskDetailList
        ) {
            taskDetail.setPersianRegisterDate(UtilService.getFormattedPersianDateTime(taskDetail.getAssignedTime()));
            taskDetail.setPersonName((taskDetail.getPersistence().getPerson()).getName());
            if (!taskDetail.isActive()) {
                taskDetail.setPersianFinishedDate(UtilService.getFormattedPersianDateTime(taskDetail.getFinishedTime()));
            }
        }

        return taskDetailList
                .stream()
                .sorted(Comparator.comparing(TaskDetail::getId).reversed())
                .collect(Collectors.toList());
    }

    private TaskDetail assignNewTaskDetail(TaskDetail taskDetail, int personId, char actionCode, boolean active) {
        taskDetail.setAssignedTime(LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()));
        taskDetail.setDelay(0);
        Persistence persistence = persistenceService.persistenceSetup(new Person(personId));

        if (active) {
            taskDetail.setActive(true);
        } else {
            taskDetail.setActive(false);
            persistenceService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), actionCode, new Person(personId), persistence);
            taskDetail.setFinishedTime(taskDetail.getAssignedTime());
        }

        taskDetail.setPersistence(persistence);
        taskDetailRepository.save(taskDetail);
        notificationService.sendActiveTaskAssignedMessage(personService.getPerson(personId).getAddress().getValue(), taskDetail.getTask().getName(), taskDetail.getTask().getDelay(), taskDetail.getAssignedTime());
        return taskDetail;
    }

    private List<Person> getOtherPersonList() {
        return personService.getPersonListNotIn(personService.getPersonId(personService.getCurrentUsername()));
    } // returns a list of users that will be shown in the drop-down list of assignForm.

    @Override
    public void updateTaskDetail(AssignForm assignForm, Long taskDetailId) throws IOException {
        TaskDetail currentTaskDetail = taskDetailRepository.findById(taskDetailId).get();
        Persistence currentTaskDetailPersistence = currentTaskDetail.getPersistence();
        Person currentPerson = personService.getCurrentPerson();
        currentTaskDetail.setFinishedTime(LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()));
        currentTaskDetail.setActive(false);

        if (currentTaskDetailPersistence.getPerson().equals(currentPerson)) {
            routineOperation(currentTaskDetail, currentTaskDetailPersistence, assignForm);
        } else {
            supervisorOperation(currentTaskDetail, currentTaskDetailPersistence, assignForm, currentPerson);
        }
        taskDetailRepository.save(currentTaskDetail);

        if (assignForm.getActionType() == 100) {  //  End Task
            endTask(currentTaskDetail.getTask());
        } else { //  Assign Task
            TaskDetail taskDetail = new TaskDetail();
            taskDetail.setTask(currentTaskDetail.getTask());
            assignNewTaskDetail(taskDetail, assignForm.getActionType(), '0', true);
        }
    }

    private void routineOperation(TaskDetail currentTaskDetail, Persistence currentTaskDetailPersistence, AssignForm assignForm) throws IOException {
        currentTaskDetail.setDescription(assignForm.getDescription());
        persistenceService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), '1', currentTaskDetailPersistence.getPerson(), currentTaskDetailPersistence);
        checkAttachment(assignForm.getFile(), currentTaskDetailPersistence);
    }

    private void supervisorOperation(TaskDetail currentTaskDetail, Persistence currentTaskDetailPersistence, AssignForm assignForm, Person currentPerson) throws IOException {
        currentTaskDetail.setDescription("Terminated by supervisor");
        persistenceService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), '2', currentPerson, currentTaskDetailPersistence);
        TaskDetail supervisorTaskDetail = new TaskDetail(currentTaskDetail.getTask());
        supervisorTaskDetail.setDescription(assignForm.getDescription());
        assignNewTaskDetail(supervisorTaskDetail, currentPerson.getId(), '3', false);
        checkAttachment(assignForm.getFile(), supervisorTaskDetail.getPersistence());
    }


    private void checkAttachment(MultipartFile file, Persistence persistence) throws IOException {
        if (!file.isEmpty()) {
            fileService.attachmentRegister(file, persistence);
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
        var percent = (float) taskRepository.getWeeklyFinishedTaskCount(UtilService.getDATE().minusDays(7), false) / getFinishedTaskCount() * 100;
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
        var salonList = centerService.getCenterList();
        var newPm = new Pm();
        newPm.setActive(true);
        newPm.setName(pmRegisterForm.getName());
        newPm.setPeriod(pmRegisterForm.getPeriod());
        newPm.setDescription(pmRegisterForm.getDescription());
        newPm.setEnabled(true);
        newPm.setType(new PmType(pmRegisterForm.getTypeId()));

        if (pmRegisterForm.getCenterId() == 0) { // both salons
            for (int i = 0; i < 2; i++) {
                Task newTask = new Task(true, 0, newPm, salonList.get(0), UtilService.getDATE());
                var persistence = persistenceService.persistenceSetup(null, null, '3', new Person(pmRegisterForm.getPersonId()), true);
                TaskDetail taskDetail = new TaskDetail("", newTask, persistence, true, 0, LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()));
                notificationService.sendNewTaskAssignedMessage(personService.getPerson(pmRegisterForm.getPersonId()).getAddress().getValue(), newPm.getName(), taskDetail.getAssignedTime());
            }
        } else { // selected salon
            Task newTask = new Task(true, 0, newPm, salonList.get(pmRegisterForm.getCenterId()), UtilService.getDATE());
            var persistence = persistenceService.persistenceSetup(null, null, '3', new Person(pmRegisterForm.getPersonId()), true);
            TaskDetail taskDetail = new TaskDetail("", newTask, persistence, true, 0, LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()));
            notificationService.sendNewTaskAssignedMessage(personService.getPerson(pmRegisterForm.getPersonId()).getAddress().getValue(), newPm.getName(), taskDetail.getAssignedTime());
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
                                (taskDetail.getPersistence().getPerson()).getId() == personService.getPersonId(personService.getCurrentUsername()))
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
        var activeTaskDetail = taskDetailRepository.findById(taskDetailId);
        if (activeTaskDetail.isPresent()) {
            var persistence = activeTaskDetail.get().getPersistence();
        }

/*
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
*/
        return taskDetailRepository.findById(taskDetailId).get();
    }

}
