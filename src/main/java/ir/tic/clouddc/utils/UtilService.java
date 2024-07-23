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

    private static int TODAY_REPORT_ID;

    public static final Map<String, String> PERSIAN_DAY = Map.ofEntries(
            entry("Sat", "شنبه"),
            entry("Sun", "یکشنبه"),
            entry("Mon", "دوشنبه"),
            entry("Tue", "سه شنبه"),
            entry("Wed", "چهارشنبه"),
            entry("Thu", "پنج شنبه"),
            entry("Fri", "جمعه")
    );

    public static final Map<String, String> LOG_MESSAGE = Map.ofEntries(
            entry("PmInterfaceRegister", "ثبت PmInterface جدید"),
            entry("PmInterfaceUpdate", "بروزرسانی PmInterface"),
            entry("PmUpdate", "بروزرسانی Pm"),
            entry("SupervisorPmTermination", "بستن PmDetail توسط Supervisor"),
            entry("DisableAttachment", "حذف فایل پیوست"),
            entry("EventUpdate", "ثبت و بروزرسانی یک رخداد")
    );

    public static String getCurrentDate() {
        var date = PersianDate.fromGregorian(LocalDate.now());
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
        dayName = PERSIAN_DAY.get(dayName);

        return (dayName + "    " + day + "     " + month);
    }

    public static LocalDate validateNextDue(LocalDate nextDue) {
        if (nextDue.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()).equals("Thu")) {
            return nextDue.plusDays(2);
        } else if (nextDue.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()).equals("Fri")) {
            return nextDue.plusDays(1);
        } else {
            return nextDue;
        }
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

    public static String getFormattedPersianDate(LocalDate date) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return dateFormatter.format(PersianDate.fromGregorian(date));
    }

    public static String getFormattedPersianDateTime(LocalDateTime dateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        return dateTimeFormatter.format(PersianDateTime.fromGregorian(dateTime));

    }

    public static String getFormattedPersianDayTime(LocalDate localDate, LocalTime localTime) {
        return UtilService
                .PERSIAN_DAY
                .get(localDate.getDayOfWeek()
                        .getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " - " + localTime);

    }

    public static void setTodayReportId(int todayReportId) {
        TODAY_REPORT_ID = todayReportId;
    }

    public static int getTodayReportId() {
        return TODAY_REPORT_ID;
    }
}
