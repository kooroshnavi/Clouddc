package com.navi.dcim.task;

import com.navi.dcim.form.PmRegisterForm;
import com.navi.dcim.model.Task;
import com.navi.dcim.model.TaskDetail;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

@Service
public interface TaskService {


    Task getTask(int taskId);
    List<Task> getTaskListById(int taskStatusId);
    List<TaskDetail> getTaskDetailById(int taskId);
    List<Task> getPersonTask();
    boolean checkPermission(String authenticatedName, Optional<TaskDetail> taskDetail);
    Model modelForMainPage(Model model);
    Model modelForRegisterTask(Model model);
    Model modelForTaskController(Model model);
    Model modelForTaskDetail(Model model);
    Model modelForPersonTaskList(Model model);

    Model modelForActionForm(Model model, int taskDetailId);

    void taskRegister(PmRegisterForm pmRegisterForm);
}
