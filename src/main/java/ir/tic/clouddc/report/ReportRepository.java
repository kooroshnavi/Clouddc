package ir.tic.clouddc.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<DailyReport, Long> {

    Optional<DailyReport> findByActive(boolean active);
/*
    @Query(value = """
            select *
            from Report.daily_report
            where date > (select dateadd(week, -1, getdate()))""", nativeQuery = true)
    List<DailyReport> getWeeklyReportObjects(); */

    @Query("SELECT r.id FROM DailyReport r WHERE r.active = :active")
    Long getActiveReportId(@Param("active") boolean active);

    @Query("SELECT date FROM DailyReport WHERE id IN :id")
    List<LocalDate> getWeeklyDateList(@Param("id") List<Long> weeklyIdList);

}
