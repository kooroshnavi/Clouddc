package ir.tic.clouddc.center;

import ir.tic.clouddc.report.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemperatureRepository extends JpaRepository<Temperature, Integer> {
    boolean existsBydailyReportAndCenter(DailyReport dailyReport, Center center);

    @Query("SELECT value FROM Temperature WHERE center = :center AND dailyReport = :report")
    List<Float> getDailytemperatureList(@Param("center") Center center, @Param("report") DailyReport report);

}
