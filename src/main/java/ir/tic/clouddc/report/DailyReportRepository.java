package ir.tic.clouddc.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {

    @Query("select report from DailyReport report where report.active")
    DailyReport fetchActiveReport();
}
