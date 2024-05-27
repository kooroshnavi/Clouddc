package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.center.Salon;
import ir.tic.clouddc.document.FileService;
import ir.tic.clouddc.document.MetaData;
import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.notification.NotificationService;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.report.DailyReport;
import ir.tic.clouddc.security.ModifyProtection;
import ir.tic.clouddc.utils.UtilService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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
    private final LogService logService;
    private final FileService fileService;
    private static final int DEFAULT_ASSIGNEE_ID = 7;

    @Autowired
    PmServiceImpl(PmRepository pmRepository,
                  TaskRepository taskRepository,
                  TaskDetailRepository taskDetailRepository,
                  PmTypeRepository pmTypeRepository, CenterService centerService,
                  PersonService personService,
                  NotificationService notificationService,
                  LogService logService, FileService fileService) {
        this.pmRepository = pmRepository;
        this.taskRepository = taskRepository;
        this.taskDetailRepository = taskDetailRepository;
        this.pmTypeRepository = pmTypeRepository;
        this.centerService = centerService;
        this.personService = personService;
        this.notificationService = notificationService;
        this.logService = logService;
        this.fileService = fileService;
    }

    public void updateTodayTasks(DailyReport todayReport) {
        final List<Salon> defaultSalonList = centerService.getSalonList();
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
    public List<Pm> getPmList() {
        return pmRepository.findAll(Sort.by("type"));
    }

    @Override
    public List<Task> getPmTaskList(int pmId, boolean active) {
        List<Task> taskList;
        if (active) {
            taskList = taskRepository.findByPmIdAndActive(pmId, true);
            for (Task task : taskList) {
                task.setPersianDueDate(UtilService.getFormattedPersianDate(task.getDueDate()));
                task.setCurrentAssignedPerson(task
                        .getTaskDetailList().stream()
                        .filter(TaskDetail::isActive)
                        .findFirst().get()
                        .getPersistence()
                        .getPerson().getName());
            }

        } else {
            taskList = taskRepository.findByPmIdAndActive(pmId, false);
            for (Task task : taskList) {
                task.setPersianDueDate(UtilService.getFormattedPersianDate(task.getDueDate()));
                task.setPersianFinishedDate(UtilService.getFormattedPersianDate(task.getDailyReport().getDate()));
            }
        }
        return taskList;
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
    public List<Task> getAllActiveTaskList() {
        List<Task> taskList = taskRepository.findByActive(true);
        for (Task task : taskList) {
            task.setPersianDueDate(UtilService.getFormattedPersianDate(task.getDueDate()));
            task.setName(task.getPm().getName());
            task.setCurrentAssignedPerson(
                    task
                            .getTaskDetailList().stream()
                            .filter(TaskDetail::isActive)
                            .findFirst().get()
                            .getPersistence()
                            .getPerson().getName());
        }
        return taskList;
    }

    @Override
    public Model getTaskDetailList(Model model, Long taskId) {
        List<TaskDetail> taskDetailList = taskDetailRepository.findByTaskId(taskId);
        for (TaskDetail taskDetail : taskDetailList
        ) {
            taskDetail.setPersianRegisterDate(UtilService.getFormattedPersianDateTime(taskDetail.getAssignedTime()));
            taskDetail.setAssignedPerson((taskDetail.getPersistence().getPerson()).getName());
            if (!taskDetail.isActive()) {
                taskDetail.setPersianFinishedDate(UtilService.getFormattedPersianDateTime(taskDetail.getFinishedTime()));
            }
        }
        var orderedTaskDetailList = taskDetailList
                .stream()
                .sorted(Comparator.comparing(TaskDetail::getId).reversed())
                .toList();

        var task = taskDetailList.get(0).getTask();
        List<Long> persistenceIdList = taskDetailRepository.getPersistenceIdList(task.getId());
        List<MetaData> metaDataList = fileService.getRelatedMetadataList(persistenceIdList);
        if (!metaDataList.isEmpty()) {
            model.addAttribute("metaDataList", metaDataList);
        }

        var ownerUsername = taskDetailList.stream().filter(TaskDetail::isActive).findFirst().get().getPersistence().getPerson().getUsername();
        task.setPersianDueDate(UtilService.getFormattedPersianDate(task.getDueDate()));
        model.addAttribute("taskDetailList", orderedTaskDetailList);
        model.addAttribute("task", task);
        model.addAttribute("permission", taskDetailFormViewPermission(ownerUsername));
        model.addAttribute("ownerUsername", ownerUsername);

        return model;
    }

    private boolean taskDetailFormViewPermission(String currentTaskUsername) {
        List<GrantedAuthority> supervisorRoleList = List.of(new SimpleGrantedAuthority("SUPERVISOR"), new SimpleGrantedAuthority("ADMIN"));
        if (personService.getCurrentPersonRoleList().contains((GrantedAuthority) supervisorRoleList)) {
            return true;
        } else return currentTaskUsername.equals(personService.getCurrentUsername());
    }

    private TaskDetail assignNewTaskDetail(TaskDetail taskDetail, int personId, char actionCode, boolean active) {
        taskDetail.setAssignedTime(LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()));
        taskDetail.setDelay(0);
        Persistence persistence = logService.persistenceSetup(new Person(personId));

        if (active) {
            taskDetail.setActive(true);
        } else {
            taskDetail.setActive(false);
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), actionCode, new Person(personId), persistence);
            taskDetail.setFinishedTime(taskDetail.getAssignedTime());
        }

        taskDetail.setPersistence(persistence);
        taskDetailRepository.save(taskDetail);
        notificationService.sendActiveTaskAssignedMessage(personService.getPerson(personId).getAddress().getValue(), taskDetail.getTask().getName(), taskDetail.getTask().getDelay(), taskDetail.getAssignedTime());
        return taskDetail;
    }

    @Override
    @PreAuthorize(" task.active == true  && (ownerUsername == authentication.name || hasAnyAuthority('ADMIN', 'SUPERVISOR')) ")
    public void updateTaskDetail(AssignForm assignForm, Task task, String ownerUsername) throws IOException {
        TaskDetail currentTaskDetail = taskDetailRepository.findByTaskIdAndActive(task.getId(), true).get();
        Persistence currentTaskDetailPersistence = currentTaskDetail.getPersistence();
        var currentUsername = personService.getCurrentUsername();
        currentTaskDetail.setFinishedTime(LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()));
        currentTaskDetail.setActive(false);

        if (ownerUsername.equals(currentUsername)) {
            routineOperation(currentTaskDetail, currentTaskDetailPersistence, assignForm);
        } else {
            supervisorOperation(currentTaskDetail, currentTaskDetailPersistence, assignForm, personService.getCurrentPerson());
        }
        taskDetailRepository.save(currentTaskDetail);

        if (assignForm.getActionType() == 0) {  //  End Task
            endTask(task);
        } else { //  Assign Task
            TaskDetail taskDetail = new TaskDetail();
            taskDetail.setTask(task);
            assignNewTaskDetail(taskDetail, assignForm.getActionType(), '0', true);
        }
    }

    private void routineOperation(TaskDetail currentTaskDetail, Persistence currentTaskDetailPersistence, AssignForm assignForm) throws IOException {
        currentTaskDetail.setDescription(assignForm.getDescription());
        logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), '1', currentTaskDetailPersistence.getPerson(), currentTaskDetailPersistence);
        fileService.checkAttachment(assignForm.getFile(), currentTaskDetailPersistence);
    }

    private void supervisorOperation(TaskDetail currentTaskDetail, Persistence currentTaskDetailPersistence, AssignForm assignForm, Person currentPerson) throws IOException {
        currentTaskDetail.setDescription("Terminated by supervisor");
        logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), '2', currentPerson, currentTaskDetailPersistence);
        TaskDetail supervisorTaskDetail = new TaskDetail(currentTaskDetail.getTask());
        supervisorTaskDetail.setDescription(assignForm.getDescription());
        assignNewTaskDetail(supervisorTaskDetail, currentPerson.getId(), '3', false);
        fileService.checkAttachment(assignForm.getFile(), supervisorTaskDetail.getPersistence());
    }

    @Override
    @PreAuthorize(" task.active == true  && (ownerUsername == authentication.name || hasAnyAuthority('ADMIN', 'SUPERVISOR')) ")
    public Model prepareAssignForm(Model model, Task task, String ownerUsername) {
        var currentUsername = personService.getCurrentUsername();
        AssignForm assignForm = new AssignForm();
        assignForm.setId(task.getId());
        List<Person> assignPersonList;

        if (ownerUsername.equals(currentUsername)) { /// taskDetail Owner updates task
            assignPersonList = personService.getPersonListExcept(List.of(ownerUsername));
        } else {  /// supervisor updates task
            assignPersonList = personService.getPersonListExcept(List.of(ownerUsername, currentUsername));
        }

        model.addAttribute("assignPersonList", assignPersonList);
        model.addAttribute("assignForm", assignForm);
        model.addAttribute("task", task);
        return model;
    }

    @Override
    public String getOwnerUsername(Long taskId) {
        return taskDetailRepository.fetchOwnerUsername(taskId, true);
    }

    @Override
    public Task getTask(long taskId) {
        var task = taskRepository.findById(taskId);
        return task.orElse(null);
    }

    @Override
    public Model getPersonTaskList(Model model) {
        List<Task> activePersonTaskList = taskDetailRepository.fetchActivePersonTaskList(personService.getCurrentUsername(), true);

        if (!activePersonTaskList.isEmpty()) {
            for (Task task : activePersonTaskList
            ) {
                task.setPersianDueDate(UtilService.getFormattedPersianDate(task.getDueDate()));
                task.setName(task.getPm().getName());
            }
            var sortedPersonTaskList = activePersonTaskList
                    .stream()
                    .sorted(Comparator.comparing(Task::getDelay).reversed())
                    .toList();

            model.addAttribute("activePersonTaskList", sortedPersonTaskList);

            return model;
        }
        return null;
    }

    @Override
    @ModifyProtection
    public void pmRegister(PmRegisterForm pmRegisterForm) throws IOException {
        Pm pm;
        Persistence persistence;
        var currentPerson = personService.getCurrentPerson();

        if (pmRegisterForm.getId() > 0) {  ///// Modify Pm
            pm = pmRepository.findById(pmRegisterForm.getId()).get();
            persistence = pm.getPersistence();
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), '7', currentPerson, persistence);
        } else {  //// New Pm
            pm = new Pm();
            pm.setActive(false);
            persistence = logService.persistenceSetup(currentPerson);
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), '8', currentPerson, persistence);
            pm.setPersistence(persistence);
        }

        pm.setName(pmRegisterForm.getName());
        pm.setPeriod(pmRegisterForm.getPeriod());
        pm.setDescription(pmRegisterForm.getDescription());
        pm.setEnabled(pmRegisterForm.isEnabled());
        pm.setType(new PmType(pmRegisterForm.getTypeId()));
        fileService.checkAttachment(pmRegisterForm.getFile(), persistence);

        pmRepository.saveAndFlush(pm);

        for (long id : pmRegisterForm.getLocationIdList()) {
            centerService.getSalon(id).getPmDueMap().put(pm.getId(), pmRegisterForm.getPersianFirstDueDate().toGregorian());
        }
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
    public Model getPmFormData(Model model) {
        model.addAttribute("salonList", centerService.getSalonList());
        model.addAttribute("pmTypeList", pmTypeRepository.findAll(Sort.by("name")));
        model.addAttribute("pmRegister", new PmRegisterForm());
        return model;
    }

    @Override
    public Model pmEditFormData(Model model, int pmId) {
        var pm = pmRepository.findById(pmId);

        if (pm.isPresent()) {
            var selectedPm = pm.get();
            PmRegisterForm pmForm = new PmRegisterForm();
            pmForm.setName(selectedPm.getName());
            pmForm.setDescription(selectedPm.getDescription());
            pmForm.setPeriod(selectedPm.getPeriod());
            pmForm.setId(selectedPm.getId());
            pmForm.setTypeId(selectedPm.getType().getId());

            List<Long> salonIdList = new ArrayList<>();
            for (Salon salon : centerService.getSalonList()) {
                if (salon.getPmDueMap().containsKey(pmId)) {
                    salonIdList.add(salon.getId());
                }
            }
            pmForm.setLocationIdList(salonIdList);

            List<MetaData> metaDataList = fileService.getRelatedMetadataList(List.of(selectedPm.getPersistence().getId()));
            if (!metaDataList.isEmpty()) {
                model.addAttribute("metaDataList", metaDataList);
            }
            model.addAttribute("pmForm", pmForm);
            model.addAttribute("taskSize", selectedPm.getTaskList().size());
            model.addAttribute("pm", selectedPm);
            model.addAttribute("salonList", centerService.getSalonList());
            model.addAttribute("pmTypeList", pmTypeRepository.findAll(Sort.by("name")));

            return model;
        }

        return null;
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

}
