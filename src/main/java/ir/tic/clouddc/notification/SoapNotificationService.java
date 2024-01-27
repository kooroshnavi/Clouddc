package ir.tic.clouddc.notification;

import com.github.mfathi91.time.PersianDateTime;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.soap2.SoapClientService;
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
        var person = personService.getPerson(personName);
        var address = person.getAddress().getValue();

        final String message =
                "ورود موفق" +
                        System.lineSeparator() +
                        "آدرس ماشین: " +
                        ipAddress +
                        System.lineSeparator() +
                        "تاریخ و ساعت: " +
                        persianDateTime +
                        System.lineSeparator();

        soapClientService.sendMessage(address, message);
        log.info(soapClientService.getResponse());
    }

    @Override
    public void sendNewTaskAssignedMessage(String address, String taskTitle, LocalDateTime originDatetime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String persianDateTime = formatter.format(PersianDateTime.fromGregorian(originDatetime));

        final String message =
                "وظیفه جدید با عنوان: " +
                        taskTitle +
                        System.lineSeparator() +
                        "در تاریخ و ساعت: " +
                        persianDateTime +
                        System.lineSeparator() +
                        "ثبت و در کارتابل شما قرار گرفت." +
                        System.lineSeparator();


        soapClientService.sendMessage(address, message);
        log.info(soapClientService.getResponse());
    }

    @Override
    public void sendActiveTaskAssignedMessage(String address, String taskTitle, int delay, LocalDateTime originDatetime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String persianDateTime = formatter.format(PersianDateTime.fromGregorian(originDatetime));
        switch (delay) {
            case 0: {
                final String message =
                        "وظیفه فعال با عنوان: " +
                                taskTitle +
                                System.lineSeparator() +
                                "در تاریخ و ساعت: " +
                                persianDateTime +
                                System.lineSeparator() +
                                "با انتساب دیگر همکاران در کارتابل شما قرار گرفت." +
                                System.lineSeparator();

                soapClientService.sendMessage(address, message);
                log.info(soapClientService.getResponse());
                break;
            }
            default:
                final String message =
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

                soapClientService.sendMessage(address, message);
                log.info(soapClientService.getResponse());
                break;
        }

    }

    @Override
    public void sendScheduleUpdateMessage(String personAddress, String logMessage) {
        soapClientService.sendMessage("09127016653", logMessage);
        log.info(soapClientService.getResponse());
    }

    @Override
    public void sendOTPMessage(String address, String otp, String machine, String date) {
        final String otpMessage = "همکار گرامی، کد ورود شما:" +
                System.lineSeparator() +
                otp +
                System.lineSeparator() +
                "آدرس ماشین: " +
                machine +
                System.lineSeparator() +
                "تاریخ و ساعت درخواست: " +
                date +
                System.lineSeparator() +
                "این کد تا 12 ساعت آینده به دفعات قابل استفاده می باشد." +
                System.lineSeparator();
        soapClientService.sendMessage(address, otpMessage);
        log.info(soapClientService.getResponse());
    }
}
