package ir.tic.clouddc.log;

import ir.tic.clouddc.individual.Person;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface LogService {

    Persistence persistenceSetup(Person person);

    void historyUpdate(LocalDate date, LocalTime time, String logMessage, Person person, Persistence persistence);

    List<Long> getPersistenceIdList(long workFlow);

    void saveWorkFlow(List<Workflow> workflowList);

}
