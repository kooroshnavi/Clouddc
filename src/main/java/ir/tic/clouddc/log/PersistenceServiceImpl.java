package ir.tic.clouddc.log;

import ir.tic.clouddc.person.Person;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PersistenceServiceImpl implements PersistenceService {

    private final PersistenceRepository persistenceRepository;

    @Autowired
    public PersistenceServiceImpl(PersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public Persistence setupNewPersistence(char messageId, Person person) {
        Persistence persistence = new Persistence();
        LogHistory logHistory = new LogHistory(UtilService.getDATE(), UtilService.getTime(), person, messageId, persistence, true);
        return new Persistence();
    }

    @Override
    public void updatePersistence(Persistence persistence, char messageId, int personId) {
        LogHistory logHistory = new LogHistory(UtilService.getDATE(), UtilService.getTime(), new Person(personId), messageId, persistence, true);
        persistence.getLogHistoryList().stream().filter(LogHistory::isActive).findAny().get().setActive(false);
        persistenceRepository.save(persistence);
    }
}
