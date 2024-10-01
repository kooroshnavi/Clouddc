package ir.tic.clouddc.log;

import ir.tic.clouddc.person.Person;
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

    @Autowired
    public LogServiceImpl(PersistenceRepository persistenceRepository, LogHistoryRepository logHistoryRepository) {
        this.persistenceRepository = persistenceRepository;
        this.logHistoryRepository = logHistoryRepository;
    }

    @Override
    public Persistence newPersistenceInitialization(String logMessageKey, Person person, String category) {
        Persistence persistence = new Persistence(person, category);
        LogHistory logHistory = new LogHistory(UtilService.getDATE(), UtilService.getTime(), person, persistence, UtilService.LOG_MESSAGE.get(logMessageKey), true);
        persistence.setLogHistoryList(List.of(logHistory));

        return persistence;
    }

    @Override
    public Persistence persistenceSetup(Person person, String category) {
        Persistence persistence = new Persistence();
        persistence.setPerson(person);
        persistence.setCategory(category);

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
    public List<Long> getSupervisorPmInterfaceEditFilePersiscentceIdList(String stringPmInterfaceId) {
        return logHistoryRepository.getPmInterfaceSupervisorEditedPersistenceList(stringPmInterfaceId);
    }

    @Override
    public void registerIndependentPersistence(String logMessage, Person persistencePerson, Person historyPerson, String category) {
        Persistence persistence = persistenceSetup(persistencePerson, category);
        historyUpdate(UtilService.getDATE(), UtilService.getTime(), logMessage, historyPerson, persistence);

        persistenceRepository.save(persistence);
    }
}
