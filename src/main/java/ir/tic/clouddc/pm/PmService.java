package ir.tic.clouddc.pm;

import ir.tic.clouddc.document.MetaData;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.report.DailyReport;
import jakarta.annotation.Nullable;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.List;

public interface PmService {
    void updateTodayPmList(DailyReport todayReport);

    List<PmInterface> getPmInterfaceList();

    List<Pm> getPmInterfacePmList(short pmInterfaceId, boolean active, @Nullable Integer locationId);

    PmInterfaceRegisterForm pmInterfaceEditFormData(short pmInterfaceId);

    List<PmCategory> getPmInterfaceFormData();

    Model modelForTaskController(Model model);

    Pm getPmDetail_1(int pmId);

    List<PmDetail> getPmDetail_2(Pm pm);

    List<MetaData> getPmDetail_3(Pm pm);

    boolean getPmDetail_4(PmDetail pmDetail);

    List<Pm> getActivePmList(boolean active, boolean workspace);

    PmUpdateForm getPmUpdateForm(Pm pm, String ownerUsername);

    void pmInterfaceRegister(PmInterfaceRegisterForm pmInterfaceRegisterForm) throws IOException;

    void pmUpdate(PmUpdateForm pmUpdateForm, Pm pm, String ownerUsername) throws IOException;

    String getPmOwnerUsername(int pmId);

    Pm getPm(int pmId);

    List<Person> getAssignPersonList(String pmOwnerUsername);
}
