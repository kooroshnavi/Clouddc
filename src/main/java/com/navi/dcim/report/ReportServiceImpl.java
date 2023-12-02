package com.navi.dcim.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
final class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Optional<DailyReport> findActive(boolean active) {
        return reportRepository.findByActive(active);
    }

    @Override
    public void setTodayReport() {
        List<DailyReport> dailyReportList = new ArrayList<>();
        Optional<DailyReport> yesterday = findActive(true);
        if (yesterday.isPresent()){
            System.out.println("yesterday is present");
            yesterday.get().setActive(false);
            dailyReportList.add(yesterday.get());
        }
        DailyReport today = new DailyReport();
        today.setDate(LocalDate.now());
        today.setActive(true);
        dailyReportList.add(today);
        reportRepository.saveAll(dailyReportList);
    }

    @Override
    public void saveAll(List<DailyReport> dailyReportList) {
        reportRepository.saveAll(dailyReportList);
    }
}
