package com.navi.dcim.report;

import com.navi.dcim.model.DailyReport;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReportService {

    DailyReport findActive(boolean active);
    void saveAll(List<DailyReport> dailyReportList);


}
