package com.navi.dcim.service;

import com.github.mfathi91.time.PersianDate;
import com.github.mfathi91.time.PersianDateTime;
import com.navi.dcim.model.*;
import com.navi.dcim.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
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
    @Autowired
    private StateRepository stateRepository;


    public void updateTodayTasks() {
        List<TaskStatus> taskStatusList = taskStatusRepository.findAll(Sort.unsorted());
        List<Task> taskList = taskRepository.findAll(Sort.unsorted());
        List<Center> centers = (List<Center>) centerRepository.findAll();
        List<Person> personList = (List<Person>) personRepository.findAll();
        Optional<State> pending = stateRepository.findById(1);

        delayCalculation(taskList);


        for (TaskStatus status : taskStatusList
        ) {
            if (status.getNextDue().equals(LocalDate.now())) {
                TaskDetail taskDetail = new TaskDetail();
                taskDetail.setState(pending);
                taskDetail.setPerson(personList.get(new Random().nextInt(personList.size())));
                taskDetail.setUpdate_date(LocalDateTime.now());

                Task todayTask = new Task();
                todayTask.setTaskStatus(status);
                todayTask.setDelay(0);
                todayTask.setStatus(false);
                todayTask.setCenter(centers.get(new Random().nextInt(centers.size())));
                todayTask.setDueDate(status.getNextDue());
                todayTask.setTaskDetailList(taskDetail);

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
            taskStatus.setLastSuccessfulPersian(dateTime.format(PersianDateTime.fromGregorian(taskStatus.getLastSuccessful())));
        }
        return taskStatusList;
    }

    public List<Task> getTaskList() {
        List<Task> tasks = taskRepository.findAll(Sort.by("dueDate").descending());
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

        task.setSuccessDate(java.time.LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        DateTimeFormatter dateTime = DateTimeFormatter.ISO_DATE_TIME;
        task.setSuccessDatePersian(dateTime.format(PersianDateTime.fromGregorian(task.getSuccessDate())));
        task.setStatus(true);

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        taskStatus.setLastSuccessful(task.getSuccessDate());
        taskStatus.setNextDue(taskStatus.getLastSuccessful().toLocalDate().plusDays(taskStatus.getPeriod()));
        taskStatus.setLastSuccessfulPersian(dateTime.format(PersianDateTime.fromGregorian(taskStatus.getLastSuccessful())));
        taskStatus.setNextDuePersian(date.format(PersianDate.fromGregorian(taskStatus.getNextDue())));
        return taskStatusRepository.save(taskStatus);
    }

    /*public List<Task> getUserTask(int id) {
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
