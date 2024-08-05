package ir.tic.clouddc.log;

import ir.tic.clouddc.person.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public final class LogServiceImpl implements LogService {

    private final PersistenceRepository persistenceRepository;

    private final LogHistoryRepository logHistoryRepository;

    private final WorkflowRepository workflowRepository;

    @Autowired
    public LogServiceImpl(PersistenceRepository persistenceRepository, LogHistoryRepository logHistoryRepository, WorkflowRepository workflowRepository) {
        this.persistenceRepository = persistenceRepository;
        this.logHistoryRepository = logHistoryRepository;
        this.workflowRepository = workflowRepository;
    }

    @Override
    public Persistence persistenceSetup(Person person) {
        Persistence persistence = new Persistence();
        persistence.setPerson(person);
        return persistence;
    }

    @Override
    public void historyUpdate(LocalDate date, LocalTime time, String logMessage, Person person, Persistence persistence) {
        List<LogHistory> logHistoryList = new ArrayList<>();
        LogHistory logHistory = new LogHistory(date, time, person, persistence, logMessage, true);
        logHistoryList.add(logHistory);
        if (persistence.getLogHistoryList() != null) {
            Optional<LogHistory> currentHistory = persistence.getLogHistoryList().stream().filter(LogHistory::isLast).findAny();
            if (currentHistory.isPresent()) {
                currentHistory.get().setLast(false);
                logHistoryList.add(currentHistory.get());
            }
        }
        logHistoryRepository.saveAll(logHistoryList);
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
