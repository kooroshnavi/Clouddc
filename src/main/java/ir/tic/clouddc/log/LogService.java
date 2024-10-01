package ir.tic.clouddc.log;

import ir.tic.clouddc.person.Person;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface LogService {
    Persistence newPersistenceInitialization(String logMessageKey, Person currentPerson, String category);

    Persistence persistenceSetup(Person person, String category);

    void historyUpdate(LocalDate date, LocalTime time, String logMessage, Person person, Persistence persistence);

    List<Long> getPersistenceIdList(Integer personId);

    List<Long> getSupervisorPmInterfaceEditFilePersiscentceIdList(String stringPmInterfaceId);

    void registerIndependentPersistence(String logMessage, Person persistencePerson, Person historyPerson, String category);
}
