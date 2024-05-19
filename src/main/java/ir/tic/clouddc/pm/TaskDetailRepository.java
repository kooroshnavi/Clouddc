package ir.tic.clouddc.pm;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskDetailRepository extends JpaRepository<TaskDetail, Long> {
    Optional<TaskDetail> findByTaskIdAndActive(long id, boolean active);
    List<TaskDetail> findByTaskId(long taskId);
    @Query("SELECT t.task FROM TaskDetail t WHERE t.persistence.id IN :persistenceIdList")
    List<Task> fetchRelatedActivePersonTaskList(List<Integer> persistenceIdList);

}
