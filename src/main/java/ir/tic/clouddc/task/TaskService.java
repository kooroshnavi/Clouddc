package ir.tic.clouddc.task;

import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    Task getTask(Long taskId);
    List<Task> getTaskListById(int pmId);
    List<Task> getPersonTask();
    List<Task> getActiveTaskList(int pmId);
    void modifyPm(PmRegisterForm editForm, int id);
    Optional<TaskDetail> activeTaskDetail(long taskId);
    Pm getPm(int id);
    Model modelForMainPage(Model model);
    Model modelForRegisterTask(Model model);
    Model modelForTaskController(Model model);
    Model modelForTaskDetail(Model model, Long taskId);
    Model modelForPersonTaskList(Model model);
    TaskDetail modelForActionForm(Model model, long taskDetailId, long authenticatedPersonId, long taskDetailPersonId);
    void taskRegister(PmRegisterForm pmRegisterForm);
    void updateTaskDetail(AssignForm assignForm, Long id);
}
