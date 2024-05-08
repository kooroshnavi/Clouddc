package ir.tic.clouddc.report;

import ir.tic.clouddc.center.CenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    private final CenterService centerService;

    @Autowired
    ReportServiceImpl(ReportRepository reportRepository, CenterService centerService) {
        this.reportRepository = reportRepository;
        this.centerService = centerService;
    }

    @Override
    public Optional<DailyReport> findActive(boolean active) {
        return reportRepository.findByActive(active);
    }

    @Override
    public void setCurrentReport() {
        List<DailyReport> dailyReportList = new ArrayList<>();
        Optional<DailyReport> yesterday = findActive(true);
        if (yesterday.isPresent()) {
            centerService.setDailyTemperatureReport(yesterday.get());
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
    public List<LocalDate> getWeeklyDate() {
        int activeReportId = reportRepository.getActiveReportId(true);
        List<Integer> weeklyIdList = new ArrayList<>();
        for (int i = activeReportId - 1; i > activeReportId - 7; i--) {
            weeklyIdList.add(i);
        }

        List<LocalDate> weeklyDateList = reportRepository.getWeeklyDateList(weeklyIdList);
        return weeklyDateList;
    }

    @Override
    public LocalDate getWeeklyOffsetDate() {
        int activeReportId = reportRepository.getActiveReportId(true);
        int weeklyOffsetReportId = activeReportId - 5;
        return reportRepository.findById(weeklyOffsetReportId).get().getDate();
    }

}
