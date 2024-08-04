package ir.tic.clouddc.report;

import ir.tic.clouddc.document.FileService;
import ir.tic.clouddc.notification.NotificationService;
import ir.tic.clouddc.pm.PmService;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;

@Slf4j
@Service
@EnableScheduling
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    private final PmService pmService;

    private final FileService fileService;

    private final NotificationService notificationService;


    @Autowired
    ReportServiceImpl(ReportRepository reportRepository, PmService pmService, FileService fileService, NotificationService notificationService) {
        this.reportRepository = reportRepository;
        this.pmService = pmService;
        this.fileService = fileService;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 5 0 * * SAT,SUN,MON,TUE,WED")
    public void startMidnightScheduling() {
        UtilService.setDate();
        String pmSchedulerResult = pmService.updateTodayPmList();
        String removalFileResult = fileService.scheduleDocumentRemoval(UtilService.getDATE());
        var scheduleNotificationMessage = pmSchedulerResult + System.lineSeparator() + removalFileResult;

        notificationService.sendScheduleUpdateMessage("09127016653", scheduleNotificationMessage);
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
