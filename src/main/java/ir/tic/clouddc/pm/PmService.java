package ir.tic.clouddc.pm;

import ir.tic.clouddc.report.DailyReport;
import org.springframework.ui.Model;

import java.util.List;

public interface PmService {

    void updateTodayTasks(DailyReport todayReport);
    List<Task> getTaskList(int pmId);
    Pm getPm(int pmId);
    List<Task> getActivePersonTaskList();
    void editPm(PmRegisterForm editForm, int id);
    List<Pm> getPmList(int pmTypeId);
    Model PmTypeOverview(Model model);
    Model modelForRegisterTask(Model model);
    Model modelForTaskController(Model model);
    Model modelForTaskDetail(Model model, Long taskId);
    Model modelForActivePersonTaskList(Model model);

    TaskDetail modelForActionForm(Model model, Long taskDetailId);

    void pmRegister(PmRegisterForm pmRegisterForm);

    void updateTaskDetail(AssignForm assignForm, Long id);

    long getFinishedTaskCount();

    long getOnTimeTaskCount();

    long getActiveTaskCount();

    int getWeeklyFinishedPercentage();

    int getActiveDelayedPercentage();

}
