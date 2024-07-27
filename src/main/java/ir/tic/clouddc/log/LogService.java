package ir.tic.clouddc.log;

import ir.tic.clouddc.person.Person;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface LogService {

    Persistence persistenceSetup(Person person);

    void historyUpdate(LocalDate date, LocalTime time, String logMessage, Person person, Persistence persistence);

    List<Long> getPersistenceIdList(Integer personId);

    void saveWorkFlow(List<Workflow> workflowList);

}
