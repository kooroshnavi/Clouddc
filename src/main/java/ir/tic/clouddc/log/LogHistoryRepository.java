package ir.tic.clouddc.log;

import ir.tic.clouddc.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogHistoryRepository extends JpaRepository<LogHistory, Long> {


    @Query("SELECT l.persistence.id FROM LogHistory l WHERE l.person.id = :personId AND l.active = :active")
    List<Integer> fetchActivePersonPersistenceIdList(@Param("personId") int personId, @Param("active") boolean active);

    @Query(value = "SELECT Top 1 person FROM LogHistory  WHERE persistence_id = :persistenceId AND active = :active", nativeQuery = true)
    Person fetchRelatedCurrentPerson(@Param("persistenceId") long persistenceId, @Param("active") boolean active);


}
