package ir.tic.clouddc.utils;

import com.github.mfathi91.time.PersianDate;
import com.github.mfathi91.time.PersianDateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Map.entry;

public final class UtilService {

    private static LocalDate DATE;


    public static final Map<String, String> persianDay = Map.ofEntries(
            entry("Sat", "شنبه"),
            entry("Sun", "یکشنبه"),
            entry("Mon", "دوشنبه"),
            entry("Tue", "سه شنبه"),
            entry("Wed", "چهارشنبه"),
            entry("Thu", "پنج شنبه"),
            entry("Fri", "جمعه")
    );

    private static final List<String> LOG_MESSAGES = Arrays.asList(
            "Event - ایجاد گردش رخداد",  // 0
            "حذف پیوست گردش رخداد - Event", // 1
            "بروزرسانی گردش - PM", // 2
            "PM - حذف پیوست گردش کار", // 3
            "PM - ثبت دسته بندی", // 4
            "PM - ویرایش دسته بندی", // 5
            "PM - ثبت جدید", // 6
            "PM - حذف پیوست ", // 7
            "PM - ویرایش", // 8
            "PM - بارگذاری پیوست" // 9
    );


    public static String getCurrentDate() {
        var date = PersianDate.fromGregorian(LocalDate.now());
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
        dayName = persianDay.get(dayName);


        return (dayName + "    " + day + "     " + month.toString());
    }

    public static void setDate() {
        UtilService.DATE = LocalDate.now();
    }

    public static LocalDate getDATE() {
        return UtilService.DATE;
    }

    public static LocalTime getTime() {
        return LocalTime.now();
    }

    public static String getFormattedPersianDate(LocalDate date){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return dateFormatter.format(PersianDate.fromGregorian(date));
    }

    public static String getFormattedPersianDateTime(LocalDateTime dateTime){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        return dateTimeFormatter.format(PersianDateTime.fromGregorian(dateTime));

    }

}
