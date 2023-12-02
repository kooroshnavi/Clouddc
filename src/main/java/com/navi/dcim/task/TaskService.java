package com.navi.dcim.task;

import org.springframework.ui.Model;

import java.util.List;

public interface TaskService {


    Task getTask(int taskId);
    List<Task> getTaskListById(int taskStatusId);
    List<Task> getPersonTask();
    Model modelForMainPage(Model model);
    Model modelForRegisterTask(Model model);
    Model modelForTaskController(Model model);
    Model modelForTaskDetail(Model model, int taskId);
    Model modelForPersonTaskList(Model model);

    Model modelForActionForm(Model model, int taskDetailId, String username);

    void updateTodayTasks();

    void taskRegister(PmRegisterForm pmRegisterForm);

    void updateTaskDetail(int id, AssignForm assignForm);
}
