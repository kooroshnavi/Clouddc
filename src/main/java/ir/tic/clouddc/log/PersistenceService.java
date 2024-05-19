package ir.tic.clouddc.log;

import ir.tic.clouddc.person.Person;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface PersistenceService {

    Persistence setupNewPersistence(LocalDate date, LocalTime time, char messageId, Person person, boolean active);

    void updatePersistence(LocalDate date, LocalTime time, Persistence persistence, char messageId, boolean active);

    Person getAssignedPerson(long persistenceId);

    List<Integer> getActivePersonPersistenceIdList(int personId, boolean active);
}
