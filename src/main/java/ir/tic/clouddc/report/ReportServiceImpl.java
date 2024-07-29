package ir.tic.clouddc.report;

import ir.tic.clouddc.document.FileService;
import ir.tic.clouddc.pm.PmService;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableScheduling
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    private final PmService pmService;


    @Autowired
    ReportServiceImpl(ReportRepository reportRepository, PmService pmService) {
        this.reportRepository = reportRepository;
        this.pmService = pmService;
    }

    @Scheduled(cron = "0 5 0 * * SAT,SUN,MON,TUE,WED")
    public void startMidnightScheduling() {
        UtilService.setDate();
        pmService.updateTodayPmList();
    }

/*
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

        UtilService.setTodayReportId(reportRepository.getActiveReportId(true));

        return today;
    }*/


}
