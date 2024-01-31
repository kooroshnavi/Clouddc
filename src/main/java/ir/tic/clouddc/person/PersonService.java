package ir.tic.clouddc.person;

import java.util.List;

public interface PersonService {
    Person getPerson(long personId);
    List<Person> getPersonList();
    List<Person> getPersonListNotIn(long personId);
    Person addPerson(Person person);
    long getAuthenticatedPersonId();

    Person updatePerson(Person person);

}
