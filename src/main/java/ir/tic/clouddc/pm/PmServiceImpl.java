package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.center.Location;
import ir.tic.clouddc.center.LocationPmCatalog;
import ir.tic.clouddc.document.FileService;
import ir.tic.clouddc.document.MetaData;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.notification.NotificationService;
import ir.tic.clouddc.report.DailyReport;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@EnableScheduling
@Transactional
public class PmServiceImpl implements PmService {

    private static final List<Integer> locationTargetIdList = List.of(1, 2, 3, 4);

    private static final List<Integer> deviceTargetIdList = List.of(5, 6, 7, 8, 9);

    private final PmRepository pmRepository;
    private final PmInterfaceRepository pmInterfaceRepository;
    private final PmInterfaceCatalogRepository pmInterfaceCatalogRepository;
    private final PmDetailRepository pmDetailRepository;
    private final CenterService centerService;
    private final ResourceService resourceService;
    private final PersonService personService;
    private final NotificationService notificationService;
    private final LogService logService;
    private final FileService fileService;

    @Autowired
    PmServiceImpl(PmRepository pmRepository, PmInterfaceRepository pmInterfaceRepository, PmInterfaceCatalogRepository pmInterfaceCatalogRepository, PmDetailRepository pmDetailRepository, CenterService centerService, ResourceService resourceService, PersonService personService, NotificationService notificationService, LogService logService, FileService fileService) {
        this.pmRepository = pmRepository;
        this.pmInterfaceRepository = pmInterfaceRepository;
        this.pmInterfaceCatalogRepository = pmInterfaceCatalogRepository;
        this.pmDetailRepository = pmDetailRepository;
        this.centerService = centerService;
        this.resourceService = resourceService;
        this.personService = personService;
        this.notificationService = notificationService;
        this.logService = logService;
        this.fileService = fileService;

    }

    public void updateTodayPmList(DailyReport todayReport) {
        final List<PmInterfaceCatalog> todayCatalogList = pmInterfaceCatalogRepository.getTodayCatalogList(UtilService.getDATE(), true, true);
        List<Pm> activePmList = pmRepository.findAllByActive(true);
        List<PmDetail> pmDetailList = new ArrayList<>();

        delayCalculation(activePmList, pmDetailList);

        for (PmInterfaceCatalog catalog : todayCatalogList) {
            PmDetail pmDetail;
            pmDetail = pmRegister(catalog);
            pmDetail.setPersistence(logService.persistenceSetup(catalog.getDefaultPerson()));
            pmDetailList.add(pmDetail);

        }

        pmDetailRepository.saveAll(pmDetailList);
        notificationService.sendScheduleUpdateMessage("09127016653", "Scheduler successful @: " + LocalDateTime.now());
    }

    private PmDetail pmRegister(PmInterfaceCatalog catalog) {
        var pmInterface = pmInterfaceRepository.getReferenceById(catalog.getPmInterface().getId());
        if (!catalog.isActive()) {
            catalog.setActive(true);
        }
        if (pmInterface.isStatelessRecurring()) {
            catalog.setNextDueDate(UtilService.validateNextDue(UtilService.getDATE().plusDays(pmInterface.getPeriod())));
        }

        GeneralPmDetail generalPmDetail;
        if (catalog instanceof LocationPmCatalog locationPmCatalog) {
            GeneralLocationPm generalLocationPm = new GeneralLocationPm();
            generalLocationPm.setPmInterfaceCatalog(locationPmCatalog);
            generalLocationPm.setActive(true);
            generalLocationPm.setDueDate(UtilService.getDATE());
            generalLocationPm.setRegisterTime(UtilService.getTime());
            generalLocationPm.setDelay(0);

            generalPmDetail = new GeneralPmDetail();
            generalPmDetail.setPm(generalLocationPm);

        } else {
            GeneralDevicePm generalDevicePm = new GeneralDevicePm();
            generalDevicePm.setPmInterfaceCatalog(catalog);
            generalDevicePm.setActive(true);
            generalDevicePm.setDueDate(UtilService.getDATE());
            generalDevicePm.setRegisterTime(UtilService.getTime());
            generalDevicePm.setDelay(0);

            generalPmDetail = new GeneralPmDetail();
            generalPmDetail.setPm(generalDevicePm);
        }
        generalPmDetail.setDelay(0);
        generalPmDetail.setActive(true);
        generalPmDetail.setRegisterDate(UtilService.getDATE());
        generalPmDetail.setRegisterTime(UtilService.getTime());

        return generalPmDetail;
    }

    private void delayCalculation(List<Pm> currentPmList, List<PmDetail> pmDetailList) {
        if (!currentPmList.isEmpty()) {
            for (Pm pm : currentPmList) {
                var registerDateTime = LocalDateTime.of(pm.getDueDate(), pm.getRegisterTime());
                pm.setDelay((int) ChronoUnit.DAYS.between(LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()), registerDateTime));
                var activeTaskDetail = pm.getPmDetailList().stream().filter(PmDetail::isActive).findFirst().get();
                LocalDateTime assignedDateTime = LocalDateTime.of(activeTaskDetail.getRegisterDate(), activeTaskDetail.getRegisterTime());
                activeTaskDetail.setDelay((int) ChronoUnit.DAYS.between(LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()), assignedDateTime));
                pmDetailList.add(activeTaskDetail);
            }
        }
    }

    @Override
    public List<PmInterface> getPmInterfaceList() {
        return pmInterfaceRepository.findAll(Sort.by("category"));
    }

    @Override
    public List<Pm> getPmInterfacePmList(Integer pmInterfaceId, boolean active) {

        List<Pm> basePmList = pmRepository.fetchActivePmList(pmInterfaceId, active);
        setPmTransients(basePmList);

        return basePmList;
    }

    @Override
    public List<PmDetail> getPmDetail_2(Pm pm) {
        if (pm.getPmDetailList() != null) {
            log.info("getPmDetail 2");
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

        return fileService.getRelatedMetadataList(persistenceIdList, false);
    }

    @Override
    public boolean getPmDetail_4(PmDetail pmDetail) {
        return pmDetailFormViewPermission(pmDetail.getPersistence().getPerson().getUsername());
    }

    private void setPmTransients(List<Pm> basePm) {
        for (Pm pm : basePm) {
            pm.setPersianDueDate(UtilService.getFormattedPersianDate(pm.getDueDate()));
            if (pm.isActive()) {
                pm.setActivePersonName(pm.getPmDetailList().stream().filter(PmDetail::isActive).findFirst().get().getPersistence().getPerson().getName());
            } else {
                pm.setPersianFinishedDate(UtilService.getFormattedPersianDate(pm.getFinishedDate()));
                pm.setPersianFinishedDayTime(UtilService.getFormattedPersianDayTime(pm.getFinishedDate(), pm.getFinishedTime()));
            }
        }
    }

    private void setPmDetailTransients(List<PmDetail> pmDetailList) {
        for (PmDetail pmDetail : pmDetailList) {
            pmDetail.setPersianRegisterDate(UtilService.getFormattedPersianDate(pmDetail.getRegisterDate()));
            pmDetail.setPersianRegisterDayTime(UtilService.getFormattedPersianDayTime(pmDetail.getRegisterDate(), pmDetail.getRegisterTime()));
            if (!pmDetail.isActive()) {
                pmDetail.setPersianFinishedDate(UtilService.getFormattedPersianDate(pmDetail.getFinishedDate()));
                log.info(UtilService.getFormattedPersianDayTime(pmDetail.getFinishedDate(), pmDetail.getFinishedTime()));
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
        } else return currentPmDetailUsername.equals(personService.getCurrentUsername());
    }


    @Override
    @PreAuthorize("#pm.active == true  && (#ownerUsername == authentication.name || hasAnyAuthority('ADMIN', 'SUPERVISOR')) ")
    public void updatePm(PmUpdateForm pmUpdateForm, @Param("pm") Pm pm, @Param("ownerUsername") String ownerUsername) throws IOException {
        // PART 1. Pm detail update
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
        Persistence currentPmDetailPersistence = basePmDetail.getPersistence();

        var currentPerson = personService.getCurrentPerson();
        if (ownerUsername.equals(currentPerson.getUsername())) {
            basePmDetail.setDescription(pmUpdateForm.getDescription());
            if (pmUpdateForm.getFile() != null) {
                fileService.registerAttachment(pmUpdateForm.getFile(), currentPmDetailPersistence);
            }
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("PmUpdate"), currentPmDetailPersistence.getPerson(), currentPmDetailPersistence);
          /*  if (basePmDetail instanceof TemperaturePmDetail temperaturePmDetail) {
                temperaturePmDetail.setTemperatureValue(pmUpdateForm.getTemperatureValue());
            }*/
        } else {
            basePmDetail.setDescription("توسط مسئول مدیریت وظایف از کارتابل خارج شد.");
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("SupervisorPmTermination"), currentPerson, currentPmDetailPersistence);
            supervisorOperation(pmUpdateForm, currentPerson, pm);
        }
        pmDetailRepository.saveAndFlush(basePmDetail);

        log.info("Current pmDetail persisted");

        // PART 2. Pm Update
        if (pmUpdateForm.getActionType().equals(0)) {  //  End Pm
            log.info("Closing Pm....");
            endPm(pm);

        } else { //  Assign new PmDetail
            log.info("new General PmDetail");
            GeneralPmDetail pmDetail = new GeneralPmDetail();
            pmDetail.setPm(pm);
            assignNewPmDetail(pmDetail, pmUpdateForm.getActionType(), true);
        }
    }


    private PmDetail assignNewPmDetail(PmDetail pmDetail, Integer personId, boolean active) {
        var assigneePerson = personService.getPerson(personId);
        Persistence persistence = logService.persistenceSetup(assigneePerson);
        pmDetail.setPersistence(persistence);
        pmDetail.setRegisterDate(UtilService.getDATE());
        pmDetail.setRegisterTime(UtilService.getTime());
        pmDetail.setDelay(0);

        if (active) {
            pmDetail.setActive(true);
        } else {
            pmDetail.setActive(false);
            pmDetail.setFinishedDate(pmDetail.getRegisterDate());
            pmDetail.setFinishedTime(pmDetail.getRegisterTime());
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("PmUpdate"), assigneePerson, persistence);
        }

        // notificationService.sendActiveTaskAssignedMessage(assigneePerson.getAddress().getValue(), pmDetail.getPm().getCatalog().getPmInterface().getName(), pmDetail.getPm().getDelay(), LocalDateTime.of(pmDetail.getRegisterDate(), pmDetail.getRegisterTime()));

        return pmDetailRepository.save(pmDetail);
    }


    private void supervisorOperation(PmUpdateForm pmUpdateForm, Person currentPerson, Pm pm) throws IOException {
        GeneralPmDetail supervisorGeneralPmDetail = new GeneralPmDetail();
        supervisorGeneralPmDetail.setDescription(pmUpdateForm.getDescription());
        supervisorGeneralPmDetail.setPm(pm);
        var persistedDetail = assignNewPmDetail(supervisorGeneralPmDetail, currentPerson.getId(), false);
        if (pmUpdateForm.getFile() != null) {
            fileService.registerAttachment(pmUpdateForm.getFile(), persistedDetail.getPersistence());
        }
    }

    private Pm endPm(Pm pm) {
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
        catalog.setLastFinishedDate(pm.getFinishedDate());
        catalog.setLastFinishedTime(pm.getFinishedTime());
        catalog.setLastPmId(pm.getId());
     /*   if (pm instanceof TemperaturePm temperaturePm) {
            List<TemperaturePmDetail> temperaturePmDetailList = new ArrayList<>();
            for (PmDetail pmDetail : temperaturePm.getPmDetailList()) {
                temperaturePmDetailList.add((TemperaturePmDetail) pmDetail);
            }
            float sum = (float) temperaturePmDetailList.stream().filter(temperaturePmDetail -> temperaturePmDetail.getTemperatureValue() > 0.0f).mapToDouble(TemperaturePmDetail::getTemperatureValue).sum();
            int reportedTemperatures = (int) temperaturePmDetailList.stream().filter(temperaturePmDetail -> temperaturePmDetail.getTemperatureValue() > 0.0f).count();
            temperaturePm.setAverageDailyValue(sum / (float) reportedTemperatures);
        }*/

        return pmRepository.save(pm);
    }

    @Override
    public List<Person> getAssignPersonList(String pmOwnerUsername) {
        List<Person> assignPersonList;
        var currentUsername = personService.getCurrentUsername();
        if (pmOwnerUsername.equals(currentUsername)) { /// pmDetail Owner updates pm
            assignPersonList = personService.getPersonListExcept(List.of(pmOwnerUsername));
        } else {  /// supervisor updates pm
            assignPersonList = personService.getPersonListExcept(List.of(pmOwnerUsername, currentUsername));
        }
        return assignPersonList;
    }

    @Override
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
    public Location getReferencedLocation(Long locationId) throws SQLException {
        return centerService.getRefrencedLocation(locationId);
    }

    @Override
    public Device getDevice(Long deviceId) throws SQLException {
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
    public void registerNewCatalog(CatalogForm catalogForm, LocalDate validDate) throws SQLException {

        if (catalogForm.getLocationId() != null) {
            log.info(String.valueOf(catalogForm.getLocationId()));
            LocationPmCatalog locationPmCatalog = new LocationPmCatalog();
            locationPmCatalog.setLocation(centerService.getRefrencedLocation(catalogForm.getLocationId()));
            locationPmCatalog.setDefaultPerson(new Person(catalogForm.getDefaultPersonId()));
            locationPmCatalog.setPmInterface(new PmInterface(catalogForm.getPmInterfaceId()));
            locationPmCatalog.setEnabled(true);
            locationPmCatalog.setHistory(false);
            locationPmCatalog.setActive(false);
            locationPmCatalog.setNextDueDate(validDate);

            var newCatalog = pmInterfaceCatalogRepository.save(locationPmCatalog);
            if (newCatalog.getNextDueDate().isEqual(UtilService.getDATE())) {
                var pmDetail = pmRegister(newCatalog);
                pmDetail.setPersistence(logService.persistenceSetup(newCatalog.getDefaultPerson()));
                pmDetailRepository.saveAndFlush(pmDetail);
            }
        } else {
            DevicePmCatalog devicePmCatalog = new DevicePmCatalog();
            devicePmCatalog.setDevice(resourceService.getReferencedDevice(catalogForm.getDeviceId()));
            devicePmCatalog.setDefaultPerson(new Person(catalogForm.getDefaultPersonId()));
            devicePmCatalog.setPmInterface(new PmInterface(catalogForm.getPmInterfaceId()));
            devicePmCatalog.setEnabled(true);
            devicePmCatalog.setHistory(false);
            devicePmCatalog.setActive(false);
            devicePmCatalog.setNextDueDate(UtilService.validateNextDue(validDate));

            var newCatalog = pmInterfaceCatalogRepository.save(devicePmCatalog);
            if (newCatalog.getNextDueDate().isEqual(UtilService.getDATE())) {
                var pmDetail = pmRegister(newCatalog);
                pmDetail.setPersistence(logService.persistenceSetup(newCatalog.getDefaultPerson()));
                pmDetailRepository.saveAndFlush(pmDetail);
            }
        }
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
    public List<Pm> getActivePmList(boolean active, boolean workspace) {
        List<Pm> activePmList;
        if (workspace) {
            activePmList = pmDetailRepository.fetchWorkspacePmList(personService.getCurrentUsername(), active);

        } else {
            activePmList = pmDetailRepository.fetchActivePmList(active);
        }

        if (!activePmList.isEmpty()) {
            setPmTransients(activePmList);

            return activePmList.stream().sorted(Comparator.comparing(Pm::getDelay).reversed()).toList();
        }
        return activePmList;
    }

    @Override
    public Long getActivePmCount(Integer pmInterfaceId) {
        return pmRepository.countActiveByPmInterface(pmInterfaceId, true);
    }

    @Override
    @ModifyProtection
    public void pmInterfaceRegister(PmInterfaceRegisterForm pmInterfaceRegisterForm) throws IOException {
        PmInterface pmInterface;
        Persistence persistence;
        var currentPerson = personService.getCurrentPerson();

        if (pmInterfaceRegisterForm.isUpdate()) { ///// Modify Pm
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
            persistence = logService.persistenceSetup(currentPerson);
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("PmInterfaceRegister"), currentPerson, persistence);
            pmInterface.setPersistence(persistence);
        }

        pmInterface.setEnabled(pmInterfaceRegisterForm.isEnabled());
        pmInterface.setTitle(pmInterfaceRegisterForm.getTitle());
        pmInterface.setPeriod(pmInterfaceRegisterForm.getPeriod());
        pmInterface.setDescription(pmInterfaceRegisterForm.getDescription());
        pmInterface.setStatelessRecurring(pmInterfaceRegisterForm.isStatelessRecurring());
        if (pmInterfaceRegisterForm.getFile() != null) {
            fileService.registerAttachment(pmInterfaceRegisterForm.getFile(), persistence);
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


    @Override
    public Model modelForTaskController(Model model) {
        model.addAttribute("person", personService.getCurrentPerson());
        model.addAttribute("date", UtilService.getCurrentDate());

        return model;
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
