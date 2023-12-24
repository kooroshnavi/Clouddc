package com.navi.dcim.notification;

import com.github.mfathi91.time.PersianDateTime;
import com.navi.dcim.person.Person;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TicgramNotificationService implements NotificationService {

    @Override
    public void sendSuccessLoginMessage(Person person, String ipAddress, LocalDateTime originDatetime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String persianDateTime = formatter.format(PersianDateTime.fromGregorian(originDatetime));
        String phone = person.getName();
        String message =
                        "ورود موفق" +
                        System.lineSeparator() +
                        "آدرس ماشین: " +
                        ipAddress +
                        System.lineSeparator() +
                        "تاریخ و ساعت: " +
                        persianDateTime +
                        System.lineSeparator() +
                        "سامانه مدیریت و نگه داری زیرساخت ابر";
    }
}
