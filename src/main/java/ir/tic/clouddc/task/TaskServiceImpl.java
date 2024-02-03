package ir.tic.clouddc.task;

import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.center.Salon;
import ir.tic.clouddc.center.SalonPmDue;
import ir.tic.clouddc.notification.NotificationService;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.report.DailyReport;
import ir.tic.clouddc.report.ReportService;
import ir.tic.clouddc.utils.UtilityService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@EnableScheduling
@Transactional
public class TaskServiceImpl implements TaskService {

    private final PmRepository pmRepository;
    private final TaskRepository taskRepository;
    private final TaskDetailRepository taskDetailRepository;
    private final CenterService centerService;
    private final PersonService personService;
    private final ReportService reportService;
    private final NotificationService notificationService;
    private final UtilityService utilityService;
    private static LocalDate CurrentDate;
    private static final int DEFAULT_ASSIGNEE_ID = 5;

    @Autowired
    TaskServiceImpl(PmRepository pmRepository,
                    TaskRepository taskRepository,
                    TaskDetailRepository taskDetailRepository,
                    CenterService centerService,
                    PersonService personService,
                    ReportService reportService,
                    NotificationService notificationService, UtilityService utilityService) {
        this.pmRepository = pmRepository;
        this.taskRepository = taskRepository;
        this.taskDetailRepository = taskDetailRepository;
        this.centerService = centerService;
        this.personService = personService;
        this.reportService = reportService;
        this.notificationService = notificationService;
        this.utilityService = utilityService;
    }

    @Scheduled(cron = "@midnight")
    private void updateTodayTasks() {
        CurrentDate = LocalDate.now();
        final Person defaultPerson = personService.getPerson(DEFAULT_ASSIGNEE_ID);
        List<SalonPmDue> todayRecordList = centerService.getTodaySalonPmList();
        List<Task> activeTaskList = taskRepository.findByActive(true);

        reportService.setTodayReport();

        delayCalculation(activeTaskList);

        if (!todayRecordList.isEmpty()) {
            List<Pm> todayPmList = new ArrayList<>();
            for (SalonPmDue todayRecord : todayRecordList
            ) {
                Task task = taskSetup(todayRecord.getPm(), todayRecord.getSalon());
                taskDetailSetup(task, defaultPerson);
                if (!todayRecord.getPm().isActive()) {
                    todayRecord.getPm().setActive(true);
                    todayPmList.add(todayRecord.getPm());
                }
            }
            pmRepository.saveAll(todayPmList);
        }

        notificationService.sendScheduleUpdateMessage("09127016653", "Scheduler successful @: " + LocalDateTime.now());
    }

    private TaskDetail taskDetailSetup(Task task, Person person) {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setPerson(person);
        taskDetail.setAssignedDate(LocalDateTime.now());
        taskDetail.setActive(true);
        taskDetail.setTask(task);
        task.addTaskDetail(taskDetail);
        return taskDetail;
    }

    private Task taskSetup(Pm pm, Salon salon) {
        Task todayTask = new Task();
        todayTask.setDelay(0);
        todayTask.setActive(true);
        todayTask.setSalon(salon);
        todayTask.setDueDate(CurrentDate);
        todayTask.setPm(pm);
        pm.addTask(todayTask);
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
    public Pm getPm(int pmId) {
        var pm = pmRepository.findById(pmId);
        if (pm.isPresent()) {
            if (pm.get().getLastSuccessful() != null) {
                pm.get().setLastSuccessfulPersian(utilityService.getPersianFormattedDateTime(pm.get().getLastSuccessful()));
            }
            return pm.get();
        } else {
            throw new NoSuchElementException("No such Pm: " + pmId);
        }
    }

    private Task getTask(Long taskId) {
        var task = taskRepository.findById(taskId);
        if (task.isPresent()) {
            task.get().setDueDatePersian(utilityService.getPersianFormattedDate(task.get().getDueDate()));
            if (!task.get().isActive()) {
                task.get().setSuccessDatePersian(utilityService.getPersianFormattedDateTime(task.get().getSuccessDate()));
            }
            return task.get();
        } else {
            throw new NoSuchElementException("No such task: " + taskId);
        }
    }

    private List<Pm> getPmList() {
        List<Pm> pmList = pmRepository
                .findAll(Sort.by("active").descending());
        for (Pm pm : pmList
        ) {
            if (pm.getLastSuccessful() != null) {
                pm.setLastSuccessfulPersian(utilityService.getPersianFormattedDateTime(pm.getLastSuccessful()));
            }
        }
        return pmList;
    }

    private void updateTask(Task task) {
        var successDateTime = LocalDateTime.now();
        CurrentDate = successDateTime.toLocalDate();
        Pm pm = task.getPm();
        Optional<DailyReport> report = reportService.findActive(true);

        task.setSuccessDate(successDateTime);
        task.setSuccessDatePersian(utilityService.getPersianFormattedDateTime(task.getSuccessDate()));
        task.setActive(false);
        task.setDailyReport(report.get());
        pm.setLastSuccessful(task.getSuccessDate());
        pm.setLastSuccessfulPersian(utilityService.getPersianFormattedDateTime(pm.getLastSuccessful()));

        var salonPmDueRecord = pm
                .getSalonPmDueList()
                .stream()
                .filter(salonPmDue -> salonPmDue.getSalon().getId() == task.getSalon().getId())
                .findAny();

        if (salonPmDueRecord.isPresent()) {
            var nextDue = CurrentDate.plusDays(pm.getPeriod());
            if (nextDue.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()).equals("Thu")) {
                salonPmDueRecord.get().setDue(CurrentDate.plusDays(pm.getPeriod() + 2));
            } else if (nextDue.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()).equals("Fri")) {
                salonPmDueRecord.get().setDue(CurrentDate.plusDays(pm.getPeriod() + 1));
            } else {
                salonPmDueRecord.get().setDue(nextDue);
            }
        }

        if (pm.getTaskList().stream().noneMatch(Task::isActive)) {
            pm.setActive(false);    // pm is inactive til next due
        }
        pmRepository.saveAndFlush(pm);
    }


    @Override
    public List<Task> getTaskList(Pm pm) {
        for (Task task : pm.getTaskList()
        ) {
            task.setDueDatePersian(utilityService.getPersianFormattedDate(task.getDueDate()));
            if (!task.isActive()) {
                task.setSuccessDatePersian(utilityService.getPersianFormattedDateTime(task.getSuccessDate()));
            }
        }
        return pm.getTaskList();
    }

    private List<TaskDetail> getDetailList(Task task) {
        for (TaskDetail taskDetail : task.getTaskDetailList()
        ) {
            taskDetail.setPersianDate(utilityService.getPersianFormattedDateTime(taskDetail.getAssignedDate()));
        }

        return task.getTaskDetailList()
                .stream()
                .sorted(Comparator.comparing(TaskDetail::getId).reversed())
                .collect(Collectors.toList());
    }


    @Override
    public void updateTaskDetail(AssignForm assignForm, Long id) {
        TaskDetail taskDetail = taskDetailRepository.findById(id).get();

        if (assignForm.getActionType() == 100) {     // Ends task. No assign
            taskDetail.setDescription(assignForm.getDescription());
            taskDetail.setActive(false);
            updateTask(taskDetail.getTask());
        } else { // Updates current taskDetail ,creates a new taskDetail and assigns it to specified person
            taskDetail.setDescription(assignForm.getDescription());
            taskDetail.setActive(false);
            taskDetailSetup(taskDetail.getTask(), personService.getPerson(assignForm.getId()));
        }
    }

    @Override
    public void taskRegister(PmRegisterForm pmRegisterForm) {
        var person = personService.getPerson(pmRegisterForm.getPersonId());
        var salonList = centerService.getSalonList();
        var newPm = new Pm();
        newPm.setTitle(pmRegisterForm.getName());
        newPm.setPeriod(pmRegisterForm.getPeriod());
        newPm.setDescription(pmRegisterForm.getDescription());
        salonPmDueSetup(newPm, salonList, pmRegisterForm.getDueDate(), pmRegisterForm.getSalonId());

        if (pmRegisterForm.getDueDate().equals(LocalDate.now())) { // Activates Pm and creates related task and details
            newPm.setActive(true);
            if (pmRegisterForm.getSalonId() == 0) { // both salons
                for (int i = 0; i < 2; i++) {
                    Task newTask = taskSetup(newPm, salonList.get(i));
                    var newTaskDetail = taskDetailSetup(newTask, person);
                    notificationService.sendNewTaskAssignedMessage(newTaskDetail.getPerson().getAddress().getValue(), newPm.getTitle(), newTaskDetail.getAssignedDate());
                }
            } else { // selected salon
                Task newTask = taskSetup(newPm, centerService.getSalon(pmRegisterForm.getSalonId()));
                var newTaskDetail = taskDetailSetup(newTask, person);
                notificationService.sendNewTaskAssignedMessage(newTaskDetail.getPerson().getAddress().getValue(), newPm.getTitle(), newTaskDetail.getAssignedDate());
            }
        } else {
            newPm.setActive(false);
        }

        pmRepository.saveAndFlush(newPm);
    }

    private void salonPmDueSetup(Pm newPm, List<Salon> salonList, LocalDate dueDate, int salonId) {
        if (salonId == 0) { // 2 records for two salons
            for (int i = 0; i < 2; i++) {
                SalonPmDue salonPmDueRecord = new SalonPmDue();
                salonPmDueRecord.setDue(dueDate);
                salonPmDueRecord.setSalon(salonList.get(i));
                salonPmDueRecord.setPm(newPm);
                newPm.setSalonPmDueList(salonPmDueRecord);
            }
        } else { // record for selected salon
            SalonPmDue salonPmDueRecord = new SalonPmDue();
            salonPmDueRecord.setDue(dueDate);
            salonPmDueRecord.setSalon(salonList.get(salonId));
            salonPmDueRecord.setPm(newPm);
            newPm.setSalonPmDueList(salonPmDueRecord);
        }
    }

    @Override
    public List<Task> getPersonTaskList() {
        Person person = personService.getPerson(personService.getAuthenticatedPersonId());
        List<TaskDetail> taskDetailList = taskDetailRepository.findAllByPerson_IdAndActive(person.getId(), true);
        List<Task> personTaskList = new ArrayList<>();
        for (TaskDetail taskDetail : taskDetailList

        ) {
            var dueDate = taskDetail.getTask().getDueDate();
            taskDetail.getTask().setDueDatePersian(utilityService.getPersianFormattedDate(dueDate));
            personTaskList.add(taskDetail.getTask());
        }
        return personTaskList
                .stream()
                .sorted(Comparator.comparing(Task::getDelay).reversed())
                .collect(Collectors.toList());
    }


    @Override
    public void modifyPm(PmRegisterForm editForm, int pmId) {
        var pm = this.getPm(pmId);
        pm.setTitle(editForm.getName());
        pm.setPeriod(editForm.getPeriod());
        pm.setDescription(editForm.getDescription());
        pmRepository.saveAndFlush(pm);
    }

    @Override
    public Model modelForTaskController(Model model) {
        Person person = personService.getPerson(personService.getAuthenticatedPersonId());
        model.addAttribute("person", person);
        model.addAttribute("date", UtilityService.getCurrentPersianDate());

        return model;
    }

    @Override
    public Model pmListService(Model model) {
        model.addAttribute("pmList", this.getPmList());
        return model;
    }

    @Override
    public Model pmTaskListService(Model model, int pmId) {
        var pm = this.getPm(pmId);
        model.addAttribute("taskList", this.getTaskList(pm));
        model.addAttribute("pm", pm);
        return model;
    }

    @Override
    public Model pmEditFormService(Model model, int pmId) {
        var pm = this.getPm(pmId);
        PmRegisterForm pmEditForm = new PmRegisterForm();
        pmEditForm.setName(pm.getTitle());
        pmEditForm.setDescription(pm.getDescription());
        pmEditForm.setPeriod(pm.getPeriod());
        model.addAttribute("pmEdit", pmEditForm);
        model.addAttribute("taskSize", pm.getTaskList().size());
        model.addAttribute("pmId", pm.getId());
        model.addAttribute("personList", personService.getPersonList());
        model.addAttribute("centerList", centerService.getSalonList());

        return model;
    }

    @Override
    public Model pmRegisterFormService(Model model) {
        model.addAttribute("pmRegister", new PmRegisterForm());
        model.addAttribute("personList", personService.getPersonList());
        model.addAttribute("centerList", centerService.getSalonList());
        return model;
    }

    @Override
    public Model taskDetailListService(Model model, Long taskId) {
        var task = this.getTask(taskId);
        List<TaskDetail> taskDetailList = this.getDetailList(task);
        var activeDetail = taskDetailList.stream().filter(TaskDetail::isActive).findFirst();
        var active = task.isActive();
        var delay = task.getDelay();
        var dueDate = task.getDueDatePersian();
        var pmName = task.getPm().getTitle();
        var personId = personService.getAuthenticatedPersonId();
        var permission = activeDetail.filter(taskDetail -> (personId == taskDetail.getPerson().getId())).isPresent();
        model.addAttribute("permission", permission);
        model.addAttribute("taskDetailList", taskDetailList);
        model.addAttribute("name", pmName);
        model.addAttribute("taskId", taskId);
        model.addAttribute("delay", delay);
        model.addAttribute("duedate", dueDate);
        model.addAttribute("active", active);
        model.addAttribute("task", task);
        model.addAttribute("pmId", task.getPm().getId());

        return model;
    }

    @Override
    public Model personTaskListService(Model model) {
        List<Task> personTaskList = getPersonTaskList();
        if (!personTaskList.isEmpty()) {
            model.addAttribute("userTaskList", personTaskList);
        }
        return model;
    }

    @Override
    @PreAuthorize("authenticatedPersonId == taskDetailPersonId")
    public TaskDetail taskActionFormService(Model model, long taskDetailId, long authenticatedPersonId, long taskDetailPersonId) {
        List<Person> personList = personService.getPersonListNotIn(personService.getAuthenticatedPersonId());
        var taskDetail = taskDetailRepository.findById(taskDetailId);
        if (taskDetail.isPresent()) {
            Task task = taskDetail.get().getTask();
            var taskName = task.getPm().getTitle();
            var description = task.getPm().getDescription();
            var dueDate = utilityService.getPersianFormattedDate(task.getDueDate());
            var center = task.getSalon().getName();
            var delay = task.getDelay();
            var personName = taskDetail.get().getPerson().getName();
            AssignForm assignForm = new AssignForm();
            assignForm.setId(taskDetailId);
            model.addAttribute("id", taskDetailId);
            model.addAttribute("taskDetail", taskDetailRepository.findById(taskDetailId));
            model.addAttribute("taskName", taskName);
            model.addAttribute("dueDate", dueDate);
            model.addAttribute("center", center);
            model.addAttribute("personName", personName);
            model.addAttribute("personList", personList);
            model.addAttribute("delay", delay);
            model.addAttribute("assignForm", assignForm);
            model.addAttribute("description", description);
        }

        return taskDetailRepository.findById(taskDetailId).get();
    }

}
