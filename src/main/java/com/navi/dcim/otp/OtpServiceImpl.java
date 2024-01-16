package com.navi.dcim.otp;

import com.github.mfathi91.time.PersianDateTime;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.navi.dcim.notification.NotificationService;
import com.navi.dcim.person.Address;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OtpServiceImpl implements OtpService {

    private static final Integer EXPIRE_MIN = 7;

    private LoadingCache<String,String> otpCache;

    private NotificationService notificationService;

    @Autowired
    public OtpServiceImpl() {
        otpCache = CacheBuilder.newBuilder()
                .expireAfterWrite(EXPIRE_MIN, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public String load(String s) {
                        return "";
                    }
                });

        this.notificationService = notificationService;
    }

    @Override
    public void sendOtpMessage(Address address, String machine, LocalDateTime requestDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String persianDateTime = formatter.format(PersianDateTime.fromGregorian(requestDateTime));
        String otp = getRandomOTP(address.getValue());
        log.info(otp);
        String message = "همکار گرامی، لطفا کد ذیل را جهت ورود وارد نمایید." +
                System.lineSeparator() +
                otp +
                System.lineSeparator() +
                "آدرس ماشین: " +
                machine +
                System.lineSeparator() +
                "تاریخ و ساعت درخواست: " +
                persianDateTime +
                System.lineSeparator() +
                "این کد تا هفت دقیقه پس از ارسال درخواست معتبر است." +
                System.lineSeparator();

        notificationService.sendOTPMessage(address.getValue(), message);


    }

    private String getRandomOTP(String value) {
        String otp = new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
        otpCache.put(value, otp);
        return otp;

    }


}
