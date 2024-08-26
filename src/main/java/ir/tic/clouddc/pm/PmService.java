package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.document.MetaData;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.resource.Device;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PmService {

    String updateTodayPmList();

    List<PmInterface> getPmInterfaceList();

    List<Pm> getPmInterfacePmList(Integer pmInterfaceId, boolean active);

    List<PmDetail> getPmDetail_2(Pm pm);

    List<MetaData> getPmDetail_3(Pm pm);

    boolean getPmDetail_4(PmDetail pmDetail);

    List<Pm> getActivePmList(boolean active, boolean workspace);

    void pmInterfaceRegister(PmInterfaceRegisterForm pmInterfaceRegisterForm) throws IOException;

    void updatePm(PmUpdateForm pmUpdateForm, Pm pm, String ownerUsername) throws IOException;

    String getPmOwnerUsername(Long pmId, boolean active);

    Optional<Pm> getPm(Long pmId);

    List<Person> getAssignPersonList(String pmOwnerUsername);

    List<Person> getDefaultPersonList();

    List<PmInterface> getNonCatalogedPmInterfaceList(@Nullable Location location, @Nullable Device device);

    PmInterface getReferencedPmInterface(Integer pmInterfaceId) throws EntityNotFoundException;

    Location getReferencedLocation(Long locationId) throws SQLException;

    Integer registerNewCatalog(CatalogForm catalogForm, LocalDate validDate) throws SQLException;

    Device getDevice(Long deviceId) throws SQLException;

    Pm getRefrencedPm(Long pmId) throws SQLException;

    Long getPmInterfaceActivePmCount(Integer id);

    PmInterfaceCatalog getReferencedCatalog(Long catalogId);

    Long getCatalogActivePmCount(Long catalogId);

    List<Pm> getCatalogPmList(Long catalogId, boolean active);
}
