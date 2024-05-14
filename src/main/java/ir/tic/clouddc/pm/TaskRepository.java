package ir.tic.clouddc.pm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByActive(boolean active);

    List<Task> findByPmId(int pmId);

    List<Task> findByActiveAndTaskStatusId(boolean active, int statusId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.active = :active")
    long getTaskCountByActivation(@Param("active") boolean active);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.delay > :delay AND t.active = :active")
    long getDelayedActiveTaskCount(@Param("delay") int delay, @Param("active") boolean active);

    @Query("SELECT DISTINCT COUNT(t) FROM Task t WHERE t.successDate > :date AND t.active = :active")
    long getWeeklyFinishedTaskCount(@Param("date") LocalDateTime date, @Param("active") boolean active);


}
