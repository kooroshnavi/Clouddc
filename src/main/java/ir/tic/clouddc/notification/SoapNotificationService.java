package ir.tic.clouddc.notification;

import com.github.mfathi91.time.PersianDateTime;
import ir.tic.clouddc.soap2.SoapClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SoapNotificationService implements NotificationService {

    private final SoapClientService soapClientService;

    @Autowired
    public SoapNotificationService(SoapClientService soapClientService) {
        this.soapClientService = soapClientService;
    }

    @Override
    public void sendSuccessLoginMessage(String personAddress, String ipAddress, LocalDateTime originDatetime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String persianDateTime = formatter.format(PersianDateTime.fromGregorian(originDatetime));
        final String message =
                "ورود موفق" +
                        System.lineSeparator() +
                        "آدرس ماشین: " +
                        ipAddress +
                        System.lineSeparator() +
                        "تاریخ و ساعت: " +
                        persianDateTime +
                        System.lineSeparator();

        soapClientService.sendMessage(personAddress, message);
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
        final String otpMessage = "کد ثبت نام:" +
                System.lineSeparator() +
                otpCode +
                System.lineSeparator() +
                "اعتبار 10 دقیقه" +
                System.lineSeparator();

        soapClientService.sendMessage(phoneNumber, otpMessage);
    }

    @Override
    public void sendPersonWelcomingMessage(String personName, String phoneNumber, char role) {
        String welcomingMessage;
        if (role == '0' || role == '1') {
            welcomingMessage =
                    "همکار گرامی، " +
                            personName + "،" +
                            " با سلام و احترام، خوش آمدید." +
                            System.lineSeparator() +
                            System.lineSeparator() +
                            "ثبت نام تان انجام شد. نام کاربری شماره تلفن همراه و کد ورود را از طریق تیک گرام می فرستیم." +
                            System.lineSeparator() +
                            "در این سامانه اطلاعات و رخدادهای مرتبط با تجهیزات شبکه ابر زیرساخت، ثبت، بروزرسانی و گزارش گیری می گردد. " +
                            System.lineSeparator() +
                            "لذا خواهشمندست در ثبت اطلاعات هر بخش دقت لازم را به عمل آورید. " +
                            System.lineSeparator() +
                            "توسعه و بهبود فعلا ادامه دارد. نظرات شما ارزشمند است. آنها را بگویید چه رودررو چه پشت سر" +
                            System.lineSeparator() +
                            "در حال حاضر صرفا در شبکه داخلی TIC در دسترس هستیم." +
                            System.lineSeparator() +
                            System.lineSeparator() +
                            "اداره کل خدمات و فناوری های نوین - معاونت فناوری اطلاعات" +
                            System.lineSeparator() +
                            "Clouddc.tic.ir";

        } else if (role == '6') {
            welcomingMessage =
                    "همکار گرامی، " +
                            personName + "،" +
                            " با سلام و احترام، خوش آمدید." +
                            System.lineSeparator() +
                            System.lineSeparator() +
                            "ثبت نام انجام شد. نام کاربری شماره تلفن همراه و کد ورود را از طریق تیک گرام می فرستیم." +
                            System.lineSeparator() +
                            "صدور و مدیریت توکن وب سرویس داشبوردهای ابری از طریق این سامانه امکان پذیر است." +
                            System.lineSeparator() +
                            "در حال حاضر صرفا در شبکه داخلی TIC در دسترس هستیم." +
                            System.lineSeparator() +
                            "توسعه و بهبود فعلا ادامه دارد. نظرات شما ارزشمند است. آنها را بگویید چه رودررو چه پشت سر" +
                            System.lineSeparator() +
                            System.lineSeparator() +
                            "اداره کل خدمات و فناوری های نوین - معاونت فناوری اطلاعات" +
                            System.lineSeparator() +
                            "Clouddc.tic.ir";

        } else {
            welcomingMessage =
                    "همکار گرامی، " +
                            personName + "،" +
                            " با سلام و احترام، خوش آمدید." +
                            System.lineSeparator() +
                            System.lineSeparator() +
                            "ثبت نام شما انجام شد. نام کاربری شماره تلفن همراه و کد ورود از طریق تیک گرام ارسال خواهد شد." +
                            System.lineSeparator() +
                            "در این سامانه اطلاعات و رخدادهای مرتبط با تجهیزات شبکه ابر زیرساخت، ثبت، بروزرسانی و گزارش گیری می گردد. " +
                            System.lineSeparator() +
                            "* توسعه ادامه دارد. خواهشمندست بازخوردهای خود را مطرح فرمایید." +
                            System.lineSeparator() +
                            "در حال حاضر دسترسی صرفا در بستر شبکه داخلی TIC امکان پذیر است." +
                            System.lineSeparator() +
                            System.lineSeparator() +
                            "اداره کل خدمات و فناوری های نوین - معاونت فناوری اطلاعات" +
                            System.lineSeparator() +
                            "Clouddc.tic.ir";

        }
        soapClientService.sendMessage(phoneNumber, welcomingMessage);
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
