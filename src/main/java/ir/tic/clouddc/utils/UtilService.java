package ir.tic.clouddc.utils;

import com.github.mfathi91.time.PersianDate;
import com.github.mfathi91.time.PersianDateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;

import static java.util.Map.entry;

public final class UtilService {

    private static LocalDate DATE;

    private static int TODAY_REPORT_ID ;


    public static final Map<String, String> persianDay = Map.ofEntries(
            entry("Sat", "شنبه"),
            entry("Sun", "یکشنبه"),
            entry("Mon", "دوشنبه"),
            entry("Tue", "سه شنبه"),
            entry("Wed", "چهارشنبه"),
            entry("Thu", "پنج شنبه"),
            entry("Fri", "جمعه")
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
        return LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    public static String getFormattedPersianDate(LocalDate date){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return dateFormatter.format(PersianDate.fromGregorian(date));
    }

    public static String getFormattedPersianDateTime(LocalDateTime dateTime){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        return dateTimeFormatter.format(PersianDateTime.fromGregorian(dateTime));

    }

    public static void setTodayReportId(int todayReportId) {
        TODAY_REPORT_ID = todayReportId;
    }

    public static int getTodayReportId() {
        return TODAY_REPORT_ID;
    }
}
