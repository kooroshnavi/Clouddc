package ir.tic.clouddc.pm;

import ir.tic.clouddc.report.DailyReport;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.List;

public interface PmService {

    void updateTodayTasks(DailyReport todayReport);
    List<Task> getTaskList(int pmId);
    Pm getPm(int pmId);
    List<Task> getActivePersonTaskList();
    void editPm(PmRegisterForm editForm, int id);
    List<Pm> getPmList();
    List<Task> getPmTaskList(int pmId, boolean active);

    Model PmTypeOverview(Model model);
    Model modelForRegisterTask(Model model);
    Model modelForTaskController(Model model);
    Model modelForTaskDetail(Model model, Long taskId);
    Model modelForActivePersonTaskList(Model model);

    TaskDetail modelForActionForm(Model model, Long taskDetailId);

    void pmRegister(PmRegisterForm pmRegisterForm) throws IOException;

    void updateTaskDetail(AssignForm assignForm, Long id) throws IOException;

    long getFinishedTaskCount();

    long getOnTimeTaskCount();

    long getActiveTaskCount();

    int getWeeklyFinishedPercentage();

    int getActiveDelayedPercentage();

}
