package ir.tic.clouddc.pm;

import ir.tic.clouddc.report.DailyReport;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

public interface PmService {

    void updateTodayTasks(DailyReport todayReport);
    Task getTask(Long taskId);
    List<Task> getTaskList(int pmId);
    Pm getPm(int pmId);
    List<Task> getPersonTask();
    List<Task> getActiveTaskList(int statusId);
    void editPm(PmRegisterForm editForm, int id);
    Optional<TaskDetail> activeTaskDetail(long taskId, boolean active);
    List<Pm> getPmList(int pmTypeId);
    Model PmTypeOverview(Model model);
    Model modelForRegisterTask(Model model);
    Model modelForTaskController(Model model);
    Model modelForTaskDetail(Model model, Long taskId);
    Model modelForPersonTaskList(Model model);

    TaskDetail modelForActionForm(Model model, Long taskDetailId);

    void pmRegister(PmRegisterForm pmRegisterForm);

    void updateTaskDetail(AssignForm assignForm, Long id);

    long getFinishedTaskCount();

    long getOnTimeTaskCount();

    long getActiveTaskCount();

    int getWeeklyFinishedPercentage();

    int getActiveDelayedPercentage();

}
