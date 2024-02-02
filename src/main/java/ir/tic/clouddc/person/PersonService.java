package ir.tic.clouddc.person;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    Person getPerson(long personId);
    Person updatePerson(Person person);
    Person addPerson(Person person);
    List<Person> getPersonList();
    List<Person> getPersonListNotIn(long personId);
    Optional<Address> getPersonAddress(long personId);
    long getAuthenticatedPersonId();


}
