package ir.tic.clouddc.report;

import ir.tic.clouddc.center.CenterService;
import ir.tic.clouddc.pm.PmService;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@EnableScheduling
class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    private final CenterService centerService;

    private final PmService pmService;


    @Autowired
    ReportServiceImpl(ReportRepository reportRepository, CenterService centerService, PmService pmService) {
        this.reportRepository = reportRepository;
        this.centerService = centerService;
        this.pmService = pmService;
    }

    @Scheduled(cron = "0 5 0 * * SAT,SUN,MON,TUE,WED")
    public void startMidnightScheduling() {
        UtilService.setDate();
        var todayReport = setCurrentReport();
        pmService.updateTodayTasks(todayReport);
        centerService.setDailyTemperatureReport(findActive(true).get());

    }

    @Override
    public Optional<DailyReport> findActive(boolean active) {
        return reportRepository.findByActive(active);
    }


    @Override
    public DailyReport setCurrentReport() {
        List<DailyReport> dailyReportList = new ArrayList<>();
        Optional<DailyReport> yesterday = findActive(true);
        if (yesterday.isPresent()) {

            yesterday.get().setActive(false);
            dailyReportList.add(yesterday.get());
        }
        DailyReport today = new DailyReport();
        today.setDate(UtilService.getDATE());
        today.setActive(true);
        dailyReportList.add(today);
        reportRepository.saveAll(dailyReportList);
        UtilService.setTodayReportId(yesterday.get().getId() + 1);
        return today;
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
