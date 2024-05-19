package ir.tic.clouddc.log;

import ir.tic.clouddc.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogHistoryRepository extends JpaRepository<LogHistory, Long> {


    @Query("SELECT TOP(1) l.person FROM LogHistory l WHERE l.persistence.id = :persistenceId")
    Person fetchTaskDetailPersonName(@Param("persistenceId") long persistenceId);

    @Query("SELECT l.persistence.id FROM LogHistory l WHERE l.person.id = :personId AND l.active = :active")
    List<Integer> fetchActivePersonPersistenceIdList(@Param("personId") int personId, @Param("active") boolean active);
}
