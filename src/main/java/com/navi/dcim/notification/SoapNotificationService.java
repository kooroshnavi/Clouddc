package com.navi.dcim.notification;

import com.github.mfathi91.time.PersianDateTime;
import com.navi.dcim.soap2.SoapClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class SoapNotificationService implements NotificationService {


    @Override
    public void sendSuccessLoginMessage(String personAddress, String ipAddress, LocalDateTime originDatetime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String persianDateTime = formatter.format(PersianDateTime.fromGregorian(originDatetime));

        final String successLoginMessage =
                "ورود موفق" +
                        System.lineSeparator() +
                        "آدرس ماشین: " +
                        ipAddress +
                        System.lineSeparator() +
                        "تاریخ و ساعت: " +
                        persianDateTime +
                        System.lineSeparator();

        SoapClientService soapClientService = new SoapClientService(successLoginMessage, "09127016653");
        log.info(soapClientService.getResponse());
    }

    @Override
    public void sendNewTaskAssignedMessage(String personAddress, String taskTitle, LocalDateTime originDatetime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String persianDateTime = formatter.format(PersianDateTime.fromGregorian(originDatetime));

        final String newTaskAssignMessage =
                "وظیفه جدید با عنوان: " +
                        taskTitle +
                        System.lineSeparator() +
                        "در تاریخ و ساعت: " +
                        persianDateTime +
                        System.lineSeparator() +
                        "تعریف و در کارتابل شما قرار گرفت." +
                        System.lineSeparator();


        SoapClientService soapClientService = new SoapClientService(newTaskAssignMessage, personAddress);
        log.info(soapClientService.getResponse());
    }

    @Override
    public void sendActiveTaskAssignedMessage(String personAddress, String taskTitle, int delay, LocalDateTime originDatetime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String persianDateTime = formatter.format(PersianDateTime.fromGregorian(originDatetime));
        switch (delay) {
            case 0: {
                final String activeAssignMessage =
                        "وظیفه فعال با عنوان: " +
                                taskTitle +
                                System.lineSeparator() +
                                "در تاریخ و ساعت: " +
                                persianDateTime +
                                System.lineSeparator() +
                                "از طریق انتساب دیگر همکاران در کارتابل شما قرار گرفت." +
                                System.lineSeparator();

                SoapClientService soapClientService = new SoapClientService(activeAssignMessage, personAddress);
                log.info(soapClientService.getResponse());
                break;
            }
            default:
                final String activeAssignMessage =
                        "وظیفه فعال با عنوان: " +
                                taskTitle +
                                System.lineSeparator() +
                                "در تاریخ و ساعت: " +
                                persianDateTime +
                                System.lineSeparator() +
                                "از طریق انتساب دیگر همکاران در کارتابل شما قرار گرفت. " +
                                System.lineSeparator() +
                                "تاخیر اتمام انجام این وظیفه تا امروز برابر با " +
                                delay +
                                " روز می باشد." +
                                System.lineSeparator();

                SoapClientService soapClientService = new SoapClientService(activeAssignMessage, personAddress);
                log.info(soapClientService.getResponse());
                break;
        }

    }

    @Override
    public void sendScheduleUpdateMessage(String personAddress, String logMessage) {
        final String scheduleLogMessage = logMessage;
        SoapClientService soapClientService = new SoapClientService(scheduleLogMessage, "09127016653");
        log.info(soapClientService.getResponse());
    }
}
