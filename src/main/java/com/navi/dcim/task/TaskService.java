package com.navi.dcim.task;

import org.springframework.ui.Model;

import java.util.List;

public interface TaskService {


    Task getTask(Long taskId);
    List<Task> getTaskListById(int taskStatusId);
    List<Task> getPersonTask();

    void updateStatus(PmRegisterForm editForm, int id);

    TaskStatus getStatusForEdit(int id);
    Model modelForMainPage(Model model);
    Model modelForRegisterTask(Model model);
    Model modelForTaskController(Model model);
    Model modelForTaskDetail(Model model, Long taskId);
    Model modelForPersonTaskList(Model model);

    TaskDetail modelForActionForm(Model model, Long taskDetailId);

    void updateTodayTasks();

    void taskRegister(PmRegisterForm pmRegisterForm);

    void updateTaskDetail(AssignForm assignForm, Long id);
}
