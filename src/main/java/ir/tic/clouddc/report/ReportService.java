package ir.tic.clouddc.report;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReportService {

    Optional<DailyReport> findActive(boolean active);

    DailyReport setCurrentReport();
    void saveAll(List<DailyReport> dailyReportList);
    List<LocalDate> getWeeklyDate();

    LocalDate getWeeklyOffsetDate();

}
