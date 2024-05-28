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
public class LogServiceImpl implements LogService {

    private final PersistenceRepository persistenceRepository;
    private final LogHistoryRepository logHistoryRepository;

    @Autowired
    public LogServiceImpl(PersistenceRepository persistenceRepository, LogHistoryRepository logHistoryRepository) {
        this.persistenceRepository = persistenceRepository;
        this.logHistoryRepository = logHistoryRepository;
    }


    @Override
    public Persistence persistenceSetup(Person person) {
        return new Persistence(person);
    }

    @Override
    public void historyUpdate(LocalDate date, LocalTime time, int logMessageId, Person person, Persistence persistence) {
        List<LogHistory> logHistoryList = new ArrayList<>();
        LogHistory logHistory = new LogHistory(date, time, person, persistence, new LogMessage(logMessageId), true);
        logHistoryList.add(logHistory);
        Optional<LogHistory> currentHistory = persistence.getLogHistoryList().stream().filter(LogHistory::isLast).findFirst();
        if (currentHistory.isPresent()) {
            currentHistory.get().setLast(false);
            logHistoryList.add(currentHistory.get());
        }
        logHistoryRepository.saveAll(logHistoryList);
    }


}
