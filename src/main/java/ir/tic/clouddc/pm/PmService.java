package ir.tic.clouddc.pm;

import ir.tic.clouddc.report.DailyReport;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.List;

public interface PmService {

    void updateTodayTasks(DailyReport todayReport);
    List<Task> getAllActiveTaskList();
    List<Pm> getPmList();
    List<Task> getPmTaskList(int pmId, boolean active);

    Model pmEditFormData(Model model, int pmId);
    Model PmTypeOverview(Model model);
    Model getPmFormData(Model model);
    Model modelForTaskController(Model model);
    Model getTaskDetailList(Model model, Long taskId);
    Model getPersonTaskList(Model model);

    Model prepareAssignForm(Model model, Task task, String ownerUsername);

    void pmRegister(PmRegisterForm pmRegisterForm) throws IOException;

    void updateTaskDetail(AssignForm assignForm, Task task, String ownerUsername) throws IOException;

    long getFinishedTaskCount();

    long getOnTimeTaskCount();

    long getActiveTaskCount();

    int getWeeklyFinishedPercentage();

    int getActiveDelayedPercentage();

    String getOwnerUsername(Long taskId);

    Task getTask(long taskId);


}
