package ir.tic.clouddc.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventDetailRepository extends JpaRepository<EventDetail, Long> {

    @Query("SELECT e.persistence.id FROM EventDetail e WHERE event.id = :eventId")
    List<Long> getPersistenceIdList(@Param("eventId") long event);
}
