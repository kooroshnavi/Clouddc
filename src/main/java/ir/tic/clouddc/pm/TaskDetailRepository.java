package ir.tic.clouddc.pm;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskDetailRepository extends JpaRepository<GeneralTaskDetail, Long> {
    Optional<GeneralTaskDetail> findByTaskIdAndActive(long taskId, boolean active);

    List<GeneralTaskDetail> findByTaskId(long taskId);

    @Query("SELECT t.task FROM TaskDetail t WHERE t.persistence.person.username = :username AND t.active = :active")
    List<Task> fetchActivePersonTaskList(@Param("username") String username, @Param("active") boolean active);

    @Query("SELECT t.persistence.id FROM TaskDetail t WHERE t.task.id = :taskId")
    List<Long> getPersistenceIdList(@Param("taskId") long taskId);

    @Query("SELECT t.persistence.person.username FROM TaskDetail t WHERE t.task.id = :taskId AND t.active = :active")
    String fetchOwnerUsername(@Param("taskId") long taskId, @Param("active") boolean active);

}
