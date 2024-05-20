package ir.tic.clouddc.center;

import ir.tic.clouddc.report.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemperatureRepository extends JpaRepository<Temperature, Integer> {
    boolean existsBydailyReportAndSalon(DailyReport dailyReport, Salon salon);

    @Query("SELECT value FROM Temperature WHERE salon = :salon AND dailyReport = :report")
    List<Float> getDailytemperatureList(@Param("salon") Salon salon, @Param("report") DailyReport report);

}
