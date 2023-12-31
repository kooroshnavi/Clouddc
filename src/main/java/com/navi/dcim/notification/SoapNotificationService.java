package com.navi.dcim.notification;

import com.github.mfathi91.time.PersianDateTime;
import com.navi.dcim.person.PersonService;
import com.navi.dcim.soap2.SoapClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class SoapNotificationService implements NotificationService {

    private final SoapClientService soapClientService;

    private final PersonService personService;
    @Autowired
    public SoapNotificationService(SoapClientService soapClientService, @Lazy PersonService personService) {
        this.soapClientService = soapClientService;
        this.personService = personService;
    }

    @Override
    public void sendSuccessLoginMessage(String personName, String ipAddress, LocalDateTime originDatetime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String persianDateTime = formatter.format(PersianDateTime.fromGregorian(originDatetime));
        var person  = personService.getPerson(personName);
        var address = person.getAddress().getValue();

        final String successLoginMessage =
                "ورود موفق" +
                        System.lineSeparator() +
                        "آدرس ماشین: " +
                        ipAddress +
                        System.lineSeparator() +
                        "تاریخ و ساعت: " +
                        persianDateTime +
                        System.lineSeparator();

        soapClientService.sendMessage(successLoginMessage, address);
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
                        "ثبت و در کارتابل شما قرار گرفت." +
                        System.lineSeparator();


        soapClientService.sendMessage(newTaskAssignMessage, personAddress);
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
                                "با انتساب دیگر همکاران در کارتابل شما قرار گرفت." +
                                System.lineSeparator();

                soapClientService.sendMessage(activeAssignMessage, personAddress);
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
                                "با انتساب دیگر همکاران در کارتابل شما قرار گرفت. " +
                                System.lineSeparator() +
                                "تاخیر در اتمام انجام این وظیفه برابر با " +
                                delay +
                                " روز می باشد." +
                                System.lineSeparator();

                soapClientService.sendMessage(activeAssignMessage, personAddress);
                log.info(soapClientService.getResponse());
                break;
        }

    }

    @Override
    public void sendScheduleUpdateMessage(String personAddress, String logMessage) {
        final String scheduleLogMessage = logMessage;
        soapClientService.sendMessage(scheduleLogMessage, "09127016653");
        log.info(soapClientService.getResponse());
    }
}
