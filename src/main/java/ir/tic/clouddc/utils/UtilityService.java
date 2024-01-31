package ir.tic.clouddc.utils;

import com.github.mfathi91.time.PersianDate;
import com.github.mfathi91.time.PersianDateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Map.entry;

public final class UtilityService {
    private static LocalDate CurrentDate;
    static final Map<String, String> PERSIAN_DAY = Map.ofEntries(
            entry("Sat", "شنبه"),
            entry("Sun", "یکشنبه"),
            entry("Mon", "دوشنبه"),
            entry("Tue", "سه شنبه"),
            entry("Wed", "چهارشنبه"),
            entry("Thu", "پنج شنبه"),
            entry("Fri", "جمعه")
    );

    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    public static String getCurrentPersianDate() {
        var date = PersianDate.fromGregorian(LocalDate.now());
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
        dayName = PERSIAN_DAY.get(dayName);

        return (dayName + "    " + day + "     " + month.toString());
    }

    public static List<Object> getCalendarList() {
        //DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        CurrentDate = LocalDate.now();
        List<String> persianWeekDay = new ArrayList<>();
        List<LocalDate> georgianDate = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (!CurrentDate.plusDays(i).getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()).equals("Thu")
                    && !CurrentDate.plusDays(i).getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()).equals("Fri")) {
                persianWeekDay.add((PersianDate.fromGregorian(CurrentDate.plusDays(i)))
                        + " - "
                        + PERSIAN_DAY.get(CurrentDate.plusDays(i).getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault())));
                georgianDate.add(CurrentDate.plusDays(i));
            }
        }
        List<Object> calendars = new ArrayList<>();
        calendars.add(persianWeekDay);
        calendars.add(georgianDate);
        return calendars;
    }

    public String getPersianFormattedDate(LocalDate date) {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return pattern.format(PersianDate.fromGregorian(date));
    }

    public String getPersianFormattedDateTime(LocalDateTime dateTime) {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return pattern.format(PersianDateTime.fromGregorian(dateTime));
    }

}
