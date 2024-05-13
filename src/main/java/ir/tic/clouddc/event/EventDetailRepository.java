package ir.tic.clouddc.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventDetailRepository extends JpaRepository<EventDetail, Long> {

    @Query("SELECT id FROM EventDetail WHERE event = :event")
    List<Long> getPersistenceIdList(@Param("event") Event event);
}
