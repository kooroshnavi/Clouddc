package ir.tic.clouddc.log;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {

    @Query("SELECT w.persistence.id FROM Workflow w WHERE w.id : =workFlow")
    List<Long> fetchRelatedPersistenceIdList(long workFlow);
}
