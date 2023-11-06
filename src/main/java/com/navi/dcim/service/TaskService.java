package com.navi.dcim.service;

import com.github.mfathi91.time.PersianDate;
import com.github.mfathi91.time.PersianDateTime;
import com.navi.dcim.form.AssignForm;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class TaskService {

    private TaskStatusRepository taskStatusRepository;
    private TaskRepository taskRepository;
    private TaskDetailRepository taskDetailRepository;
    private PersonRepository personRepository;
    private CenterRepository centerRepository;

    @Autowired
    public TaskService(TaskStatusRepository taskStatusRepository,
                       TaskRepository taskRepository,
                       PersonRepository personRepository,
                       CenterRepository centerRepository,
                       TaskDetailRepository taskDetailRepository) {
        this.taskStatusRepository = taskStatusRepository;
        this.taskRepository = taskRepository;
        this.personRepository = personRepository;
        this.centerRepository = centerRepository;
        this.taskDetailRepository = taskDetailRepository;
    }


    public void updateTodayTasks() {
        List<TaskStatus> taskStatusList = taskStatusRepository.findAll(Sort.unsorted());
        List<Task> taskList = taskRepository.findAll(Sort.unsorted());
        List<Center> centers = (List<Center>) centerRepository.findAll();
        List<Person> personList = personRepository.findAll();

        delayCalculation(taskList);

        for (TaskStatus status : taskStatusList
        ) {
            if (isTodayTask(status)) {
                Task todayTask = setupTask(status, centers);
                TaskDetail taskDetail = setupTaskDetail(todayTask, personList);
                todayTask.setTaskDetailList(taskDetail);
                status.setTasks(todayTask);
                taskStatusRepository.save(status);
            }
        }
    }

    private TaskDetail setupTaskDetail(Task todayTask, List<Person> personList) {
        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setPerson(personList.get(new Random().nextInt(personList.size())));
        taskDetail.setAssignedDate(LocalDateTime.now());
        taskDetail.setFinished(false);
        taskDetail.setTask(todayTask);
        return taskDetail;
    }

    private Task setupTask(TaskStatus status, List<Center> centers) {
        Task todayTask = new Task();
        todayTask.setTaskStatus(status);
        todayTask.setDelay(0);
        todayTask.setStatus(false);
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

    public TaskStatus updateTask(Task task) {
        TaskStatus taskStatus = task.getTaskStatus();

        DateTimeFormatter dateTime = DateTimeFormatter.ISO_DATE_TIME;
        task.setSuccessDate(LocalDateTime.now());
        task.setSuccessDatePersian(dateTime.format(PersianDateTime.fromGregorian(task.getSuccessDate())));
        task.setStatus(true);

        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        taskStatus.setLastSuccessful(task.getSuccessDate());
        taskStatus.setNextDue(taskStatus.getLastSuccessful().toLocalDate().plusDays(taskStatus.getPeriod()));
        taskStatus.setLastSuccessfulPersian(dateTime.format(PersianDateTime.fromGregorian(taskStatus.getLastSuccessful())));
        taskStatus.setNextDuePersian(date.format(PersianDate.fromGregorian(taskStatus.getNextDue())));
        return taskStatusRepository.save(taskStatus);
    }

    public List<Task> getTaskListById(int taskStatusId) {
        TaskStatus taskStatus = taskStatusRepository.findById(taskStatusId).get();
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter dateTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        for (Task task : taskStatus.getTasks()
        ) {
            task.setDueDatePersian(date.format(PersianDate.fromGregorian(task.getDueDate())));
            if (task.getStatus()) {
                task.setSuccessDatePersian(dateTime.format(PersianDateTime.fromGregorian(task.getSuccessDate())));
            }
        }
        return taskStatus.getTasks();
    }

    public List<TaskDetail> getTaskDetailById(int taskId) {
        Task task = taskRepository.findById(taskId).get();

        DateTimeFormatter dateTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
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
        DateTimeFormatter dateTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        TaskDetail newTaskDetail = new TaskDetail();
        newTaskDetail.setAssignedDate(LocalDateTime.now());
        newTaskDetail.setFinished(false);
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
                taskDetail.setFinished(true);
                updateTask(taskDetail.getTask());
                break;

            default: // Updates current taskDetail ,creates a new taskDetail and assigns it to specified person
                taskDetail.setDescription(assignForm.getDescription());
                taskDetail.setFinished(true);
                assignNewTaskDetail(taskDetailId, assignForm.getActionType());
                break;

        }

    }

    public Task getTask(int taskDetailId) {
        return taskDetailRepository.findById(taskDetailId).get().getTask();
    }



    public List<Task> getUserTask(int id) {
        List<TaskDetail> taskDetailList = taskDetailRepository.findAllByPerson_IdAndFinishedFalse(id);
        List<Task> userTasks = new ArrayList<>();
        for (TaskDetail taskDetail : taskDetailList
        ) {
           userTasks.add(taskDetail.getTask());
        }
        return userTasks;
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
