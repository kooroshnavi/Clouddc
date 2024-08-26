package ir.tic.clouddc.log;

import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
public final class LogServiceImpl implements LogService {

    private final PersistenceRepository persistenceRepository;

    private final LogHistoryRepository logHistoryRepository;

    private final WorkflowRepository workflowRepository;

    private final PersonService personService;

    @Autowired
    public LogServiceImpl(PersistenceRepository persistenceRepository, LogHistoryRepository logHistoryRepository, WorkflowRepository workflowRepository, PersonService personService) {
        this.persistenceRepository = persistenceRepository;
        this.logHistoryRepository = logHistoryRepository;
        this.workflowRepository = workflowRepository;
        this.personService = personService;
    }

    @Override
    public Persistence newPersistenceInitialization(String logMessageKey) {
        var currentPerson = personService.getCurrentPerson();
        Persistence persistence = new Persistence(currentPerson);
        LogHistory logHistory = new LogHistory(UtilService.getDATE(), UtilService.getTime(), currentPerson, persistence, UtilService.LOG_MESSAGE.get(logMessageKey), true);
        persistence.setLogHistoryList(List.of(logHistory));

        return persistence;
    }

    @Override
    public Persistence persistenceSetup(Person person) {
        Persistence persistence = new Persistence();
        persistence.setPerson(person);
        return persistence;
    }

    @Override
    public void historyUpdate(LocalDate date, LocalTime time, String logMessage, Person person, Persistence persistence) {

        if (persistence.getLogHistoryList() != null) {
            persistence
                    .getLogHistoryList()
                    .stream()
                    .filter(LogHistory::isLast)
                    .findFirst()
                    .ifPresent(logHistory -> logHistory.setLast(false));

        }

        LogHistory newLogHistory = new LogHistory(date, time, person, persistence, logMessage, true);
        if (persistence.getLogHistoryList() != null) {
            persistence.getLogHistoryList().add(newLogHistory);
        } else {
            persistence.setLogHistoryList(List.of(newLogHistory));
        }
    }

    @Override
    public List<Long> getPersistenceIdList(Integer personId) {
        return persistenceRepository.getPersonPersistenceIdList(personId);
    }

    @Override
    public void saveWorkFlow(List<Workflow> workflowList) {
        workflowRepository.saveAll(workflowList);
    }

    @Override
    public List<Long> getSupervisorPmInterfaceEditFilePersiscentceIdList(String stringPmInterfaceId) {
        return logHistoryRepository.getPmInterfaceSupervisorEditedPersistenceList(stringPmInterfaceId);
    }
}
