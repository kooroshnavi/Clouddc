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
    public Persistence setupNewPersistence(LocalDate date, LocalTime time, char messageId, Person person, boolean active) {
        Persistence persistence = new Persistence();
        LogHistory logHistory = new LogHistory(date, time, person, messageId, persistence, active);
        return new Persistence();
    }

    @Override
    public void updatePersistence(LocalDate date, LocalTime time, Persistence persistence, char messageId, boolean active) {
        Optional<LogHistory> logHistory = persistence.getLogHistoryList().stream().filter(LogHistory::isActive).findFirst();
        if (logHistory.isPresent()) {
            if (logHistory.get().getDate() == null) {
                logHistory.get().setDate(date);
                logHistory.get().setTime(time);
            }
            logHistory.get().setActive(active);
        }
        persistenceRepository.save(persistence);
    }

    @Override
    public Person getAssignedPerson(long persistenceId) {
        return logHistoryRepository.fetchTaskDetailPersonName(persistenceId);
    }

    @Override
    public List<Integer> getActivePersonPersistenceIdList(int personId, boolean active) {
        return logHistoryRepository.fetchActivePersonPersistenceIdList(personId, active);
    }
}
