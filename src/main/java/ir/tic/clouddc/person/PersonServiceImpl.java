package ir.tic.clouddc.person;

import ir.tic.clouddc.log.LogService;
import ir.tic.clouddc.log.Persistence;
import ir.tic.clouddc.otp.OTPService;
import ir.tic.clouddc.utils.UtilService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@Transactional
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    private final OTPService otpService;

    private final AddressRepository addressRepository;

    private final LogService logService;


    @Autowired
    public PersonServiceImpl(PersonRepository personRepository, OTPService otpService, AddressRepository addressRepository, LogService logService) {
        this.personRepository = personRepository;
        this.otpService = otpService;
        this.addressRepository = addressRepository;
        this.logService = logService;
    }

    @Override
    public boolean checkPhoneExistence(PersonRegisterForm personRegisterForm) {

        return addressRepository.existsByValue(personRegisterForm.getPhoneNumber());
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISOR', 'MANAGER')")
    public String initPhoneRegister(String phoneNumber) throws ExecutionException {
        return otpService.generatePersonRegisterOTP(phoneNumber);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISOR', 'MANAGER')")
    public boolean registerNewPerson(PersonRegisterForm personRegisterForm) throws ExecutionException {
        Person person;
        if (personRegisterForm.getPersonId() != null) { // Update Person Info
            person = personRepository.getReferenceById(personRegisterForm.getPersonId());
            if (person.isEnabled() && !personRegisterForm.isEnabled()) {
                person.setEnabled(false);
                person.setAssignee(false);
                otpService.invalidateLoginOTP(person.getAddress());

           /*     List<String> loggedUserList = sessionRegistry
                        .getAllPrincipals()
                        .stream()
                        .filter(a -> a instanceof String)
                        .map(String.class::cast)
                        .toList();
                log.info(loggedUserList.toString());*/

                logService.registerIndependentPersistence(UtilService.LOG_MESSAGE.get("DisablePerson"), person, getCurrentPerson(), "PersonUpdate");

                return true;
            } else if (!person.isEnabled() && personRegisterForm.isEnabled()) {
                person.setEnabled(true);
                person.setRole(personRegisterForm.getRoleCode());
                person.setAssignee(person.getRole() != '2');
                logService.registerIndependentPersistence(UtilService.LOG_MESSAGE.get("EnablePerson"), person, getCurrentPerson(), "PersonUpdate");

                return true;
            } else if (person.isEnabled() && person.getRole() != personRegisterForm.getRoleCode()) {
                person.setRole(personRegisterForm.getRoleCode());
                person.setAssignee(person.getRole() != '2');
                logService.registerIndependentPersistence(UtilService.LOG_MESSAGE.get("PersonRole"), person, getCurrentPerson(), "PersonUpdate");

                return true;
            } else {
                return false;
            }

        } else {    // Register New Person
            person = new Person();
            Address address = new Address();
            var firstName = personRegisterForm.getFirstName().trim();
            var lastName = personRegisterForm.getLastName().trim();
            person.setName(lastName + " " + firstName);
            UUID username = UUID.randomUUID();
            person.setUsername(username.toString());
            person.setRole(personRegisterForm.getRoleCode());
            person.setAssignee(person.getRole() != '2');
            person.setWorkspaceSize(0);
            person.setEnabled(true);
            address.setValue(personRegisterForm.getPhoneNumber());
            person.setAddress(address);
            Persistence persistence = new Persistence(person, "PersonRegister");
            logService.historyUpdate(UtilService.getDATE(), UtilService.getTime(), UtilService.LOG_MESSAGE.get("RegPerson"), getCurrentPerson(), persistence);
            person.setPersistenceList(List.of(persistence));
            personRepository.saveAndFlush(person);

            return true;
        }
    }

    @Override
    public String validateOTP(PersonRegisterForm personRegisterForm) throws ExecutionException {
        return otpService.verifyPersonRegisterOTP(personRegisterForm.getPhoneNumber(), personRegisterForm.getOTPCode());
    }

    @Override
    public void registerLoginHistory(String address, String remoteAddr, boolean successful) {
        var person = personRepository.fetchByPhoneNumber(address);
        LoginHistory loginHistory = new LoginHistory(person, remoteAddr, UtilService.getDATE(), UtilService.getTime(), successful);
        if (person.getLoginHistoryList() != null) {
            person.getLoginHistoryList().add(loginHistory);
        } else {
            person.setLoginHistoryList(List.of(loginHistory));
        }
        if (successful) {
            person.setLatestLoginHistory(loginHistory);
        }
        personRepository.saveAndFlush(person);
    }

    @Override
    public Person getPersonByUsername(String name) {
        return personRepository.fetchByUsername(name);
    }

    @Override
    public List<Person> getPersonListExcept(List<String> ignoreUsernameList) {
        return personRepository.fetchAssignablePersonList(ignoreUsernameList, true);
    }

    @Override
    public Person getCurrentPerson() {
        return getPersonByUsername(SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName());
    }

    @Override
    public String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public List<GrantedAuthority> getCurrentPersonRoleList() {
        return new ArrayList<>(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
    }

    @Override
    public List<Person> getDefaultAssgineeList() {
        return personRepository.findAllByAssigneeAndEnabled(true, true);
    }

    @Override
    public Person getReferencedPerson(Integer personId) throws EntityNotFoundException {
        return personRepository.getReferenceById(personId);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERVISOR', 'MANAGER', 'OPERATOR')")
    public List<Person> getRegisteredPerosonList() {
        List<Person> personList = new ArrayList<>();
        var currentPersonRoleList = getCurrentPersonRoleList();
        if (currentPersonRoleList
                .stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN")
                        || grantedAuthority.getAuthority().equals("MANAGER"))) {
            personList = personRepository.fetchTotalPersonList();

        } else if (currentPersonRoleList
                .stream()
                .anyMatch(grantedAuthority
                        -> grantedAuthority.getAuthority().equals("SUPERVISOR"))) {
            personList = personRepository.fetchAccessiblePersonList(List.of('0', '1'));
        } else {
            personList = List.of(getCurrentPerson());
        }

        for (Person person : personList) {
            if (person.getLatestLoginHistory() != null) {
                var persianLoginDateTime = UtilService
                        .getFormattedPersianDateAndTime(person.getLatestLoginHistory().getLocalDate()
                                , person.getLatestLoginHistory().getLocalTime());
                person.setPersianLoginTime(persianLoginDateTime);
            }
        }
        return personList
                .stream()
                .sorted(Comparator.comparing(Person::isEnabled)
                        .thenComparing(Person::getWorkspaceSize).reversed())
                .toList();
    }

    @Override
    @PreAuthorize("(#targetUsername == authentication.name || hasAnyAuthority('ADMIN', 'MANAGER', 'SUPERVISOR')) ")
    public List<LoginHistory> getLoginHistoryList(Person targetPerson, String targetUsername) {
        var currentPerson = getCurrentPerson();
        if (currentPerson.getRole() == '1' && targetPerson.getRole() != '0' && targetPerson.getRole() != '1') {
            return Collections.emptyList();
        }
        var loginList = targetPerson.getLoginHistoryList();
        if (!loginList.isEmpty()) {
            for (LoginHistory loginHistory : loginList) {
                loginHistory.setPersianLoginDate(UtilService.getFormattedPersianDateAndTime(loginHistory.getLocalDate(), loginHistory.getLocalTime()));
            }
        }

        return loginList
                .stream()
                .sorted(Comparator.comparing(LoginHistory::getLocalDate).reversed())
                .toList();
    }
}
