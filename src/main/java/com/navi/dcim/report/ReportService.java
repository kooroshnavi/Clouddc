package com.navi.dcim.report;

import java.util.List;

public interface ReportService {

    DailyReport findActive(boolean active);
    void saveAll(List<DailyReport> dailyReportList);


}
