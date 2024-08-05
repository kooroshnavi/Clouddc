package ir.tic.clouddc.utils;

import com.github.mfathi91.time.PersianDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;

import static java.util.Map.entry;

public final class UtilService {

    private static final Logger log = LoggerFactory.getLogger(UtilService.class);
    private static LocalDate DATE;

    private static Long TODAY_REPORT_ID;

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
            entry("PmInterfaceUpdateSupervisorFile", "افزودن فایل به PmInterface توسط Supervisor"),
            entry("PmUpdate", "بروزرسانی Pm"),
            entry("SupervisorPmTermination", "بستن PmDetail توسط Supervisor"),
            entry("DisableAttachment", "حذف فایل پیوست"),
            entry("EventRegister", "ثبت رخداد"),
            entry("CatalogRegister", "ثبت کاتالوگ"),
            entry("CatalogUpdate", "بروزرسانی کاتالوگ")
    );

    public static final Map<Integer, String> PM_CATEGORY = Map.ofEntries(
            entry(1, "عمومی - مکان ها"),
            entry(2, "عمومی - تجهیزات")
    );

    public static final Map<Integer, Integer> PM_CATEGORY_ID = Map.ofEntries(
            entry(1, 1),
            entry(2, 1),
            entry(3, 1),
            entry(4, 1),
            entry(5, 2),
            entry(6, 2),
            entry(7, 2),
            entry(8, 2),
            entry(9, 2)
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


    public static String getFormattedPersianDayTime(LocalDate localDate, LocalTime localTime) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        var dayName = PERSIAN_DAY.get(localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()));
        var formattedTime = timeFormatter.format(localTime);

        return dayName + " - " + formattedTime;
    }

    public static void setTodayReportId(Long todayReportId) {
        TODAY_REPORT_ID = todayReportId;
    }

    public static Long getTodayReportId() {
        return TODAY_REPORT_ID;
    }
}
