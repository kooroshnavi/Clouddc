package ir.tic.clouddc.log;

import ir.tic.clouddc.person.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PersistenceServiceImpl implements PersistenceService {

    private final PersistenceRepository persistenceRepository;
    private final LogHistoryRepository logHistoryRepository;

    @Autowired
    public PersistenceServiceImpl(PersistenceRepository persistenceRepository, LogHistoryRepository logHistoryRepository) {
        this.persistenceRepository = persistenceRepository;
        this.logHistoryRepository = logHistoryRepository;
    }


    @Override
    public Persistence persistenceSetup(LocalDate date, LocalTime time, char messageId, Person person) {
        Persistence persistence = new Persistence(person);
        LogHistory logHistory = new LogHistory(date, time, person, messageId, persistence, true);
        return persistence;
    }

    @Override
    public void historyUpdate(LocalDate date, LocalTime time, char newHistoryMessageId, Person person, Persistence persistence) {
        Optional<LogHistory> currentHistory = persistence.getLogHistoryList().stream().filter(LogHistory::isLast).findFirst();
        if (currentHistory.isPresent()) {
            if (currentHistory.get().getDate() == null) { /// TaskDetail
                currentHistory.get().setDate(date);
                currentHistory.get().setTime(time);
            } else {
                LogHistory logHistory = new LogHistory(date, time, person, newHistoryMessageId, persistence, true);
                persistence.getLogHistoryList().add(logHistory);
            }
            currentHistory.get().setLast(false);
        }
        persistenceRepository.save(persistence);
    }

    @Override
    public List<Integer> getActivePersonPersistenceIdList(int personId, boolean active) {
        return logHistoryRepository.fetchActivePersonPersistenceIdList(personId, active);
    }

    @Override
    public Person getRelatedCurrentPerson(long persistenceId, boolean active) {
        return logHistoryRepository.fetchRelatedCurrentPerson(persistenceId, true);
    }
}
