package ir.tic.clouddc.log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogHistoryRepository extends JpaRepository<LogHistory, Long> {

    @Query("select l.persistence.id from LogHistory l where l.logMessage = :stringPmInterfaceId")
    List<Long> getPmInterfaceSupervisorEditedPersistenceList(@Param("stringPmInterfaceId") String stringPmInterfaceId);
}
