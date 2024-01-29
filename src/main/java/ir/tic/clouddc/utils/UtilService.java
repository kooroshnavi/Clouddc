package ir.tic.clouddc.utils;

import com.github.mfathi91.time.PersianDate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Map.entry;

public final class UtilService {
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

    public static String getCurrentDate() {
        var date = PersianDate.fromGregorian(LocalDate.now());
        var year = date.getYear();
        var month = date.getMonth().getPersianName();
        var day = date.getDayOfMonth();
        var dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
        dayName = PERSIAN_DAY.get(dayName);

        return (dayName + "    " + day + "     " + month.toString());
    }

    public static List<String> getCalendarDate() {
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        CurrentDate = LocalDate.now();
        List<String> persianWeekDay = new ArrayList<>();
        for (int i = 0; i < 10; i++) {

        }
        return null;
    }


}
