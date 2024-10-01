package ir.tic.clouddc.person;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface PersonService {

    boolean checkPhoneExistence(PersonRegisterForm personRegisterForm);

    String initPhoneRegister(String phoneNumber) throws ExecutionException;

    boolean registerNewPerson(PersonRegisterForm personRegisterForm) throws ExecutionException;

    String validateOTP(PersonRegisterForm personRegisterForm) throws ExecutionException;

    void registerLoginHistory(String address, String remoteAddr, boolean successful);

    interface PersonProjection_1 {

        int getId();

        String getName();

        boolean getAssignee();

        boolean getEnabled();

        char getRole();

        int getWorkspaceSize();
    }

    Person getPersonByUsername(String name);

    List<Person> getPersonListExcept(List<String> ignoreUsernameList);

    Person getCurrentPerson();

    String getCurrentUsername();

    List<GrantedAuthority> getCurrentPersonRoleList();

    List<Person> getDefaultAssgineeList();

    Person getReferencedPerson(Integer defaultPersonId);

    List<PersonProjection_1> getRegisteredPerosonList();
}
