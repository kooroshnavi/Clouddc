package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.center.LocationPmCatalog;
import ir.tic.clouddc.document.FileService;
import ir.tic.clouddc.document.MetaData;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.resource.Device;
import ir.tic.clouddc.resource.DevicePmCatalog;
import ir.tic.clouddc.resource.ResourceService;
import ir.tic.clouddc.security.ModifyProtection;
import ir.tic.clouddc.utils.UtilService;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@Transactional
public class PmServiceImpl implements PmService {

    private final PmRepository pmRepository;
    private final PmInterfaceRepository pmInterfaceRepository;
    private final PmInterfaceCatalogRepository pmInterfaceCatalogRepository;
    private final PmDetailRepository pmDetailRepository;
    private final CenterService centerService;
    private final ResourceService resourceService;
    private final PersonService personService;
    private final LogService logService;
    private final FileService fileService;

    @Autowired
    PmServiceImpl(PmRepository pmRepository, PmInterfaceRepository pmInterfaceRepository, PmInterfaceCatalogRepository pmInterfaceCatalogRepository, PmDetailRepository pmDetailRepository, CenterService centerService, ResourceService resourceService, PersonService personService, LogService logService, FileService fileService) {
        this.pmRepository = pmRepository;
        this.pmInterfaceRepository = pmInterfaceRepository;
        this.pmInterfaceCatalogRepository = pmInterfaceCatalogRepository;
        this.pmDetailRepository = pmDetailRepository;
        this.centerService = centerService;
        this.resourceService = resourceService;
        this.personService = personService;
        this.logService = logService;
        this.fileService = fileService;
    }

    public String updateTodayPmList() {
        final List<PmInterfaceCatalog> todayCatalogList = pmInterfaceCatalogRepository.getTodayCatalogList
                (UtilService.getDATE(), true, true);

        List<Pm> activePmList = pmRepository.findAllByActive(true);

        delayCalculation(activePmList);
        List<Pm> totalPmList = new ArrayList<>(activePmList);

        if (!todayCatalogList.isEmpty()) {
            for (PmInterfaceCatalog catalog : todayCatalogList) {
                Pm todayPm = pmRegister(catalog);
                totalPmList.add(todayPm);
            }
        }
        if (!totalPmList.isEmpty()) {
            pmRepository.saveAll(totalPmList);

            return "Pm Scheduler Successful @: " + LocalDateTime.now();
        }
        return "No pm schedules for today." + LocalDateTime.now();
    }

    private Pm pmRegister(PmInterfaceCatalog catalog) {
        var pmInterface = pmInterfaceRepository.getReferenceById(catalog.getPmInterface().getId());
        if (!catalog.isActive()) {
            catalog.setActive(true);
        }
        if (pmInterface.isStatelessRecurring()) {
            catalog.setNextDueDate(UtilService.validateNextDue(UtilService.getDATE().plusDays(pmInterface.getPeriod())));
        }

        catalog.getDefaultPerson().setWorkspaceSize(catalog.getDefaultPerson().getWorkspaceSize() + 1);

        Pm pm;
        if (catalog instanceof LocationPmCatalog) {
            pm = new GeneralLocationPm();
        } else {
            pm = new GeneralDevicePm();
        }

        pm.setPmInterfaceCatalog(catalog);
        pm.setActive(true);
        pm.setDueDate(UtilService.getDATE());
        pm.setRegisterTime(UtilService.getTime());
        pm.setDelay(0);

        GeneralPmDetail generalPmDetail = new GeneralPmDetail();
        generalPmDetail.setDelay(0);
        generalPmDetail.setActive(true);
        generalPmDetail.setRegisterDate(UtilService.getDATE());
        generalPmDetail.setRegisterTime(UtilService.getTime());
        generalPmDetail.setPersistence(logService.persistenceSetup(catalog.getDefaultPerson(), "Pm"));
        generalPmDetail.setPm(pm);
        pm.setPmDetailList(List.of(generalPmDetail));

        return pm;
    }

    private void delayCalculation(List<Pm> activePmList) {
        if (!activePmList.isEmpty()) {
            for (Pm pm : activePmList) {
                var registerDateTime = LocalDateTime.of(pm.getDueDate(), pm.getRegisterTime());
                pm.setDelay((int) ChronoUnit.DAYS.between(registerDateTime, LocalDateTime.of(UtilService.getDATE(), UtilService.getTime())));
                var activeTaskDetail = pm.getPmDetailList().stream().filter(PmDetail::isActive).findFirst();
                if (activeTaskDetail.isPresent()) {
                    LocalDateTime assignedDateTime = LocalDateTime.of(activeTaskDetail.get().getRegisterDate(), activeTaskDetail.get().getRegisterTime());
                    pm.getPmDetailList()
                            .stream()
                            .filter(PmDetail::isActive)
                            .findFirst()
                            .ifPresent(pmDetail -> pmDetail.setDelay((((int) ChronoUnit.DAYS.between(assignedDateTime, LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()))))));
                }
            }
        }
    }

    @Override
    public List<PmInterface> getPmInterfaceList() {

        return pmInterfaceRepository.findAll();
    }

    @Override
    public List<Pm> getPmInterfacePmList(Integer pmInterfaceId, boolean active) {
        List<Pm> basePmList = pmRepository.fetchActivePmList(pmInterfaceId, active);
        setPmTransients(basePmList);

        return basePmList;
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISOR', 'OPERATOR', 'MANAGER')")
    public List<PmDetail> getPmDetail_2(Pm pm) {
        if (pm.getPmDetailList() != null) {
            var sortedPmDetailList = pm
                    .getPmDetailList().stream()
                    .sorted(Comparator.comparing(PmDetail::getId).reversed())
                    .toList();
            setPmDetailTransients(sortedPmDetailList);

            return sortedPmDetailList;
        }

        return new ArrayList<>();
    }

    @Override
    public List<MetaData> getPmDetail_3(Pm pm) {
        List<Long> persistenceIdList = pmDetailRepository.getPersistenceIdList(pm.getId());
        persistenceIdList.add(pm.getPmInterfaceCatalog().getPmInterface().getPersistence().getId());
        var stringPmInterfaceId = pm.getPmInterfaceCatalog().getPmInterface().getId().toString();
        List<Long> pmInterfaceSupervisorEditFilePersistenceIdList = logService.getSupervisorPmInterfaceEditFilePersiscentceIdList(stringPmInterfaceId);
        persistenceIdList.addAll(pmInterfaceSupervisorEditFilePersistenceIdList);

        return fileService.getRelatedMetadataList(persistenceIdList, false);
    }

    @Override
    public boolean getPmDetail_4(PmDetail pmDetail) {
        return pmDetailFormViewPermission(pmDetail.getPersistence().getPerson().getUsername());
    }

    private void setPmTransients(List<Pm> pmList) {
        if (!pmList.isEmpty()) {
            for (Pm pm : pmList) {
                pm.setPersianDueDate(UtilService.getFormattedPersianDate(pm.getDueDate()));
                if (pm.isActive()) {
                    pm
                            .getPmDetailList()
                            .stream()
                            .filter(PmDetail::isActive)
                            .findFirst()
                            .ifPresent(pmDetail -> pmDetail.getPm().setActivePersonName(pmDetail.getPersistence().getPerson().getName()));
                } else {
                    pm.setPersianFinishedDate(UtilService.getFormattedPersianDate(pm.getFinishedDate()));
                    pm.setPersianFinishedDayTime(UtilService.getFormattedPersianDayTime(pm.getFinishedDate(), pm.getFinishedTime()));
                }
            }
        }
    }

    private void setPmDetailTransients(List<PmDetail> pmDetailList) {
        for (PmDetail pmDetail : pmDetailList) {
            pmDetail.setPersianRegisterDate(UtilService.getFormattedPersianDate(pmDetail.getRegisterDate()));
            pmDetail.setPersianRegisterDayTime(UtilService.getFormattedPersianDayTime(pmDetail.getRegisterDate(), pmDetail.getRegisterTime()));
            if (!pmDetail.isActive()) {
                pmDetail.setPersianFinishedDate(UtilService.getFormattedPersianDate(pmDetail.getFinishedDate()));
                pmDetail.setPersianFinishedDayTime(UtilService.getFormattedPersianDayTime(pmDetail.getFinishedDate(), pmDetail.getFinishedTime()));
            }
        }
    }

    private boolean pmDetailFormViewPermission(String currentPmDetailUsername) {
        var currentPersonRoleList = personService.getCurrentPersonRoleList();
        if (currentPersonRoleList
                .stream()
                .anyMatch(grantedAuthority
                        -> grantedAuthority.getAuthority().equals("ADMIN")
                        || grantedAuthority.getAuthority().equals("SUPERVISOR"))) {
            return true;
        } else
            return currentPmDetailUsername.equals(personService.getCurrentUsername());
    }


    @Override
    @PreAuthorize("#pm.active == true  && (#ownerUsername == authentication.name || hasAnyAuthority('ADMIN', 'SUPERVISOR')) ")
    public void updatePm(PmUpdateForm pmUpdateForm, @Param("pm") Pm pm, @Param("ownerUsername") String ownerUsername) throws IOException {
        // All operations run on a single Pm
        // PART 1. PmDetail update
        PmDetail basePmDetail;
        var optionalPmDetail = pmDetailRepository.findByPmIdAndActive(pm.getId(), true);
        if (optionalPmDetail.isPresent()) {
            basePmDetail = optionalPmDetail.get();
        } else {
            throw new NoSuchElementException();
        }

        basePmDetail.setFinishedDate(UtilService.getDATE());
        basePmDetail.setFinishedTime(UtilService.getTime());
        basePmDetail.setActive(false);
        var pmDetailPerson = basePmDetail.getPersistence().getPerson();
        pmDetailPerson.setWorkspaceSize(pmDetailPerson.getWorkspaceSize() - 1);
        Persistence attachmentPersistence;

        var currentPerson = personService.getCurrentPerson();
        if (ownerUsername.equals(currentPerson.getUsername())) { // Routine Operation
            attachmentPersistence = basePmDetail.getPersistence();
            basePmDetail.setDescription(pmUpdateForm.getDescription());
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("PmUpdate"), basePmDetail.getPersistence().getPerson(), basePmDetail.getPersistence());
        } else { // Supervisor Operation
            basePmDetail.setDescription("توسط مسئول مدیریت وظایف از کارتابل خارج شد.");
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("SupervisorPmTermination"), currentPerson, basePmDetail.getPersistence());
            var supervisorPmDetail = setupNewPmDetail(currentPerson.getId(), false);
            supervisorPmDetail.setDescription(pmUpdateForm.getDescription());
            supervisorPmDetail.setPm(pm);
            pm.getPmDetailList().add(supervisorPmDetail);
            attachmentPersistence = supervisorPmDetail.getPersistence();
        }

        // PART 2. Pm Update
        if (pmUpdateForm.getActionType().equals(0)) {  //  End Pm
            endPm(pm);

        } else { //  Assign new PmDetail
            var nextPmDetail = setupNewPmDetail(pmUpdateForm.getActionType(), true);
            nextPmDetail.setPm(pm);
            pm.getPmDetailList().add(nextPmDetail);
        }

        pmRepository.saveAndFlush(pm);

        if (pmUpdateForm.getFile() != null) {
            fileService.registerAttachment(pmUpdateForm.getFile(), attachmentPersistence);
        }
    }

    private PmDetail setupNewPmDetail(Integer personId, boolean active) {
        GeneralPmDetail generalPmDetail = new GeneralPmDetail();
        var assigneePerson = personService.getReferencedPerson(personId);
        Persistence persistence = logService.persistenceSetup(assigneePerson, "Pm");
        generalPmDetail.setPersistence(persistence);
        generalPmDetail.setRegisterDate(UtilService.getDATE());
        generalPmDetail.setRegisterTime(UtilService.getTime());
        generalPmDetail.setDelay(0);

        if (active) {
            generalPmDetail.setActive(true);
            assigneePerson.setWorkspaceSize(assigneePerson.getWorkspaceSize() + 1);
        } else {
            generalPmDetail.setActive(false);
            generalPmDetail.setFinishedDate(generalPmDetail.getRegisterDate());
            generalPmDetail.setFinishedTime(generalPmDetail.getRegisterTime());
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("PmUpdate"), assigneePerson, persistence);
        }

        return generalPmDetail;
    }

    private void endPm(Pm pm) {
        pm.setFinishedDate(UtilService.getDATE());
        pm.setFinishedTime(UtilService.getTime());
        var catalog = pm.getPmInterfaceCatalog();
        if (catalog.getPmList().stream().filter(Pm::isActive).count() == 1) {
            catalog.setActive(false);
        }

        pm.setActive(false);

        var pmInterface = catalog.getPmInterface();
        if (!pmInterface.isStatelessRecurring()) {
            catalog.setNextDueDate(UtilService.validateNextDue(UtilService.getDATE().plusDays(pmInterface.getPeriod())));
        }

        if (!catalog.isHistory()) {
            catalog.setHistory(true);
        }
        catalog.setLastPmId(pm.getId());
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISOR', 'OPERATOR')")
    public List<Person> getAssignPersonList(String pmOwnerUsername) {
        List<Person> assignPersonList;
        var currentUsername = personService.getCurrentUsername();
        if (pmOwnerUsername.equals(currentUsername)) { /// pmDetail Owner updates pm
            assignPersonList = personService.getPersonListExcept(List.of(pmOwnerUsername));
        } else {  /// supervisor updates pm
            assignPersonList = personService.getPersonListExcept(List.of(pmOwnerUsername, currentUsername));
        }

        return assignPersonList.stream().sorted(Comparator.comparing(Person::getWorkspaceSize)).toList();
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISOR', 'OPERATOR')")
    public List<Person> getDefaultPersonList() {
        return personService.getDefaultAssgineeList();
    }

    @Override
    public List<PmInterface> getNonCatalogedPmInterfaceList(@Nullable Location location, @Nullable Device device) {
        List<PmInterface> pmInterfaceList;
        List<Integer> catalogedInterface;

        if (!Objects.equals(location, null)) {
            List<Integer> targetIdList = List.of(location.getLocationCategory().getCategoryId(), 4);
            if (location.getLocationPmCatalogList().isEmpty()) {
                pmInterfaceList = pmInterfaceRepository.fetchPmInterfaceListNotInCatalogList(true, List.of(0), targetIdList);
            } else {
                catalogedInterface = location
                        .getLocationPmCatalogList()
                        .stream()
                        .map(LocationPmCatalog::getPmInterface)
                        .map(PmInterface::getId)
                        .toList();

                pmInterfaceList = pmInterfaceRepository.fetchPmInterfaceListNotInCatalogList(true, catalogedInterface, targetIdList);
            }

            return pmInterfaceList;

        } else if (!Objects.equals(device, null)) {
            List<Integer> targetIdList = List.of(device.getDeviceCategory().getCategoryId(), 9);
            if (device.getDevicePmCatalogList().isEmpty()) {
                pmInterfaceList = pmInterfaceRepository.fetchPmInterfaceListNotInCatalogList(true, List.of(0), targetIdList);
            } else {
                catalogedInterface = device
                        .getDevicePmCatalogList()
                        .stream()
                        .map(DevicePmCatalog::getPmInterface)
                        .map(PmInterface::getId)
                        .toList();

                pmInterfaceList = pmInterfaceRepository.fetchPmInterfaceListNotInCatalogList(true, catalogedInterface, targetIdList);
            }
            return pmInterfaceList;

        }

        throw new NoSuchElementException();
    }

    @Override
    public PmInterface getReferencedPmInterface(Integer pmInterfaceId) throws EntityNotFoundException {

        return pmInterfaceRepository.getReferenceById(pmInterfaceId);
    }

    @Override
    public Location getReferencedLocation(Long locationId) {
        return centerService.getRefrencedLocation(locationId);
    }

    @Override
    public Device getDevice(Long deviceId) {
        return resourceService.getReferencedDevice(deviceId);
    }

    @Override
    public Pm getRefrencedPm(Long pmId) throws SQLException {
        Pm pm;
        try {
            pm = pmRepository.getReferenceById(pmId);
        } catch (Exception e) {
            throw new SQLException(e);
        }
        setPmTransients(List.of(pm));

        return pm;
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISOR', 'OPERATOR')")
    public Integer registerNewCatalog(CatalogForm catalogForm, LocalDate validDate) {
        Persistence persistence;
        PmInterfaceCatalog newCatalog;
        var currentPerson = personService.getCurrentPerson();

        if (catalogForm.getPmInterfaceCatalogId() != null) {   // update catalog
            newCatalog = pmInterfaceCatalogRepository.getReferenceById(catalogForm.getPmInterfaceCatalogId());
            persistence = newCatalog.getPersistence();
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("CatalogUpdate"), currentPerson, persistence);
            newCatalog.setEnabled(catalogForm.isEnabled());
            newCatalog.setDefaultPerson(personService.getReferencedPerson(catalogForm.getDefaultPersonId()));
            newCatalog.setNextDueDate(UtilService.validateNextDue(validDate));

        } else {
            if (catalogForm.getLocationId() != null) { // Register location catalog
                LocationPmCatalog locationPmCatalog = new LocationPmCatalog();
                locationPmCatalog.setLocation(centerService.getRefrencedLocation(catalogForm.getLocationId()));
                locationPmCatalog.setDefaultPerson(personService.getReferencedPerson(catalogForm.getDefaultPersonId()));
                locationPmCatalog.setPmInterface(getReferencedPmInterface(catalogForm.getPmInterfaceId()));
                locationPmCatalog.setEnabled(true);
                locationPmCatalog.setHistory(false);
                locationPmCatalog.setActive(false);
                locationPmCatalog.setNextDueDate(UtilService.validateNextDue(validDate));
                persistence = logService.newPersistenceInitialization("CatalogRegister", personService.getCurrentPerson(), "LocationPmCatalog");
                locationPmCatalog.setPersistence(persistence);

                newCatalog = locationPmCatalog;

            } else { // Register Device catalog
                DevicePmCatalog devicePmCatalog = new DevicePmCatalog();
                devicePmCatalog.setDevice(resourceService.getReferencedDevice(catalogForm.getDeviceId()));
                devicePmCatalog.setDefaultPerson(personService.getReferencedPerson(catalogForm.getDefaultPersonId()));
                devicePmCatalog.setPmInterface(getReferencedPmInterface(catalogForm.getPmInterfaceId()));
                devicePmCatalog.setEnabled(true);
                devicePmCatalog.setHistory(false);
                devicePmCatalog.setActive(false);
                devicePmCatalog.setNextDueDate(UtilService.validateNextDue(validDate));
                persistence = logService.newPersistenceInitialization("CatalogRegister", personService.getCurrentPerson(), "DevicePmCatalog");
                devicePmCatalog.setPersistence(persistence);

                newCatalog = devicePmCatalog;
            }
        }
        if (newCatalog.getNextDueDate().isEqual(UtilService.getDATE())) {
            var todayPm = pmRegister(newCatalog);
            if (newCatalog.getPmList() == null) {
                newCatalog.setPmList(List.of(todayPm));
            } else {
                newCatalog.getPmList().add(todayPm);
            }
        }
        var catalog = pmInterfaceCatalogRepository.save(newCatalog);

        return catalog.getPmInterface().getId();
    }

    @Override
    @PreAuthorize("#active")
    public String getPmOwnerUsername(Long pmId, @Param("active") boolean active) {
        return pmDetailRepository.fetchPmOwnerUsername(pmId, true);
    }

    @Override
    public Optional<Pm> getPm(Long pmId) {
        return pmRepository.findById(pmId);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISOR', 'OPERATOR', 'MANAGER')")
    public List<Pm> getActivePmList(boolean workspace, @Nullable Integer personId) {
        List<Pm> activePmList;
        if (workspace) {
            if (personId == null) {
                activePmList = pmDetailRepository.fetchWorkspacePmList(personService.getCurrentUsername(), true);
            } else {
                activePmList = pmDetailRepository.fetchWorkspacePmList(personService.getReferencedPerson(personId).getUsername(), true);
            }
        } else {
            activePmList = pmDetailRepository.fetchActivePmList(true);
        }

        if (!activePmList.isEmpty()) {
            setPmTransients(activePmList);

            return activePmList
                    .stream()
                    .sorted(Comparator.comparing(Pm::getDelay).reversed())
                    .toList();
        }
        return activePmList;
    }

    @Override
    public Long getPmInterfaceActivePmCount(Integer pmInterfaceId) {
        return pmRepository.countActiveByPmInterface(pmInterfaceId, true);
    }

    @Override
    public PmInterfaceCatalog getReferencedCatalog(Long catalogId) throws EntityNotFoundException {
        return pmInterfaceCatalogRepository.getReferenceById(catalogId);
    }

    @Override
    public Long getCatalogActivePmCount(Long catalogId) {
        return pmRepository.getCatalogActivePmCount(catalogId, true);
    }

    @Override
    public List<Pm> getCatalogPmList(Long catalogId, boolean active) {
        List<Pm> pmList = pmRepository.fetchActiveCatalogPmList(catalogId, active);
        setPmTransients(pmList);

        return pmList;
    }

    @Override
    @ModifyProtection
    public void pmInterfaceRegister(PmInterfaceRegisterForm pmInterfaceRegisterForm) throws IOException {
        PmInterface pmInterface;
        Persistence persistence;
        var currentPerson = personService.getCurrentPerson();

        if (pmInterfaceRegisterForm.isUpdate()) { ///// Modify PmInterface
            pmInterface = pmInterfaceRepository.getReferenceById(pmInterfaceRegisterForm.getPmInterfaceId());
            persistence = pmInterface.getPersistence();
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("PmInterfaceUpdate"), currentPerson, persistence);
            updateCatalog(pmInterface, pmInterfaceRegisterForm);
        } else {    // New Pm
            pmInterface = new PmInterface();
            pmInterface.setTarget(pmInterfaceRegisterForm.getTarget());
            pmInterface.setCategoryId(UtilService.PM_CATEGORY_ID.get(pmInterfaceRegisterForm.getTarget()));
            pmInterface.setCategory(UtilService.PM_CATEGORY.get(pmInterface.getCategoryId()));
            pmInterface.setGeneralPm(true);
            persistence = logService.newPersistenceInitialization("PmInterfaceRegister", personService.getCurrentPerson(), "PmInterfaceRegister");
            pmInterface.setPersistence(persistence);
        }

        pmInterface.setEnabled(pmInterfaceRegisterForm.isEnabled());
        pmInterface.setTitle(pmInterfaceRegisterForm.getTitle());
        pmInterface.setPeriod(pmInterfaceRegisterForm.getPeriod());
        pmInterface.setDescription(pmInterfaceRegisterForm.getDescription());
        pmInterface.setStatelessRecurring(false);
        if (pmInterfaceRegisterForm.getFile() != null) {
            if (Objects.equals(currentPerson.getId(), persistence.getPerson().getId())) {
                fileService.registerAttachment(pmInterfaceRegisterForm.getFile(), persistence);
            } else {
                logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("PmInterfaceUpdateSupervisorFile"), currentPerson, persistence);
                var newPersistence = logService.newPersistenceInitialization(pmInterface.getId().toString(), personService.getCurrentPerson(), "Pm-File");
                fileService.registerAttachment(pmInterfaceRegisterForm.getFile(), newPersistence);
            }
        }
        pmInterfaceRepository.saveAndFlush(pmInterface);
    }

    private void updateCatalog(PmInterface pmInterface, PmInterfaceRegisterForm pmInterfaceRegisterForm) {
        if (pmInterface.isEnabled() && !pmInterfaceRegisterForm.isEnabled()) {
            var activePmCount = pmRepository.countActiveByPmInterface(pmInterface.getId(), true);
            if (activePmCount != 0) {
                throw new AccessDeniedException("PmInterface has at least one active catalog");
            } else {
                pmInterfaceCatalogRepository.disableCatalogByPmInterface(pmInterface.getId(), false);
            }
        } else if (!pmInterface.isEnabled() && pmInterfaceRegisterForm.isEnabled()) {
            pmInterfaceCatalogRepository.enableCatalogByPmInterface(pmInterface.getId(), true, UtilService.validateNextDue(UtilService.getDATE().plusDays(1)));
        }
    }


    /*
    @Override
    public long getFinishedTaskCount() {
        return taskRepository.getTaskCountByActivation(false);
    }

    @Override
    public long getOnTimeTaskCount() {
        return taskRepository.getDelayedActiveTaskCount(0, false);
    }

    @Override
    public long getActiveTaskCount() {
        return taskRepository.getTaskCountByActivation(true);
    }

    @Override
    public int getWeeklyFinishedPercentage() {
        DecimalFormat decimalFormat = new DecimalFormat("##");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        var percent = (float) taskRepository.getWeeklyFinishedTaskCount(UtilService.getDATE().minusDays(7), false) / getFinishedTaskCount() * 100;
        var formatted = decimalFormat.format(percent);
        return Integer.parseInt(formatted);
    }

    @Override
    public int getActiveDelayedPercentage() {
        DecimalFormat decimalFormat = new DecimalFormat("##");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
        var percent = (float) taskRepository.getDelayedActiveTaskCount(0, true) / taskRepository.getTaskCountByActivation(true) * 100;
        log.info(String.valueOf(percent));
        var formatted = decimalFormat.format(percent);
        return Integer.parseInt(formatted);
    }
*/
}
