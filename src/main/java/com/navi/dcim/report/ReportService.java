package com.navi.dcim.report;

import java.util.List;
import java.util.Optional;

public interface ReportService {

    Optional<DailyReport> findActive(boolean active);
    void setTodayReport();
    void saveAll(List<DailyReport> dailyReportList);


}
