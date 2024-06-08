package ir.tic.clouddc.pm;

import ir.tic.clouddc.report.DailyReport;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.List;

public interface PmService {
    void updateTodayPmList(DailyReport todayReport);
    List<PmInterface> getPmInterfaceList();
    Model getPmInterfacePmListModel(Model model, short pmInterfaceId, boolean active, int locationId);
    Model pmInterfaceEditFormData(Model model, short pmInterfaceId);
    Model getPmInterfaceFormData(Model model);
    Model modelForTaskController(Model model);
    Model getPmDetailList(Model model, int pmId);
    Model getActivePmList(Model model, boolean active, boolean workspace);
    Model getPmUpdateForm(Model model, Pm pm, String ownerUsername);
    void pmInterfaceRegister(pmInterfaceRegisterForm pmInterfaceRegisterForm) throws IOException;
    void updatePmDetail(PmUpdateForm pmUpdateForm, Pm pm, String ownerUsername) throws IOException;
    String getPmOwnerUsername(int pmId);
    Pm getPm(int pmId);


}
