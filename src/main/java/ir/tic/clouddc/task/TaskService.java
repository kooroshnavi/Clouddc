package ir.tic.clouddc.task;

import org.springframework.ui.Model;

import java.util.List;

public interface TaskService {
    List<Task> getTaskList(Pm pm);
    List<Task> getPersonTaskList();
    void modifyPm(PmRegisterForm editForm, int id);
    Pm getPm(int id);
    Model pmListService(Model model);
    Model pmTaskListService(Model model, int pmId);
    Model pmEditFormService(Model model, int pmId);
    Model pmRegisterFormService(Model model);

    Model modelForTaskController(Model model);
    Model taskDetailListService(Model model, Long taskId);
    Model personTaskListService(Model model);
    TaskDetail taskActionFormService(Model model, long taskDetailId, long authenticatedPersonId, long taskDetailPersonId);
    void taskRegister(PmRegisterForm pmRegisterForm);
    void updateTaskDetail(AssignForm assignForm, Long id);
}
