package ir.tic.clouddc.log;

import ir.tic.clouddc.person.Person;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface PersistenceService {

    Persistence persistenceSetup(LocalDate date, LocalTime time, char messageId, Person person);

    void historyUpdate(LocalDate date, LocalTime time, char actionCode, Person person, Persistence persistence);


    List<Integer> getActivePersonPersistenceIdList(int personId, boolean active);

    Person getRelatedCurrentPerson(long persistenceId, boolean active);
}
