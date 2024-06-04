package ir.tic.clouddc.pm;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PmDetailRepository extends JpaRepository<PmDetail, Integer> {
    Optional<GeneralPmDetail> findByTaskIdAndActive(long taskId, boolean active);

    List<GeneralPmDetail> findByTaskId(long taskId);

    @Query("SELECT t.task FROM TaskDetail t WHERE t.persistence.person.username = :username AND t.active = :active")
    List<Task> fetchActivePersonTaskList(@Param("username") String username, @Param("active") boolean active);

    @Query("SELECT P.persistence.id FROM PmDetail P WHERE P.pm.id = :pmId")
    List<Long> getPersistenceIdList(@Param("pmId") int pmId);

    @Query("SELECT p.persistence.person.username FROM PmDetail p WHERE p.pm.id = :pmId AND p.active = :active")
    String fetchOwnerUsername(@Param("pmId") int pmId, @Param("active") boolean active);

}
