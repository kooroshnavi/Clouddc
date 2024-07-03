package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.center.LocationPmCatalog;
import ir.tic.clouddc.document.FileService;
import ir.tic.clouddc.document.MetaData;
import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.notification.NotificationService;
import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.report.DailyReport;
import ir.tic.clouddc.security.ModifyProtection;
import ir.tic.clouddc.utils.UtilService;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@EnableScheduling
@Transactional
public class PmServiceImpl implements PmService {

    private final PmRepository pmRepository;
    private final PmInterfaceRepository pmInterfaceRepository;
    private final PmDetailRepository pmDetailRepository;
    private final PmCategoryRepository pmCategoryRepository;
    private final CenterService centerService;
    private final PersonService personService;
    private final NotificationService notificationService;
    private final LogService logService;
    private final FileService fileService;

    @Autowired
    PmServiceImpl(PmRepository pmRepository, PmInterfaceRepository pmInterfaceRepository, PmDetailRepository pmDetailRepository, PmCategoryRepository pmCategoryRepository, CenterService centerService, PersonService personService, NotificationService notificationService, LogService logService, FileService fileService) {
        this.pmRepository = pmRepository;
        this.pmInterfaceRepository = pmInterfaceRepository;
        this.pmDetailRepository = pmDetailRepository;
        this.pmCategoryRepository = pmCategoryRepository;
        this.centerService = centerService;
        this.personService = personService;
        this.notificationService = notificationService;
        this.logService = logService;
        this.fileService = fileService;

    }

    public void updateTodayPmList(DailyReport todayReport) {
        final List<LocationPmCatalog> todayCatalogList = centerService.getTodayCatalogList(UtilService.getDATE());
        List<Pm> activePmList = pmRepository.findAllByActive(true);
        List<PmDetail> pmDetailList = new ArrayList<>();

        delayCalculation(activePmList, pmDetailList);

        for (LocationPmCatalog catalog : todayCatalogList) {
            PmDetail pmDetail;
            var pmInterface = catalog.getPmInterface();
            if (pmInterface.isEnabled()) {
                pmDetail = pmRegister(catalog);
                if (!pmInterface.isActive()) {
                    pmInterface.setActive(true);
                }
                if (pmInterface.isStatelessRecurring()) {
                    centerService.updateCatalogDueDate(pmInterface, catalog.getLocation());
                }
                pmDetail.setPersistence(logService.persistenceSetup(catalog.getDefaultPerson()));
                pmDetailList.add(pmDetail);
            }
        }

        pmDetailRepository.saveAll(pmDetailList);
        notificationService.sendScheduleUpdateMessage("09127016653", "Scheduler successful @: " + LocalDateTime.now());
    }

    private PmDetail pmRegister(LocationPmCatalog catalog) {
        if (catalog.getPmInterface().isGeneralPm()) {
            GeneralPm generalPm = new GeneralPm();
            generalPm.setPmInterface(catalog.getPmInterface());
            generalPm.setLocation(catalog.getLocation());
            generalPm.setActive(true);
            generalPm.setDueDate(UtilService.getDATE());
            generalPm.setDelay(0);

            GeneralPmDetail generalPmDetail = new GeneralPmDetail();
            generalPmDetail.setPm(generalPm);
            generalPmDetail.setDelay(0);
            generalPmDetail.setActive(true);
            generalPmDetail.setRegisterDate(UtilService.getDATE());
            generalPmDetail.setRegisterTime(UtilService.getTime());

            return generalPmDetail;
        } else {
            TemperaturePm temperaturePm = new TemperaturePm();
            temperaturePm.setPmInterface(catalog.getPmInterface());
            temperaturePm.setLocation(catalog.getLocation());
            temperaturePm.setActive(true);
            temperaturePm.setDueDate(UtilService.getDATE());
            temperaturePm.setDelay(0);

            TemperaturePmDetail temperaturePmDetail = new TemperaturePmDetail();
            temperaturePmDetail.setPm(temperaturePm);
            temperaturePmDetail.setDelay(0);
            temperaturePmDetail.setActive(true);
            temperaturePmDetail.setRegisterDate(UtilService.getDATE());
            temperaturePmDetail.setRegisterTime(UtilService.getTime());

            return temperaturePmDetail;

        }
    }

    private void delayCalculation(List<Pm> currentPmList, List<PmDetail> pmDetailList) {
        if (!currentPmList.isEmpty()) {
            for (Pm pm : currentPmList) {
                var delay = pm.getDelay();
                delay += 1;
                pm.setDelay(delay);
                var activeTaskDetail = pm.getPmDetailList().stream().filter(PmDetail::isActive).findFirst().get();
                LocalDateTime assignedDateTime = LocalDateTime.of(activeTaskDetail.getRegisterDate(), activeTaskDetail.getRegisterTime());
                activeTaskDetail.setDelay((int) ChronoUnit.DAYS.between(LocalDateTime.of(UtilService.getDATE(), UtilService.getTime()), assignedDateTime));
                pmDetailList.add(activeTaskDetail);
            }
        }
    }

    @Override
    public List<PmInterface> getPmInterfaceList() {
        return pmInterfaceRepository.findAll(Sort.by("general"));
    }

    @Override
    public List<Pm> getPmInterfacePmList(short pmInterfaceId, boolean active, @Nullable Integer locationId) {
        PmInterface pmInterface;
        var optionalPmInterface = pmInterfaceRepository.findById(pmInterfaceId);
        if (optionalPmInterface.isPresent()) {
            pmInterface = optionalPmInterface.get();
        } else {
            throw new NoSuchElementException();
        }

        List<Pm> basePmList = pmRepository.findAllByPmInterfaceAndActiveAndLocationId(pmInterface, active, locationId);

        setPmTransients(basePmList);

        return basePmList.stream().sorted(Comparator.comparing(Pm::getDueDate).reversed()).toList();
    }

    @Override
    public Pm getPmDetail_1(int pmId) {
        /// Get pm and its interface
        var optionalPm = pmRepository.findById(pmId);
        if (optionalPm.isPresent()) {
            setPmTransients(List.of(optionalPm.get()));
            return optionalPm.get();
        } else {
            throw new NoSuchElementException("The specified Pm does not exist.");
        }
    }

    @Override
    public List<PmDetail> getPmDetail_2(Pm pm) {
        var sortedPmDetailList = pm.getPmDetailList().stream().sorted(Comparator.comparing(PmDetail::getId).reversed()).toList();
        setPmDetailTransients(sortedPmDetailList);
        return sortedPmDetailList;
    }

    @Override
    public List<MetaData> getPmDetail_3(Pm pm) {
        List<Long> persistenceIdList = pmDetailRepository.getPersistenceIdList(pm.getId());
        return fileService.getRelatedMetadataList(persistenceIdList);
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
                pmDetail.setPersianFinishedDayTime(UtilService.getFormattedPersianDayTime(pmDetail.getFinishedDate(), pmDetail.getFinishedTime()));
            }
        }
    }

    private boolean pmDetailFormViewPermission(String currentPmDetailUsername) {
        List<GrantedAuthority> supervisorRoleList = List.of(new SimpleGrantedAuthority("SUPERVISOR"), new SimpleGrantedAuthority("ADMIN"));
        if (personService.getCurrentPersonRoleList().contains((GrantedAuthority) supervisorRoleList)) {
            return true;
        } else return currentPmDetailUsername.equals(personService.getCurrentUsername());
    }


    @Override
    @PreAuthorize(" pm.active == true  && (ownerUsername == authentication.name || hasAnyAuthority('ADMIN', 'SUPERVISOR')) ")
    public void pmUpdate(PmUpdateForm pmUpdateForm, Pm pm, String ownerUsername) throws IOException {
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
            fileService.checkAttachment(pmUpdateForm.getFile(), currentPmDetailPersistence);
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), '1', currentPmDetailPersistence.getPerson(), currentPmDetailPersistence);
            if (basePmDetail instanceof TemperaturePmDetail temperaturePmDetail) {
                temperaturePmDetail.setTemperatureValue(pmUpdateForm.getTemperatureValue());
            }
        } else {
            basePmDetail.setDescription("Terminated");
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), '2', currentPerson, currentPmDetailPersistence);
            supervisorOperation(pmUpdateForm, currentPerson);
        }
        pmDetailRepository.save(basePmDetail);

        // PART 2. Pm Update
        if (pmUpdateForm.getActionType() == 0) {  //  End Pm
            endPm(pm);

        } else { //  Assign new PmDetail

            if (pm instanceof GeneralPm generalPm) {
                GeneralPmDetail pmDetail = new GeneralPmDetail();
                pmDetail.setRegisterDate(UtilService.getDATE());
                pmDetail.setRegisterTime(UtilService.getTime());
                pmDetail.setDelay(0);
                pmDetail.setPm(generalPm);
                assignNewPmDetail(pmDetail, pmUpdateForm.getActionType(), '0', true);

            } else if (pm instanceof TemperaturePm temperaturePm) {
                TemperaturePmDetail pmDetail = new TemperaturePmDetail();
                pmDetail.setRegisterDate(UtilService.getDATE());
                pmDetail.setRegisterTime(UtilService.getTime());
                pmDetail.setDelay(0);
                pmDetail.setPm(temperaturePm);
                assignNewPmDetail(pmDetail, pmUpdateForm.getActionType(), '0', true);
            }
        }
    }

    private PmDetail assignNewPmDetail(PmDetail pmDetail, int personId, char actionCode, boolean active) {
        var assigneePerson = personService.getPerson(personId);
        Persistence persistence = logService.persistenceSetup(assigneePerson);

        if (active) {
            pmDetail.setActive(true);
        } else {
            pmDetail.setActive(false);
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), actionCode, assigneePerson, persistence);
            pmDetail.setFinishedDate(pmDetail.getRegisterDate());
            pmDetail.setFinishedTime(pmDetail.getRegisterTime());
        }
        pmDetail.setPersistence(persistence);

        var persistedPmDetail = pmDetailRepository.save(pmDetail);
        notificationService.sendActiveTaskAssignedMessage(assigneePerson.getAddress().getValue(), pmDetail.getPm().getPmInterface().getName(), pmDetail.getPm().getDelay(), LocalDateTime.of(pmDetail.getRegisterDate(), pmDetail.getRegisterTime()));

        return persistedPmDetail;
    }


    private void supervisorOperation(PmUpdateForm pmUpdateForm, Person currentPerson) throws IOException {
        var pm = pmUpdateForm.getPm();
        if (pm instanceof GeneralPm generalPm) {
            GeneralPmDetail supervisorGeneralPmDetail = new GeneralPmDetail();
            supervisorGeneralPmDetail.setDescription(pmUpdateForm.getDescription());
            supervisorGeneralPmDetail.setPm(generalPm);
            var persistedDetail = assignNewPmDetail(supervisorGeneralPmDetail, currentPerson.getId(), '3', false);
            fileService.checkAttachment(pmUpdateForm.getFile(), persistedDetail.getPersistence());

        } else if (pm instanceof TemperaturePm temperaturePm) {
            TemperaturePmDetail supervisorTemperatureDetail = new TemperaturePmDetail();
            supervisorTemperatureDetail.setDescription(pmUpdateForm.getDescription());
            supervisorTemperatureDetail.setTemperatureValue(pmUpdateForm.getTemperatureValue());
            supervisorTemperatureDetail.setPm(temperaturePm);
            var persistedDetail = assignNewPmDetail(supervisorTemperatureDetail, currentPerson.getId(), '3', false);
            fileService.checkAttachment(pmUpdateForm.getFile(), persistedDetail.getPersistence());
        }
    }

    private Pm endPm(Pm pm) {
        pm.setFinishedDate(UtilService.getDATE());
        pm.setFinishedTime(UtilService.getTime());
        pm.setActive(false);

        if (pm instanceof GeneralPm) {

            centerService.updateCatalogDueDate(pm.getPmInterface(), pm.getLocation());

        } else if (pm instanceof TemperaturePm temperaturePm) {

            List<TemperaturePmDetail> temperaturePmDetailList = new ArrayList<>();
            for (PmDetail pmDetail : temperaturePm.getPmDetailList()) {
                temperaturePmDetailList.add((TemperaturePmDetail) pmDetail);
            }
            float sum = (float) temperaturePmDetailList.stream().filter(temperaturePmDetail -> temperaturePmDetail.getTemperatureValue() > 0.0f).mapToDouble(TemperaturePmDetail::getTemperatureValue).sum();
            int reportedTemperatures = (int) temperaturePmDetailList.stream().filter(temperaturePmDetail -> temperaturePmDetail.getTemperatureValue() > 0.0f).count();
            temperaturePm.setAverageDailyValue(sum / (float) reportedTemperatures);
        }

        var persistedPm = pmRepository.save(pm);

        var pmInterface = persistedPm.getPmInterface();
        if (pmInterface.getPmList().stream().noneMatch(Pm::isActive)) {
            pmInterface.setActive(false);
            pmInterfaceRepository.save(pmInterface);
        }
        return persistedPm;
    }

    @Override
    @PreAuthorize(" pm.active == true AND (ownerUsername == authentication.name OR hasAnyAuthority('ADMIN', 'SUPERVISOR')) ")
    public PmUpdateForm getPmUpdateForm(Pm pm, String ownerUsername) {
        PmUpdateForm pmUpdateForm = new PmUpdateForm();
        pmUpdateForm.setPm(pm);
        pmUpdateForm.setOwnerUsername(ownerUsername);

        return pmUpdateForm;
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
    public String getPmOwnerUsername(int pmId) {
        return pmDetailRepository.fetchPmOwnerUsername(pmId, true);
    }

    @Override
    public Pm getPm(int pmId) {
        var pm = pmRepository.findById(pmId);
        if (pm.isPresent()) {
            setPmTransients(List.of(pm.get()));
            return pm.get();
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public List<Pm> getActivePmList(boolean active, boolean workspace) {
        List<Pm> activePmList;
        if (workspace) {
            activePmList = pmDetailRepository.fetchPmListByActivationAndPerson(personService.getCurrentUsername(), active);

        } else {
            activePmList = pmDetailRepository.fetchPmListByActivationAndPerson(null, active);
        }

        if (!activePmList.isEmpty()) {
            setPmTransients(activePmList);

            return activePmList.stream().sorted(Comparator.comparing(Pm::getDelay).reversed()).toList();
        }
        return activePmList;
    }

    @Override
    @ModifyProtection
    public void pmInterfaceRegister(PmInterfaceRegisterForm pmInterfaceRegisterForm) throws IOException {
        PmInterface pmInterface;
        Persistence persistence;
        var currentPerson = personService.getCurrentPerson();

        if (pmInterfaceRegisterForm.getPmInterface() != null) { ///// Modify Pm
            pmInterface = pmInterfaceRegisterForm.getPmInterface();
            persistence = pmInterface.getPersistence();
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), '7', currentPerson, persistence);
            if (!pmInterface.isEnabled() && pmInterfaceRegisterForm.isEnabled()) {
                pmInterface.setEnabled(true);
                centerService.updateNewlyEnabledCatalog(pmInterface);
            } else {
                pmInterface.setEnabled(pmInterfaceRegisterForm.isEnabled());
            }

        } else {    // New Pm
            pmInterface = new PmInterface();
            pmInterface.setActive(false);
            pmInterface.setGeneralPm(true);
            pmInterface.setEnabled(pmInterfaceRegisterForm.isEnabled());
            persistence = logService.persistenceSetup(currentPerson);
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), '8', currentPerson, persistence);
            pmInterface.setPersistence(persistence);
        }

        pmInterface.setName(pmInterfaceRegisterForm.getTitle());
        pmInterface.setPeriod(pmInterfaceRegisterForm.getPeriod());
        pmInterface.setDescription(pmInterfaceRegisterForm.getDescription());
        pmInterface.setPmCategory(pmInterfaceRegisterForm.getPmCategory());
        pmInterface.setStatelessRecurring(pmInterfaceRegisterForm.isStatelessRecurring());
        fileService.checkAttachment(pmInterfaceRegisterForm.getFile(), persistence);

        pmInterfaceRepository.saveAndFlush(pmInterface);
    }

    @Override
    public Model modelForTaskController(Model model) {
        var authenticated = SecurityContextHolder.getContext().getAuthentication();
        var personName = authenticated.getName();
        Person person = personService.getPerson(personName);
        model.addAttribute("person", person);
        model.addAttribute("role", authenticated.getAuthorities());
        model.addAttribute("date", UtilService.getCurrentDate());

        return model;
    }

    @Override
    public List<PmCategory> getPmInterfaceFormData() {
        return pmCategoryRepository.findAll(Sort.by("name"));
    }

    @Override
    public PmInterfaceRegisterForm pmInterfaceEditFormData(short pmInterfaceId) {
        var optionalPmInterface = pmInterfaceRepository.findById(pmInterfaceId);

        if (optionalPmInterface.isPresent()) {
            var pmInterface = optionalPmInterface.get();
            PmInterfaceRegisterForm pmInterfaceRegisterForm = new PmInterfaceRegisterForm();
            pmInterfaceRegisterForm.setTitle(pmInterface.getName());
            pmInterfaceRegisterForm.setDescription(pmInterface.getDescription());
            pmInterfaceRegisterForm.setPeriod(pmInterface.getPeriod());
            pmInterfaceRegisterForm.setPmInterface(pmInterface);
            pmInterfaceRegisterForm.setPmCategory(pmInterface.getPmCategory());

         /*   List<MetaData> metaDataList = fileService.getRelatedMetadataList(List.of(pmInterface.getPersistence().getId()));
            if (!metaDataList.isEmpty()) {
                model.addAttribute("metaDataList", metaDataList);
            }*/
            return pmInterfaceRegisterForm;
        }

        return null;
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
