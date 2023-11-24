package com.navi.dcim.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
final class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public DailyReport findActive(boolean active) {
        return reportRepository.findByActive(active);
    }

    @Override
    public void saveAll(List<DailyReport> dailyReportList) {
        reportRepository.saveAll(dailyReportList);
    }
}
