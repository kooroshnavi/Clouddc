package ir.tic.clouddc.report;

import ir.tic.clouddc.document.FileService;
import ir.tic.clouddc.notification.NotificationService;
import ir.tic.clouddc.pm.PmService;
import ir.tic.clouddc.resource.ResourceService;
import ir.tic.clouddc.utils.UtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@EnableScheduling
public class ReportServiceImpl implements ReportService {

    private final ResourceService resourceService;

    private final PmService pmService;

    private final FileService fileService;

    private final NotificationService notificationService;

    private final DailyReportRepository dailyReportRepository;

    @Autowired
    ReportServiceImpl(ResourceService resourceService, PmService pmService, FileService fileService, NotificationService notificationService, DailyReportRepository dailyReportRepository) {
        this.resourceService = resourceService;
        this.pmService = pmService;
        this.fileService = fileService;
        this.notificationService = notificationService;
        this.dailyReportRepository = dailyReportRepository;
    }

    @Scheduled(cron = "0 5 0 * * SAT,SUN,MON,TUE,WED")
    public void startMidnightScheduling() {
        UtilService.setDate();
        var yesterdayReport = dailyReportRepository.fetchActiveReport();
        yesterdayReport.setActive(false);
        UtilService.setTodayReportID(yesterdayReport.getId() + 1);
        DailyReport todayReport = new DailyReport();
        todayReport.setActive(true);
        todayReport.setDate(UtilService.getDATE());
        dailyReportRepository.saveAll(List.of(yesterdayReport, todayReport));

        String pmSchedulerResult = pmService.updateTodayPmList();
        String removalFileResult = fileService.scheduleDocumentRemoval(UtilService.getDATE());
        String removalDeviceResult = resourceService.scheduleUnassignedDeviceRemoval();

        var scheduleNotificationMessage = pmSchedulerResult
                + System.lineSeparator()
                + removalFileResult
                + System.lineSeparator()
                + removalDeviceResult;

        notificationService.sendScheduleUpdateMessage("09127016653", scheduleNotificationMessage);
    }

    @Override
    public DailyReport getReferencedTodayReport(long todayReportID) {
        return dailyReportRepository.getReferenceById(todayReportID);
    }
}
