package ir.tic.clouddc.notification;

import com.github.mfathi91.time.PersianDateTime;
import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.soap2.SoapClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
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
        var person = personService.getPersonByUsername(personName);
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
    }

    @Override
    public void sendPmAssignedMessage(String address, String pmTitle, LocalDateTime originDatetime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String persianDateTime = formatter.format(PersianDateTime.fromGregorian(originDatetime));

        final String message =
                "وظیفه با عنوان: " +
                        pmTitle +
                        System.lineSeparator() +
                        "در تاریخ و ساعت: " +
                        persianDateTime +
                        System.lineSeparator() +
                        "در کارتابل شما قرار گرفت." +
                        System.lineSeparator();


        soapClientService.sendMessage(address, message);
    }


    @Override
    public void sendScheduleUpdateMessage(String personAddress, String logMessage) {
        soapClientService.sendMessage("09127016653", logMessage);
    }

    @Override
    public void sendExceptionMessage(String message, LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String persianDateTime = formatter.format(PersianDateTime.fromGregorian(dateTime));
        final String otpMessage = message +
                System.lineSeparator() +
                "@: " +
                persianDateTime +
                System.lineSeparator();

        soapClientService.sendMessage("09127016653", otpMessage);
    }

    @Override
    public void sendRegisterOTPMessage(String phoneNumber, String otpCode) {
        final String otpMessage = "کد ثبت نام" +
                System.lineSeparator() +
                otpCode +
                System.lineSeparator() +
                "اعتبار 10 دقیقه" +
                System.lineSeparator();

        soapClientService.sendMessage(phoneNumber, otpMessage);
    }

    @Override
    public void sendOTPMessage(String address, String otp, String machine, String date) {
        final String otpMessage =
                otp +
                        System.lineSeparator() +
                        "آدرس ماشین: " +
                        machine +
                        System.lineSeparator() +
                        "تاریخ و ساعت درخواست: " +
                        date +
                        System.lineSeparator() +
                        System.lineSeparator() +
                        "امکان استفاده مجدد تا 12 ساعت" +
                        System.lineSeparator();

        soapClientService.sendMessage(address, otpMessage);
    }
}
