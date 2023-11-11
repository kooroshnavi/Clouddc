package com.navi.dcim.service;

import com.github.mfathi91.time.PersianDate;
import com.github.mfathi91.time.PersianDateTime;
import com.navi.dcim.form.AssignForm;
import com.navi.dcim.form.EventForm;
import com.navi.dcim.form.PmRegisterForm;
import com.navi.dcim.model.*;
import com.navi.dcim.repository.*;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class TaskService {

    private TaskStatusRepository taskStatusRepository;
    private TaskRepository taskRepository;
    private TaskDetailRepository taskDetailRepository;
    private PersonRepository personRepository;
    private CenterRepository centerRepository;
    private EventTypeRepository eventTypeRepository;
    private EventRepository eventRepository;


    @Autowired
    public TaskService(TaskStatusRepository taskStatusRepository,
                       TaskRepository taskRepository,
                       PersonRepository personRepository,
                       CenterRepository centerRepository,
                       TaskDetailRepository taskDetailRepository,
                       EventTypeRepository eventTypeRepository,
                       EventRepository eventRepository) {
        this.taskStatusRepository = taskStatusRepository;
        this.taskRepository = taskRepository;
        this.personRepository = personRepository;
        this.centerRepository = centerRepository;
        this.taskDetailRepository = taskDetailRepository;
        this.eventTypeRepository = eventTypeRepository;
        this.eventRepository = eventRepository;
    }


    public void updateTodayTasks() {
        List<TaskStatus> taskStatusList = taskStatusRepository.findAll(Sort.unsorted());
        List<Task> taskList = taskRepository.findAll(Sort.unsorted());
        List<Center> centers = centerRepository.findAll();
        List<Person> personList = personRepository.findAll();

        delayCalculation(taskList);

        for (TaskStatus status : taskStatusList
        ) {
            if (isTodayTask(status)) {
                Task todayTask = setupTask(status, centers);
                TaskDetail taskDetail = setupTaskDetail(todayTask, personList);
                todayTask.setTaskDetailList(taskDetail);
                status.setTasks(todayTask);
                status.setActive(true);
            } else if (status.getTasks().stream().anyMatch(task -> task.isActive())) {
                status.setActive(true);
            } else {
                status.setActive(false);
            }
        }
        taskStatusRepository.saveAll(taskStatusList);
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
                    var delayedDays = LocalDate.now().compareTo(ChronoLocalDate.from(task.getDueDate()));
                    task.setDelay(delayedDays);
                }
            }
        }
        taskRepository.saveAll(taskList);
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

        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        task.setSuccessDate(LocalDateTime.now());
        task.setSuccessDatePersian(dateTime.format(PersianDateTime.fromGregorian(task.getSuccessDate())));
        task.setActive(false);

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        taskStatus.setLastSuccessful(task.getSuccessDate());
        taskStatus.setNextDue(taskStatus.getLastSuccessful().toLocalDate().plusDays(taskStatus.getPeriod()));
        taskStatus.setLastSuccessfulPersian(dateTime.format(PersianDateTime.fromGregorian(taskStatus.getLastSuccessful())));
        taskStatus.setNextDuePersian(date.format(PersianDate.fromGregorian(taskStatus.getNextDue())));
        taskStatus.setActive(false);// task is inactive til next due
        return taskStatusRepository.save(taskStatus);
    }

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
        return userTasks;
    }

    public Person getPerson(int i) {
        return personRepository.findById(i).get();
    }

    public void pmRegister(PmRegisterForm pmRegisterForm) {
        var newTaskStatus = new TaskStatus();
        newTaskStatus.setActive(true);
        newTaskStatus.setNextDue(LocalDate.now());
        newTaskStatus.setName(pmRegisterForm.getName());
        newTaskStatus.setPeriod(pmRegisterForm.getPeriod());

        Center center = centerRepository.findById(pmRegisterForm.getCenterId()).get();

        Task todayTask = new Task();
        todayTask.setTaskStatus(newTaskStatus);
        todayTask.setDelay(0);
        todayTask.setActive(true);
        todayTask.setCenter(center);
        todayTask.setDueDate(newTaskStatus.getNextDue());

        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setTask(todayTask);
        if (pmRegisterForm.getPersonId() == 1) { // Random
            List<Person> personList = personRepository.findAll();
            taskDetail.setPerson(personList.get(new Random().nextInt(personList.size())));
        } else { // assign to specified user
            Person person = personRepository.findById(pmRegisterForm.getPersonId()).get();
            taskDetail.setPerson(person);
        }

        taskDetail.setAssignedDate(LocalDateTime.now());
        taskDetail.setActive(true);

        todayTask.setTaskDetailList(taskDetail);
        newTaskStatus.setTasks(todayTask);
        taskStatusRepository.save(newTaskStatus);

    }

    public void eventRegister(EventForm eventForm, Person person) {
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        var eventType = eventTypeRepository.findById(eventForm.getEventType()).get();
        var registerDate = LocalDateTime.now();
        Event event = new Event(registerDate
                , registerDate
                , eventForm.isActive()
                , " ( "
                + dateTime.format(PersianDateTime.fromGregorian(registerDate))
                + "-->" + eventForm.getDescription()
                + " ) "
                + System.lineSeparator()
                , eventType
                , person
                , centerRepository.findById(eventForm.getCenterId()).get());
        eventType.setEvent(event);
        event.setType(eventType);
        eventTypeRepository.save(eventType);
    }


    public List<Person> getPersonList() {
        return personRepository.findAll();
    }

    public List<Center> getCenterList() {
        return centerRepository.findAll();
    }

    public List<EventType> getEventType() {
        return eventTypeRepository.findAll();
    }

    public List<Event> getEventList() {
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        List<Event> eventList = eventRepository.findAll(Sort.by("active").descending());
        for (Event event : eventList
        ) {
            event.setPersianDate(dateTime.format(PersianDateTime.fromGregorian(event.getEventDate())));
            event.setPersianUpdate(dateTime.format(PersianDateTime.fromGregorian(event.getUpdateDate())));
        }

        return eventList;
    }

    public Event getEvent(int eventId) {
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        Event event = eventRepository.findById(eventId).get();
        event.setPersianDate(dateTime.format(PersianDateTime.fromGregorian(event.getEventDate())));
        event.setPersianUpdate(dateTime.format(PersianDateTime.fromGregorian(event.getUpdateDate())));
        return event;
    }

    public void updateEvent(int id, EventForm eventForm) {
        DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        Event event = eventRepository.findById(id).get();
        event.setActive(eventForm.isActive());
        event.setUpdateDate(LocalDateTime.now());
        event.setPersianDate(dateTime.format(PersianDateTime.fromGregorian(event.getEventDate())));
        event.setPersianUpdate(dateTime.format(PersianDateTime.fromGregorian(event.getUpdateDate())));
        var description = event.getDescription()
                + " ( "
                + event.getPersianUpdate()
                + "-->"
                + eventForm.getDescription()
                + " ) "
                + System.lineSeparator();
        event.setDescription(description);
        eventRepository.save(event);

    }

    public List<Event> getPendingEventList(int personId) {
        return eventRepository.findAllByPerson_IdAndActive(personId, true);
    }

    public Person getPersonByName(String personName) {
        return personRepository.findByUsername(personName);
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
