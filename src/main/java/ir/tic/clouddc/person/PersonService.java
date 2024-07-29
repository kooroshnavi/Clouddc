package ir.tic.clouddc.person;

import ir.tic.clouddc.resource.Utilizer;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface PersonService {
    Person getPerson(int personId);
    Person getPerson(String name);
    List<Person> getPersonList();
    List<Person> getPersonListExcept(List<String> ignoreUsernameList);
    Person addPerson(Person person);

    Person updatePerson(Person person);
    int getPersonId(String username);
    Person getCurrentPerson();

    String getCurrentUsername();

    List<GrantedAuthority> getCurrentPersonRoleList();

    List<Utilizer> getUtilizerList();

    List<Person> getDefaultAssgineeList();

    Person getReferencedPerson(Integer defaultPersonId);
}
