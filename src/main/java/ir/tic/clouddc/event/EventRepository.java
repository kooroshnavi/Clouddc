package ir.tic.clouddc.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByActive(boolean active);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.active = :active")
    long getActiveEventCount(@Param("active") boolean active);

    @Query(value = """
            SELECT count(Event.event.id) as event_count
            from Event.event_type as t
            left join Event.event
            on (t.id = event.event_type_id)
            group by t.id""", nativeQuery = true)
    List<Long> getEventTypeCount();

    @Query("SELECT DISTINCT COUNT(e) FROM Event e WHERE e.eventDate > :date")
    long getWeeklyFinishedTaskCount(@Param("date") LocalDateTime weeklyOffsetDateTime);
}
