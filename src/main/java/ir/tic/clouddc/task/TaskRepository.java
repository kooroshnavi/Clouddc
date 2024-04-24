package ir.tic.clouddc.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByActive(boolean active);

    List<Task> findByActiveAndTaskStatusId(boolean active, int statusId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.active = :active")
    long getNotActiveTaskCount(@Param("active") boolean active);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.delay = :delay AND t.active = :active")
    long getNotActiveWithNoDelayTaskCount(@Param("delay") int delay, @Param("active") boolean active);


}
