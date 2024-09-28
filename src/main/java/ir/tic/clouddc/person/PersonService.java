package ir.tic.clouddc.person;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface PersonService {

    interface PersonProjection_1 {

        int getId();

        String getName();

        boolean getAssignee();

        boolean getDisabled();

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
