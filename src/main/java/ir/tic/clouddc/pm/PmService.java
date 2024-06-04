package ir.tic.clouddc.pm;

import ir.tic.clouddc.report.DailyReport;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.List;

public interface PmService {

    void updateTodayTasks(DailyReport todayReport);
    List<Pm> getAllActiveTaskList();
    List<PmInterface> getPmInterfaceList();
    Model getPmInterfacePmListModel(Model model, short pmInterfaceId, boolean active, int locationId);
    Model pmInterfaceEditFormData(Model model, int pmId);
    Model PmTypeOverview(Model model);
    Model getPmInterfaceFormData(Model model);
    Model modelForTaskController(Model model);
    Model getPmDetailList(Model model, int pmId);
    Model getPersonTaskList(Model model);

    Model getPmUpdateForm(Model model, Pm pm, String ownerUsername);

    void pmInterfaceRegister(pmInterfaceRegisterForm pmInterfaceRegisterForm) throws IOException;

    void updatePm(PmUpdateForm pmUpdateForm, Pm pm, String ownerUsername) throws IOException;

    long getFinishedTaskCount();

    long getOnTimeTaskCount();

    long getActiveTaskCount();

    int getWeeklyFinishedPercentage();

    int getActiveDelayedPercentage();

    String getOwnerUsername(int taskId);

    Pm getPm(int taskId);


}
