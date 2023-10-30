package com.navi.dcim.service;

import com.github.mfathi91.time.PersianDate;
import com.github.mfathi91.time.PersianDateTime;
import com.navi.dcim.model.Center;
import com.navi.dcim.model.Person;
import com.navi.dcim.model.Task;
import com.navi.dcim.model.TaskStatus;
import com.navi.dcim.repository.CenterRepository;
import com.navi.dcim.repository.PersonRepository;
import com.navi.dcim.repository.TaskRepository;
import com.navi.dcim.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class TaskService {
    public TaskService() {

    }

    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private CenterRepository centerRepository;


    public void updateTodayTasks() {
        List<TaskStatus> taskStatusList = taskStatusRepository.findAll();
        List<Task> taskList = taskRepository.findAll();
        List<Center> centers = (List<Center>) centerRepository.findAll();
        List<Person> personList = (List<Person>) personRepository.findAll();

        delayCalculation(taskList);

        for (TaskStatus status : taskStatusList
        ) {
            if (status.getNextDue().equals(LocalDate.now())) {
                Task todayTask = new Task();
                todayTask.setTaskStatus(status);
                todayTask.setDelay(0);
                todayTask.setStatus(false);
                todayTask.setCenter(centers.get(new Random().nextInt(centers.size())));
                todayTask.setPerson(personList.get(new Random().nextInt(personList.size())));
                todayTask.setDueDate(status.getNextDue());
                status.setTasks(todayTask);
                taskStatusRepository.save(status);
            }
        }
    }

    private void delayCalculation(List<Task> taskList) {
        if (!taskList.isEmpty()) {
            for (Task task : taskList
            ) {
                if (!task.getStatus()) {
                    var delayedDays = LocalDate.now().compareTo(ChronoLocalDate.from(task.getDueDate()));
                    task.setDelay(delayedDays);
                    taskRepository.save(task);
                }
            }
        }
    }

    public List<TaskStatus> getTaskStatus() {
        List<TaskStatus> taskStatusList = taskStatusRepository
                .findAll(Sort.by("lastSuccessful").descending());
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter dateTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
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
        List<Task> tasks = taskRepository.
                findAll(Sort.by("dueDate").descending());
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
        for (Task task : tasks
        ) {
            task.setDueDatePersian(date.format(PersianDate.fromGregorian(task.getDueDate())));
            if (task.getStatus()) {
                task.setSuccessDatePersian(dateTimeFormatter.format(PersianDateTime.fromGregorian(task.getSuccessDate())));
            }
        }
        return tasks;
    }

    public TaskStatus updateTask(int id, String description) {
        Task task = taskRepository.findById(id).get();
        TaskStatus taskStatus = task.getTaskStatus();

        task.setSuccessDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        DateTimeFormatter dateTime = DateTimeFormatter.ISO_DATE_TIME;
        task.setSuccessDatePersian(dateTime.format(PersianDateTime.fromGregorian(task.getSuccessDate())));
        task.setStatus(true);
        task.setDescription(description);

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        taskStatus.setLastSuccessful(task.getSuccessDate());
        taskStatus.setNextDue(taskStatus.getLastSuccessful().toLocalDate().plusDays(taskStatus.getPeriod()));
        taskStatus.setLastSuccessfulPersian(dateTime.format(PersianDateTime.fromGregorian(taskStatus.getLastSuccessful())));
        taskStatus.setNextDuePersian(date.format(PersianDate.fromGregorian(taskStatus.getNextDue())));
        return taskStatusRepository.save(taskStatus);
    }

    public List<Task> getUserTask(int id) {
        List<Task> tasks = (List<Task>) taskRepository.findAll();
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
    }

    public List<Center> getCenterList() {
        return centerRepository.findAll(Sort.by("name").ascending());
    }

    public List<Person> getPersonList() {
        return personRepository.findAll(Sort.by("name").ascending());
    }

    public void addNewTask(TaskStatus taskStatus) {
        taskStatusRepository.save(taskStatus);
    }
}
