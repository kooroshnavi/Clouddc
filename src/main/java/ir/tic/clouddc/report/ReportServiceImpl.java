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

@Slf4j
@Service
@EnableScheduling
public class ReportServiceImpl implements ReportService {

    private final ResourceService resourceService;

    private final PmService pmService;

    private final FileService fileService;

    private final NotificationService notificationService;

    @Autowired
    ReportServiceImpl(ResourceService resourceService, PmService pmService, FileService fileService, NotificationService notificationService) {
        this.resourceService = resourceService;
        this.pmService = pmService;
        this.fileService = fileService;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 5 0 * * SAT,SUN,MON,TUE,WED")
    public void startMidnightScheduling() {
        UtilService.setDate();
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
}
