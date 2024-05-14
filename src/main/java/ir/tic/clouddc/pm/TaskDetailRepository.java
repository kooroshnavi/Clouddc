package ir.tic.clouddc.pm;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskDetailRepository extends JpaRepository<TaskDetail, Long> {
    List<TaskDetail> findAllByPerson_IdAndActive(int id, boolean active);
    Optional<TaskDetail> findByTaskIdAndActive(long id, boolean active);
}
