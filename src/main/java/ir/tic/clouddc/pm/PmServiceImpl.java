package ir.tic.clouddc.pm;

import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.center.Location;
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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    private static final int DEFAULT_ASSIGNEE_ID = 7;
    private final GeneralPmRepository generalPmRepository;

    @Autowired
    PmServiceImpl(PmRepository pmRepository,
                  PmInterfaceRepository pmInterfaceRepository, PmDetailRepository pmDetailRepository,
                  PmCategoryRepository pmCategoryRepository, CenterService centerService,
                  PersonService personService,
                  NotificationService notificationService,
                  LogService logService, FileService fileService, GeneralPmRepository generalPmRepository) {
        this.pmRepository = pmRepository;
        this.pmInterfaceRepository = pmInterfaceRepository;
        this.pmDetailRepository = pmDetailRepository;
        this.pmCategoryRepository = pmCategoryRepository;
        this.centerService = centerService;
        this.personService = personService;
        this.notificationService = notificationService;
        this.logService = logService;
        this.fileService = fileService;
        this.generalPmRepository = generalPmRepository;

    }

    public void updateTodayTasks(DailyReport todayReport) {
        final List<Location> locationList = centerService.getLocationList();
        final Person defaultPerson = personService.getPerson(DEFAULT_ASSIGNEE_ID);
        List<Pm> activePmList = pmRepository.findAllByActive(true);
        List<PmDetail> pmDetailList = new ArrayList<>();

        delayCalculation(activePmList, pmDetailList);

        for (Location location : locationList) {
            Set<Short> pmInterfaceIdSet = location.getPmDueMap().keySet();
            if (!pmInterfaceIdSet.isEmpty()) {
                for (Short pmInterfaceId : pmInterfaceIdSet) {
                    if (location.getPmDueMap().get(pmInterfaceId) == UtilService.getDATE()) {
                        PmInterface pmInterface = pmInterfaceRepository.findById(pmInterfaceId).get();
                        pmSetup(location, pmInterface, pmDetailList, defaultPerson);
                        if (pmInterface.isStatelessRecurring()) {
                            var nextDue = validateNextDue(UtilService.getDATE().plusDays(pmInterface.getPeriod()));
                            location.getPmDueMap().put(pmInterfaceId, nextDue);
                        }
                    }
                }
            }
        }

        pmDetailRepository.saveAll(pmDetailList);
        notificationService.sendScheduleUpdateMessage("09127016653", "Scheduler successful @: " + LocalDateTime.now());
    }

    private LocalDate validateNextDue(LocalDate nextDue) {
        if (nextDue.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()).equals("Thu")) {
            return nextDue.plusDays(2);
        } else if (nextDue.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()).equals("Fri")) {
            return nextDue.plusDays(1);
        } else {
            return nextDue;
        }
    }

    private void delayCalculation(List<Pm> currentPmList, List<PmDetail> pmDetailList) {
        if (!currentPmList.isEmpty()) {
            for (Pm pm : currentPmList
            ) {
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

    private void pmSetup(Location location, PmInterface pmInterface, List<PmDetail> pmDetailList, Person defaultPerson) {
        if (pmInterface.isGeneralPm()) {
            GeneralPm todayGeneralPm = new GeneralPm();
            todayGeneralPm.setActive(true);
            todayGeneralPm.setDelay(0);
            todayGeneralPm.setDueDate(location.getPmDueMap().get(pmInterface.getId()));
            todayGeneralPm.setPmInterface(pmInterface);
            todayGeneralPm.setLocation(location);

            pmDetailSetup(todayGeneralPm, pmDetailList, pmInterface.isGeneralPm(), defaultPerson);

        } else {
            TemperaturePm todayTemperaturePm = new TemperaturePm();
            todayTemperaturePm.setActive(true);
            todayTemperaturePm.setDelay(0);
            todayTemperaturePm.setDueDate(location.getPmDueMap().get(pmInterface.getId()));
            todayTemperaturePm.setPmInterface(pmInterface);
            todayTemperaturePm.setLocation(location);

            pmDetailSetup(todayTemperaturePm, pmDetailList, pmInterface.isGeneralPm(), defaultPerson);
        }
    }

    private void pmDetailSetup(Pm todayPm, List<PmDetail> pmDetailList, boolean isGeneral, Person defaultPerson) {
        if (isGeneral) {
            GeneralPmDetail generalPmDetail = new GeneralPmDetail();
            generalPmDetail.setPm(todayPm);
            generalPmDetail.setActive(true);
            generalPmDetail.setDelay(0);
            generalPmDetail.setRegisterDate(UtilService.getDATE());
            generalPmDetail.setRegisterTime(UtilService.getTime());
            generalPmDetail.setPersistence(logService.persistenceSetup(defaultPerson));
            pmDetailList.add(generalPmDetail);
        } else {
            TemperaturePmDetail temperaturePmDetail = new TemperaturePmDetail();
            temperaturePmDetail.setPm(todayPm);
            temperaturePmDetail.setActive(true);
            temperaturePmDetail.setDelay(0);
            temperaturePmDetail.setRegisterDate(UtilService.getDATE());
            temperaturePmDetail.setRegisterTime(UtilService.getTime());
            temperaturePmDetail.setPersistence(logService.persistenceSetup(defaultPerson));
            pmDetailList.add(temperaturePmDetail);
        }
    }

    @Override
    public List<PmInterface> getPmInterfaceList() {
        return pmInterfaceRepository.findAll(Sort.by("general"));
    }

    @Override
    public Model getPmInterfacePmListModel(Model model, short pmInterfaceId, boolean active, int locationId) {

        PmInterface pmInterface;
        var optionalPmInterface = pmInterfaceRepository.findById(pmInterfaceId);
        if (optionalPmInterface.isPresent()) {
            pmInterface = optionalPmInterface.get();
        } else {
            throw new NoSuchElementException();
        }

        List<? extends Pm> basePmList = pmRepository.findAllByPmInterfaceAndActiveAndLocationId(pmInterface, active, locationId);

        setPmTransients(basePmList);

        var orderedPmList = basePmList
                .stream()
                .sorted(Comparator.comparing(Pm::getDueDate).reversed())
                .toList();

        model.addAttribute("pmInterface", pmInterface);
        model.addAttribute("pmList", orderedPmList);
        model.addAttribute("active", active);

        return model;
    }

    private Model getRelatedPmList(List<Pm> basePmList, Model model) {
        List<GeneralPm> generalPmList = new ArrayList<>();
        List<TemperaturePm> temperaturePmList = new ArrayList<>();
        for (Pm pm : basePmList) {
            if (pm instanceof GeneralPm generalPm) {
                generalPmList.add(generalPm);
            } else if (pm instanceof TemperaturePm temperaturePm) {
                temperaturePmList.add(temperaturePm);
            }
        }
        if (!generalPmList.isEmpty()) {
            model.addAttribute("pmList", generalPmList);
        } else {
            model.addAttribute("pmList", temperaturePmList);
        }
        return model;
    }


    private Pm endPm(Pm pm) {
        pm.setFinishedDate(UtilService.getDATE());
        pm.setFinishedTime(UtilService.getTime());
        pm.setActive(false);

        var pmInterface = pm.getPmInterface();
        if (!pmInterface.isStatelessRecurring()) {
            var location = pm.getLocation();
            location.getPmDueMap().put(pmInterface.getId(), validateNextDue(pm.getFinishedDate().plusDays(pmInterface.getPeriod())));
        }

        if (pmInterface.getPmList().stream().noneMatch(Pm::isActive)) {
            pmInterface.setActive(false);   // pmInterface is inactive til next due
        }

        return pmRepository.saveAndFlush(pm);
    }


    @Override
    public Model getPmDetailList(Model model, int pmId) {
        /// Get pm and its interface
        Pm basePm;
        var optionalPm = pmRepository.findById(pmId);
        if (optionalPm.isPresent()) {
            basePm = optionalPm.get();
        } else {
            throw new NoSuchElementException();
        }

        // add related pm instance to model
        setPmTransients(List.of(basePm));
        if (basePm instanceof GeneralPm pm) {
            model.addAttribute("pm", pm);
        } else if (basePm instanceof TemperaturePm pm) {
            model.addAttribute("pm", pm);
        }
        model.addAttribute("pmInterface", basePm.getPmInterface());

        /// prepare and add pmDetail
        var orderedPmDetailList = basePm
                .getPmDetailList()
                .stream()
                .sorted(Comparator.comparing(PmDetail::getId).reversed())
                .toList();
        setPmDetailTransients(orderedPmDetailList);

        List<TemperaturePmDetail> temperaturePmDetailList = new ArrayList<>();
        List<GeneralPmDetail> generalPmDetailList = new ArrayList<>();

        for (PmDetail pmDetail : orderedPmDetailList) {
            if (pmDetail instanceof TemperaturePmDetail temperaturePmDetail) {
                temperaturePmDetailList.add(temperaturePmDetail);
            } else if (pmDetail instanceof GeneralPmDetail generalPmDetail) {
                generalPmDetailList.add(generalPmDetail);
            }
        }
        if (!generalPmDetailList.isEmpty()) {
            model.addAttribute("pmDetailList", generalPmDetailList);
        } else {
            model.addAttribute("pmDetailList", temperaturePmDetailList);
        }

        /// add metaData list
        List<Long> persistenceIdList = pmDetailRepository.getPersistenceIdList(pmId);
        List<MetaData> metaDataList = fileService.getRelatedMetadataList(persistenceIdList);
        if (!metaDataList.isEmpty()) {
            model.addAttribute("metaDataList", metaDataList);
        }

        /// pm detail form auth fields
        var ownerUsername = orderedPmDetailList.stream().filter(PmDetail::isActive).findAny().get().getPersistence().getPerson().getUsername();
        model.addAttribute("permission", pmDetailFormViewPermission(ownerUsername));
        model.addAttribute("ownerUsername", ownerUsername);

        return model;
    }

    private void setPmTransients(List<? extends Pm> basePm) {
        for (Pm pm : basePm) {
            pm.setPersianDueDate(UtilService.getFormattedPersianDate(pm.getDueDate()));
            if (!pm.isActive()) {
                pm.setPersianFinishedDate(UtilService.getFormattedPersianDate(pm.getFinishedDate()));
                pm.setPersianFinishedDayTime(UtilService.getFormattedPersianDayTime(pm.getFinishedDate(), pm.getFinishedTime()));
            } else {
                pm.setActivePersonName(pm.getPmDetailList()
                        .stream()
                        .filter(PmDetail::isActive).findFirst().get()
                        .getPersistence().getPerson().getName());
            }
        }
    }

    private void setPmDetailTransients(List<PmDetail> pmDetailList) {
        for (PmDetail pmDetail : pmDetailList) {
            pmDetail
                    .setPersianRegisterDate(UtilService
                            .getFormattedPersianDate(pmDetail.getRegisterDate()));
            pmDetail
                    .setPersianRegisterDayTime(UtilService
                            .getFormattedPersianDayTime(pmDetail.getRegisterDate(), pmDetail.getRegisterTime()));
            if (!pmDetail.isActive()) {
                pmDetail
                        .setPersianFinishedDate(UtilService
                                .getFormattedPersianDate(pmDetail.getFinishedDate()));
                pmDetail
                        .setPersianFinishedDayTime(UtilService
                                .getFormattedPersianDayTime(pmDetail.getFinishedDate(), pmDetail.getFinishedTime()));
            }
        }
    }

    private boolean pmDetailFormViewPermission(String currentPmDetailUsername) {
        List<GrantedAuthority> supervisorRoleList = List.of(new SimpleGrantedAuthority("SUPERVISOR"), new SimpleGrantedAuthority("ADMIN"));
        if (personService.getCurrentPersonRoleList().contains((GrantedAuthority) supervisorRoleList)) {
            return true;
        } else return currentPmDetailUsername.equals(personService.getCurrentUsername());
    }

    private PmDetail assignNewTaskDetail(PmDetail pmDetail, int personId, char actionCode, boolean active) {
        pmDetail.setRegisterDate(UtilService.getDATE());
        pmDetail.setRegisterTime(UtilService.getTime());
        pmDetail.setDelay(0);
        Persistence persistence = logService.persistenceSetup(personService.getPerson(personId));

        if (active) {
            pmDetail.setActive(true);
        } else {
            pmDetail.setActive(false);
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), actionCode, new Person(personId), persistence);
            pmDetail.setFinishedDate(pmDetail.getRegisterDate());
            pmDetail.setFinishedTime(pmDetail.getRegisterTime());
        }

        pmDetail.setPersistence(persistence);
        pmDetailRepository.save(pmDetail);
        notificationService.sendActiveTaskAssignedMessage(personService.getPerson(personId).getAddress().getValue(), pmDetail.getPm().getPmInterface().getName(), pmDetail.getPm().getDelay(), pmDetail.getRegisterTime());

        return pmDetail;
    }

    @Override
    @PreAuthorize(" pm.active == true  && (ownerUsername == authentication.name || hasAnyAuthority('ADMIN', 'SUPERVISOR')) ")
    public void updatePm(PmUpdateForm pmUpdateForm, Pm pm, String ownerUsername) throws IOException {
        PmDetail basePmDetail;
        var optionalPmDetail = pm.getPmDetailList().stream().filter(PmDetail::isActive).findAny();
        if (optionalPmDetail.isPresent()) {
            basePmDetail = optionalPmDetail.get();
        } else {
            throw new NoSuchElementException();
        }

        Persistence currentPmDetailPersistence = basePmDetail.getPersistence();
        var currentUsername = personService.getCurrentUsername();
        basePmDetail.setFinishedDate(UtilService.getDATE());
        basePmDetail.setFinishedTime(UtilService.getTime());
        basePmDetail.setActive(false);

        if (ownerUsername.equals(currentUsername)) {
            routineOperation(basePmDetail, currentPmDetailPersistence, pmUpdateForm);
        } else {
            supervisorOperation(basePmDetail, currentPmDetailPersistence, pmUpdateForm, personService.getCurrentPerson());
        }

        if (pmUpdateForm.getActionType() == 0) {  //  End Pm
            endPm(pm);
        } else { //  Assign new PmDetail
            if (pm.getPmInterface().isGeneralPm()) {
                GeneralPmDetail generalPmDetail = new GeneralPmDetail();
                generalPmDetail.setPm(pm);
                assignNewTaskDetail(generalPmDetail, pmUpdateForm.getActionType(), '0', true);
            } else if (pm.getPmInterface().isTemperaturePm()) {
                TemperaturePmDetail temperaturePmDetail = new TemperaturePmDetail();
                temperaturePmDetail.setPm(pm);
                assignNewTaskDetail(temperaturePmDetail, pmUpdateForm.getActionType(), '0', true);
            }
        }

    }

    private void routineOperation(PmDetail basePmDetail, Persistence currentTaskDetailPersistence, PmUpdateForm pmUpdateForm) throws IOException {
        basePmDetail.setDescription(pmUpdateForm.getDescription());
        if (basePmDetail instanceof TemperaturePmDetail temperaturePmDetail) {
            temperaturePmDetail.setTemperatureValue(pmUpdateForm.getTemperatureValue());
        }
        logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), '1', currentTaskDetailPersistence.getPerson(), currentTaskDetailPersistence);
        fileService.checkAttachment(pmUpdateForm.getFile(), currentTaskDetailPersistence);
    }

    private void supervisorOperation(PmDetail basePmDetail, Persistence currentTaskDetailPersistence, PmUpdateForm pmUpdateForm, Person currentPerson) throws IOException {
        basePmDetail.setDescription("Terminated by supervisor");
        logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), '2', currentPerson, currentTaskDetailPersistence);

        if (basePmDetail.getPm().getPmInterface().isGeneralPm()) {
            GeneralPmDetail supervisorGeneralPmDetail = new GeneralPmDetail();
            supervisorGeneralPmDetail.setPm(basePmDetail.getPm());
            supervisorGeneralPmDetail.setDescription(pmUpdateForm.getDescription());
            assignNewTaskDetail(supervisorGeneralPmDetail, currentPerson.getId(), '3', false);
            fileService.checkAttachment(pmUpdateForm.getFile(), supervisorGeneralPmDetail.getPersistence());

        } else if (basePmDetail.getPm().getPmInterface().isTemperaturePm()) {
            TemperaturePmDetail temperaturePmDetail = new TemperaturePmDetail();
            temperaturePmDetail.setPm(basePmDetail.getPm());
            temperaturePmDetail.setDescription(pmUpdateForm.getDescription());
            temperaturePmDetail.setTemperatureValue(pmUpdateForm.getTemperatureValue());
            assignNewTaskDetail(temperaturePmDetail, currentPerson.getId(), '3', false);
            fileService.checkAttachment(pmUpdateForm.getFile(), temperaturePmDetail.getPersistence());
        }
    }


    @Override
    @PreAuthorize(" pm.active == true  && (ownerUsername == authentication.name || hasAnyAuthority('ADMIN', 'SUPERVISOR')) ")
    public Model getPmUpdateForm(Model model, Pm pm, String ownerUsername) {
        var currentUsername = personService.getCurrentUsername();
        PmUpdateForm pmUpdateForm = new PmUpdateForm();
        pmUpdateForm.setPmId(pm.getId());
        List<Person> assignPersonList;

        if (ownerUsername.equals(currentUsername)) { /// pmDetail Owner updates pm
            assignPersonList = personService.getPersonListExcept(List.of(ownerUsername));
        } else {  /// supervisor updates pm
            assignPersonList = personService.getPersonListExcept(List.of(ownerUsername, currentUsername));
        }

        model.addAttribute("pmInterface", pm.getPmInterface());
        model.addAttribute("pm", pm);
        model.addAttribute("assignPersonList", assignPersonList);
        model.addAttribute("pmUpdateForm", pmUpdateForm);

        return model;
    }


    @Override
    public String getPmOwnerUsername(int pmId) {
        return pmDetailRepository.fetchPmOwnerUsername(pmId, true);
    }

    @Override
    public Pm getPm(int pmId) {
        var pm = pmRepository.findById(pmId);
        if (pm.isPresent()) {
            return pm.get();
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public Model getPersonActivePmList(Model model) {
        List<? extends Pm> activePersonPmList = pmDetailRepository.fetchActivePersonPmList(personService.getCurrentUsername(), true);
        if (!activePersonPmList.isEmpty()) {
            setPmTransients(activePersonPmList);
            var sortedPersonTaskList = activePersonPmList
                    .stream()
                    .sorted(Comparator.comparing(Pm::getDelay).reversed())
                    .toList();
            model.addAttribute("activePersonPmList", sortedPersonTaskList);

            return model;
        }
        return model;
    }

    @Override
    @ModifyProtection
    public void pmInterfaceRegister(pmInterfaceRegisterForm pmInterfaceRegisterForm) throws IOException {
        PmInterface pmInterface;
        Persistence persistence;
        var currentPerson = personService.getCurrentPerson();

        if (pmInterfaceRegisterForm.getId() > 0) {  ///// Modify Pm
            pmInterface = pmInterfaceRepository.findById(pmInterfaceRegisterForm.getId()).get();
            persistence = pmInterface.getPersistence();
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), '7', currentPerson, persistence);
        } else {  //// New Pm
            pmInterface = new PmInterface();
            pmInterface.setActive(false);
            persistence = logService.persistenceSetup(currentPerson);
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), '8', currentPerson, persistence);
            pmInterface.setPersistence(persistence);
        }

        pmInterface.setName(pmInterfaceRegisterForm.getName());
        pmInterface.setPeriod(pmInterfaceRegisterForm.getPeriod());
        pmInterface.setDescription(pmInterfaceRegisterForm.getDescription());
        pmInterface.setEnabled(pmInterfaceRegisterForm.isEnabled());
        pmInterface.setPmCategory(pmCategoryRepository.findById(pmInterfaceRegisterForm.getCategoryId()).get());
        pmInterface.setGeneralPm(true);
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
    public Model getPmInterfaceFormData(Model model) {
        model.addAttribute("salonList", centerService.getSalonList());
        model.addAttribute("pmCategoryList", pmCategoryRepository.findAll(Sort.by("name")));
        model.addAttribute("pmRegister", new pmInterfaceRegisterForm());
        return model;
    }

    @Override
    public Model pmInterfaceEditFormData(Model model, short pmInterfaceId) {
        var optionalPmInterface = pmInterfaceRepository.findById(pmInterfaceId);

        if (optionalPmInterface.isPresent()) {
            var pmInterface = optionalPmInterface.get();
            pmInterfaceRegisterForm pmInterfaceRegisterForm = new pmInterfaceRegisterForm();
            pmInterfaceRegisterForm.setName(pmInterface.getName());
            pmInterfaceRegisterForm.setDescription(pmInterface.getDescription());
            pmInterfaceRegisterForm.setPeriod(pmInterface.getPeriod());
            pmInterfaceRegisterForm.setId(pmInterfaceId);
            pmInterfaceRegisterForm.setCategoryId(pmInterface.getPmCategory().getId());

            List<MetaData> metaDataList = fileService.getRelatedMetadataList(List.of(pmInterface.getPersistence().getId()));
            if (!metaDataList.isEmpty()) {
                model.addAttribute("metaDataList", metaDataList);
            }
            model.addAttribute("pmInterfaceRegisterForm", pmInterfaceRegisterForm);
            model.addAttribute("pmSize", pmInterface.getPmList().size());
            model.addAttribute("pmCategoryList", pmCategoryRepository.findAll(Sort.by("name")));

            return model;
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
