package ir.tic.clouddc.center;

import ir.tic.clouddc.report.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemperatureRepository extends JpaRepository<Temperature, Integer> {
    boolean existsBydailyReportAndCenter(DailyReport dailyReport, Center center);
}
