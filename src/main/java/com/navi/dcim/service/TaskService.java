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
import org.springframework.stereotype.Service;

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


    public void setTodayTasks() {
        List<TaskStatus> taskStatusList = (List<TaskStatus>) taskStatusRepository.findAll();
        List<Center> centers = (List<Center>) centerRepository.findAll();
        List<Person> personList = (List<Person>) personRepository.findAll();

        for (TaskStatus status : taskStatusList
        ) {
            if (status.getNextDue().equals(java.time.LocalDate.now())) {
                Task todayTask = new Task();
                todayTask.setTaskStatus(status);
                todayTask.setDelay(0);
                todayTask.setCenter(centers.get(new Random().nextInt(centers.size())));
                todayTask.setPerson(personList.get(new Random().nextInt(personList.size())));
                todayTask.setStatus("Action Required");
                todayTask.setDueDate(status.getNextDue());
                taskRepository.save(todayTask);
            }
        }
    }

    public List<TaskStatus> getTaskStatus() {
        List<TaskStatus> taskStatusList = (List<TaskStatus>) taskStatusRepository.findAll();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        for (TaskStatus taskStatus: taskStatusList
             ) {
            taskStatus.setNextDuePersian(dtf.format(PersianDate.fromGregorian(taskStatus.getNextDue())));
        }
        return taskStatusList;
    }

    public List<Task> getUnfinishedTasks() {
        List<Task> tasks = (List<Task>) taskRepository.findAll();
        List<Task> unfinishedTask = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        for (Task task : tasks
        ) {
            if (task.getSuccessDate() == null) {
                task.setDueDatePersian(dtf.format(PersianDate.fromGregorian(task.getDueDate())));
                unfinishedTask.add(task);
            }
        }
        return unfinishedTask;
    }

    public TaskStatus updateTask(int id, String description) {
        Task task = taskRepository.findById(id).get();
        TaskStatus taskStatus = task.getTaskStatus();

        task.setSuccessDate(java.time.LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        task.setSuccessDatePersian(dtf.format(PersianDateTime.fromGregorian(task.getSuccessDate())));
        task.setStatus("Done");
        task.setDescription(description);

        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        taskStatus.setLastSuccessful(task.getSuccessDate());
        taskStatus.setNextDue(taskStatus.getLastSuccessful().toLocalDate().plusDays(taskStatus.getPeriod()));
        taskStatus.setLastSuccessfulPersian(dtf2.format(PersianDate.fromGregorian(task.getSuccessDate().toLocalDate())));
        taskStatus.setNextDuePersian(dtf2.format(PersianDate.fromGregorian(taskStatus.getNextDue())));
        taskRepository.save(task);
        return taskStatusRepository.save(taskStatus);
    }

    public List<Task> getUserTask(int id) {
        List<Task> tasks = (List<Task>) taskRepository.findAll();
        List<Task> userTasks = new ArrayList<>();
        for (Task task : tasks
        ) {
            if (task.getPerson().getId() == id) {
                userTasks.add(task);
            }
        }
        return userTasks;
    }
}
