package ir.tic.clouddc.pm;

import ir.tic.clouddc.document.MetaData;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.report.DailyReport;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.List;

public interface PmService {
    void updateTodayPmList(DailyReport todayReport);
    List<PmInterface> getPmInterfaceList();
    List<Pm> getPmInterfacePmList(short pmInterfaceId, boolean active, int locationId);
    Model pmInterfaceEditFormData(Model model, short pmInterfaceId);
    Model getPmInterfaceFormData(Model model);
    Model modelForTaskController(Model model);
    Pm getPmDetail_1(int pmId);
    List<PmDetail> getPmDetail_2(Pm pm);

    List<MetaData> getPmDetail_3(Pm pm);
    boolean getPmDetail_4(PmDetail pmDetail);

    Model getActivePmList(Model model, boolean active, boolean workspace);
    PmUpdateForm getPmUpdateForm(Model model, Pm pm, String ownerUsername);
    void pmInterfaceRegister(pmInterfaceRegisterForm pmInterfaceRegisterForm) throws IOException;
    void updatePm(PmUpdateForm pmUpdateForm, Pm pm, String ownerUsername) throws IOException;
    String getPmOwnerUsername(int pmId);
    Pm getPm(int pmId);

    List<Person> getAssignPersonList(String pmOwnerUsername);
}
