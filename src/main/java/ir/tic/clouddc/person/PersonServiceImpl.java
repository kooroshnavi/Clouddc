package ir.tic.clouddc.person;

import ir.tic.clouddc.otp.OTPService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public final class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final OTPService otpService;
    private final AddressRepository addressRepository;


    @Autowired
    public PersonServiceImpl(PersonRepository personRepository, OTPService otpService, AddressRepository addressRepository) {
        this.personRepository = personRepository;
        this.otpService = otpService;
        this.addressRepository = addressRepository;
    }

    @Override
    public boolean checkPhoneExistence(PersonRegisterForm personRegisterForm) {
        boolean exist = addressRepository.existsByValue(personRegisterForm.getPhoneNumber());

        return exist;
    }

    @Override
    public String initPhoneRegister(String phoneNumber) throws ExecutionException {
        return otpService.generatePersonRegisterOTP(phoneNumber);
    }

    @Override
    public void registerNewPerson(PersonRegisterForm personRegisterForm) {
        log.info("Register Method");
        Person person = new Person();
        Address address = new Address();
        var firstName = personRegisterForm.getFirstName().trim();
        var lastName = personRegisterForm.getLastName().trim();
        person.setName(lastName + " " + firstName);
        UUID username = UUID.randomUUID();
        person.setUsername(username.toString());
        person.setRole(personRegisterForm.getRoleCode());
        person.setAssignee(person.getRole() != 2);
        person.setWorkSpaceSize(0);
        person.setDisabled(false);
        address.setValue(personRegisterForm.getPhoneNumber());
        person.setAddress(address);

        personRepository.saveAndFlush(person);
    }

    @Override
    public String validateOTP(PersonRegisterForm personRegisterForm) throws ExecutionException {
        return otpService.verifyPersonRegisterOTP(personRegisterForm.getPhoneNumber(), personRegisterForm.getOTPCode());
    }

    @Override
    public Person getPersonByUsername(String name) {
        return personRepository.findByUsername(name);
    }

    @Override
    public List<Person> getPersonListExcept(List<String> ignoreUsernameList) {
        return personRepository.fetchAssignablePersonList(ignoreUsernameList, true);
    }

    @Override
    public Person getCurrentPerson() {
        return getPersonByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
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
        return personRepository.findAllByAssignee(true);
    }

    @Override
    public Person getReferencedPerson(Integer personId) throws EntityNotFoundException {
        return personRepository.getReferenceById(personId);
    }

    @Override
    public List<PersonProjection_1> getRegisteredPerosonList() {
        return personRepository.getPersonProjection_1();
    }

}
