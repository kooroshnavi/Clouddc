package ir.tic.clouddc.report;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
final class ReportServiceImpl implements ReportService {

    private ReportRepository reportRepository;

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
        if (yesterday.isPresent()) {
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

    @Override
    public List<DailyReport> getWeeklyReportObjects() {

        return reportRepository.getWeeklyReportObjects();
    }
}
