package ir.tic.clouddc.log;

import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
public class PersistenceServiceImpl implements PersistenceService {


    @Override
    public Persistence setupNewPersistence(char messageId, Person person) {
        if (messageId == 'S') {
            return new Persistence();
        } else {
            Persistence persistence = new Persistence();
            LogHistory logHistory = new LogHistory(UtilService.getDATE(), LocalTime.now(), person, messageId, persistence);
            persistence.setLogHistoryList(List.of(logHistory));
            return persistence;
        }
    }
}
