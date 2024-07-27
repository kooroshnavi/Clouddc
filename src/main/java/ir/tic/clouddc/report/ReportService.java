package ir.tic.clouddc.report;

import java.util.Optional;

public interface ReportService {

    Optional<DailyReport> findActive(boolean active);

    DailyReport setCurrentReport();
}
