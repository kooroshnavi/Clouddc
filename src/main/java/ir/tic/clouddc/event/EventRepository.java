package ir.tic.clouddc.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByActive(boolean active);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.active = :active")
    long getActiveEventCount(@Param("active") boolean active);


   /* @Query("SELECT DISTINCT COUNT(e) FROM Event e WHERE e.eventDetailList.get[:0] > :date")
    long getWeeklyFinishedTaskCount(@Param("date") LocalDate weeklyOffsetDate);*/
}
