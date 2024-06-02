package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.center.Salon;
import ir.tic.clouddc.document.FileService;
import ir.tic.clouddc.document.MetaData;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.log.Workflow;
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
import java.util.*;

@Slf4j
@Service
@EnableScheduling
@Transactional
public class PmServiceImpl implements PmService {


    private final PmRepository pmRepository;
    private final PmInterfaceRepository pmInterfaceRepository;
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
                  PmInterfaceRepository pmInterfaceRepository, TaskDetailRepository taskDetailRepository,
                  PmTypeRepository pmTypeRepository, CenterService centerService,
                  PersonService personService,
                  NotificationService notificationService,
                  LogService logService, FileService fileService) {
        this.pmRepository = pmRepository;
        this.pmInterfaceRepository = pmInterfaceRepository;
        this.taskDetailRepository = taskDetailRepository;
        this.pmTypeRepository = pmTypeRepository;
        this.centerService = centerService;
        this.personService = personService;
        this.notificationService = notificationService;
        this.logService = logService;
        this.fileService = fileService;
    }

    public void updateTodayTasks(DailyReport todayReport) {
        final List<Location> locationList = centerService.getLocationList();
        final Person defaultPerson = personService.getPerson(DEFAULT_ASSIGNEE_ID);
        List<Pm> activePmList = pmRepository.findAllByActive(true);
        List<Workflow> workflowList = new ArrayList<>();

        delayCalculation(activePmList, workflowList);

        for (Location location : locationList) {
            Set<Integer> pmInterfaceIdSet = location.getPmDueMap().keySet();
            if (!pmInterfaceIdSet.isEmpty()) {
                for (Integer pmInterfaceId : pmInterfaceIdSet) {
                    if (location.getPmDueMap().get(pmInterfaceId) == UtilService.getDATE()) {
                        PmInterface pmInterface = pmInterfaceRepository.findById(pmInterfaceId).get();
                        pmSetup(location, pmInterface, workflowList, defaultPerson);
                    }
                }
            }
        }

        logService.saveWorkFlow(workflowList);
        notificationService.sendScheduleUpdateMessage("09127016653", "Scheduler successful @: " + LocalDateTime.now());
    }


    private void delayCalculation(List<Pm> currentPmList, List<Workflow> workflowList) {
        if (!currentPmList.isEmpty()) {
            for (Pm pm : currentPmList
            ) {
                var delay = pm.getDelay();
                delay += 1;
                pm.setDelay(delay);
                var activeTaskDetail = pm.getPmDetailList().stream().filter(PmDetail::isActive).findFirst().get();
                LocalDateTime assignedDateTime = LocalDateTime.of(activeTaskDetail.getDate(), activeTaskDetail.getTime());
                activeTaskDetail.setDelay((int) ChronoUnit.DAYS.between(LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()), assignedDateTime));
                workflowList.add(activeTaskDetail);
            }
        }
    }

    private void pmSetup(Location location, PmInterface pmInterface, List<Workflow> workflowList, Person defaultPerson) {
        if (pmInterface.isGeneral()) {
            GeneralPm todayGeneralPm = new GeneralPm();
            todayGeneralPm.setActive(true);
            todayGeneralPm.setDelay(0);
            todayGeneralPm.setDueDate(location.getPmDueMap().get(pmInterface.getId()));
            todayGeneralPm.setPmInterface(pmInterface);
            todayGeneralPm.setLocation(location);

            pmDetailSetup(todayGeneralPm, workflowList, pmInterface.isGeneral(), defaultPerson);

        } else {
            TemperaturePm todayTemperaturePm = new TemperaturePm();
            todayTemperaturePm.setActive(true);
            todayTemperaturePm.setDelay(0);
            todayTemperaturePm.setDueDate(location.getPmDueMap().get(pmInterface.getId()));
            todayTemperaturePm.setPmInterface(pmInterface);
            todayTemperaturePm.setLocation(location);

            pmDetailSetup(todayTemperaturePm, workflowList, pmInterface.isGeneral(), defaultPerson);
        }
    }

    private void pmDetailSetup(Pm todayPm, List<Workflow> workflowList, boolean isGeneral, Person defaultPerson) {
        if (isGeneral) {
            GeneralPmDetail generalPmDetail = new GeneralPmDetail();
            generalPmDetail.setGeneralPm(todayPm);
            generalPmDetail.setActive(true);
            generalPmDetail.setDelay(0);
            generalPmDetail.setDate(UtilService.getDATE());
            generalPmDetail.setTime(UtilService.getTime());
            generalPmDetail.setPersistence(logService.persistenceSetup(defaultPerson));
            workflowList.add(generalPmDetail);
        } else {
            TemperaturePmDetail temperaturePmDetail = new TemperaturePmDetail();
            temperaturePmDetail.setTemperaturePm(todayPm);
            temperaturePmDetail.setActive(true);
            temperaturePmDetail.setDelay(0);
            temperaturePmDetail.setDate(UtilService.getDATE());
            temperaturePmDetail.setTime(UtilService.getTime());
            temperaturePmDetail.setPersistence(logService.persistenceSetup(defaultPerson));
            workflowList.add(temperaturePmDetail);
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
                        .getGeneralPmDetailList().stream()
                        .filter(GeneralPmDetail::isActive)
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
        task.setFinishedTime(UtilService.getTime());
        task.setActive(false);
        task.setDailyReport(new DailyReport(UtilService.getTodayReportId()));

        Pm pm = task.getGeneralPm();
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
            task.setName(task.getGeneralPm().getName());
            task.setCurrentAssignedPerson(
                    task
                            .getGeneralPmDetailList().stream()
                            .filter(GeneralPmDetail::isActive)
                            .findFirst().get()
                            .getPersistence()
                            .getPerson().getName());
        }
        return taskList;
    }

    @Override
    public Model getTaskDetailList(Model model, Long taskId) {
        List<GeneralPmDetail> generalPmDetailList = taskDetailRepository.findByTaskId(taskId);
        for (GeneralPmDetail generalPmDetail : generalPmDetailList
        ) {
            generalPmDetail.setPersianRegisterDate(UtilService.getFormattedPersianDateTime(generalPmDetail.getAssignedTime()));
            generalPmDetail.setAssignedPerson((generalPmDetail.getPersistence().getPerson()).getName());
            if (!generalPmDetail.isActive()) {
                generalPmDetail.setPersianFinishedDate(UtilService.getFormattedPersianDateTime(generalPmDetail.getFinishedDateTime()));
            }
        }
        var orderedTaskDetailList = generalPmDetailList
                .stream()
                .sorted(Comparator.comparing(GeneralPmDetail::getId).reversed())
                .toList();

        var task = generalPmDetailList.get(0).getTask();
        List<Long> persistenceIdList = taskDetailRepository.getPersistenceIdList(task.getId());
        List<MetaData> metaDataList = fileService.getRelatedMetadataList(persistenceIdList);
        if (!metaDataList.isEmpty()) {
            model.addAttribute("metaDataList", metaDataList);
        }

        var ownerUsername = generalPmDetailList.stream().filter(GeneralPmDetail::isActive).findFirst().get().getPersistence().getPerson().getUsername();
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

    private GeneralPmDetail assignNewTaskDetail(GeneralPmDetail generalPmDetail, int personId, char actionCode, boolean active) {
        generalPmDetail.setAssignedTime(LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()));
        generalPmDetail.setDelay(0);
        Persistence persistence = logService.persistenceSetup(new Person(personId));

        if (active) {
            generalPmDetail.setActive(true);
        } else {
            generalPmDetail.setActive(false);
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), actionCode, new Person(personId), persistence);
            generalPmDetail.setFinishedDateTime(generalPmDetail.getAssignedTime());
        }

        generalPmDetail.setPersistence(persistence);
        taskDetailRepository.save(generalPmDetail);
        notificationService.sendActiveTaskAssignedMessage(personService.getPerson(personId).getAddress().getValue(), generalPmDetail.getTask().getName(), generalPmDetail.getTask().getDelay(), generalPmDetail.getAssignedTime());
        return generalPmDetail;
    }

    @Override
    @PreAuthorize(" task.active == true  && (ownerUsername == authentication.name || hasAnyAuthority('ADMIN', 'SUPERVISOR')) ")
    public void updateTaskDetail(AssignForm assignForm, Task task, String ownerUsername) throws IOException {
        GeneralPmDetail currentGeneralPmDetail = taskDetailRepository.findByTaskIdAndActive(task.getId(), true).get();
        Persistence currentTaskDetailPersistence = currentGeneralPmDetail.getPersistence();
        var currentUsername = personService.getCurrentUsername();
        currentGeneralPmDetail.setFinishedDateTime(LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()));
        currentGeneralPmDetail.setActive(false);

        if (ownerUsername.equals(currentUsername)) {
            routineOperation(currentGeneralPmDetail, currentTaskDetailPersistence, assignForm);
        } else {
            supervisorOperation(currentGeneralPmDetail, currentTaskDetailPersistence, assignForm, personService.getCurrentPerson());
        }
        taskDetailRepository.save(currentGeneralPmDetail);

        if (assignForm.getActionType() == 0) {  //  End Task
            endTask(task);
        } else { //  Assign Task
            GeneralPmDetail generalPmDetail = new GeneralPmDetail();
            generalPmDetail.setTask(task);
            assignNewTaskDetail(generalPmDetail, assignForm.getActionType(), '0', true);
        }
    }

    private void routineOperation(GeneralPmDetail currentGeneralPmDetail, Persistence currentTaskDetailPersistence, AssignForm assignForm) throws IOException {
        currentGeneralPmDetail.setDescription(assignForm.getDescription());
        logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), '1', currentTaskDetailPersistence.getPerson(), currentTaskDetailPersistence);
        fileService.checkAttachment(assignForm.getFile(), currentTaskDetailPersistence);
    }

    private void supervisorOperation(GeneralPmDetail currentGeneralPmDetail, Persistence currentTaskDetailPersistence, AssignForm assignForm, Person currentPerson) throws IOException {
        currentGeneralPmDetail.setDescription("Terminated by supervisor");
        logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), '2', currentPerson, currentTaskDetailPersistence);
        GeneralPmDetail supervisorGeneralPmDetail = new GeneralPmDetail(currentGeneralPmDetail.getTask());
        supervisorGeneralPmDetail.setDescription(assignForm.getDescription());
        assignNewTaskDetail(supervisorGeneralPmDetail, currentPerson.getId(), '3', false);
        fileService.checkAttachment(assignForm.getFile(), supervisorGeneralPmDetail.getPersistence());
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
                task.setName(task.getGeneralPm().getName());
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
        pm.setCategory(new PmCategory(pmRegisterForm.getTypeId()));
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
        List<PmCategory> pmCategoryList = pmTypeRepository.findAll();
        model.addAttribute("pmTypeList", pmCategoryList);
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
            pmForm.setTypeId(selectedPm.getCategory().getId());

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
