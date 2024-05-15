package ir.tic.clouddc.log;

import ir.tic.clouddc.person.Person;

public interface PersistenceService {

    Persistence setupNewPersistence(char messageId, Person person);
}
