package ir.tic.clouddc.utils;

import com.github.mfathi91.time.PersianDate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static java.util.Map.entry;

public final class UtilService {

    private static LocalDate DATE;
    private static Long TODAY_REPORT_ID;
    private static final List<String> FORM_CAPTCHA_CHALLENGE = List.of(
            "هفده پانزده",   // 0
            "ده بیست و سه نود و پنج",
            "چهل و یک هفده",
            "دوازده بیست و دو",
            "هشتاد و نه نود",
            "دویست و پنج دوصفر",
            "پانصد دویست",
            "هشتاد و هشت هفتاد",
            "یازده صدویک",
            "بیست و پنج هفده",
            "هفت هشت نه ده",
            "سیزده بیست و پنج",
            "نوزده پنجاه سی",
            "هزار و سیصد و هفتاد",
            "یازده نودوهفت",
            "نود و نه صد",  // 15
            "چهارده صفرسه", // 16
            "هجده نوزده بیست", //17
            "یازده سیزده صفریک",
            "شانزده هفتادوشش",
            "بیست و هفت صفرهشت",
            "چهارده صفرچهار",
            "دویست و پنجاه ده", //22
            "ششصد صدودوازده",
            "سی و سه پنجاه", //24
            "پانصد شصت بیست",
            "هشتادوشش  سیزده",
            "هشتاد و نه پنجاه و شش",
            "چهارده بیست و پنج", //28
            "شصت هفتاد ده",
            "دویست دویست",
            "نوزده نودویک",
            "ده بیست و نه", //32
            "سیزده چهل و شش",
            "دوازده چهل و پنج", //34
            "نودوهشت شصت",  //35
            "هفتادویک چهل و یک", //36
            "هفتادوچهار هشتاد", //37
            "چهل و هشت هشتاد",
            "پانزده سی و شش"
    );
    public static final Map<Integer, Integer> FORM_CAPTCHA_RESULT = Map.ofEntries(
            entry(0, 1715),
            entry(1, 102395),
            entry(2, 4117),
            entry(3, 1222),
            entry(4, 8990),
            entry(5, 20500),
            entry(6, 500200),
            entry(7, 8870),
            entry(8, 11101),
            entry(9, 2517),
            entry(10, 78910),
            entry(11, 1325),
            entry(12, 195030),
            entry(13, 1370),
            entry(14, 1197),
            entry(15, 99100),
            entry(16, 1403),
            entry(17, 181920),
            entry(18, 111301),
            entry(19, 1676),
            entry(20, 2708),
            entry(21, 1404),
            entry(22, 25010),
            entry(23, 600112),
            entry(24, 3350),
            entry(25, 5006020),
            entry(26, 8613),
            entry(27, 8956),
            entry(28, 1425),
            entry(29, 607010),
            entry(30, 200200),
            entry(31, 1991),
            entry(32, 1029),
            entry(33, 1346),
            entry(34, 1245),
            entry(35, 9860),
            entry(36, 7141),
            entry(37, 7480),
            entry(38, 4880),
            entry(39, 1536)
    );

    public static DTOForm createChallenge(DTOForm dtoForm) {
        var challengeIndex = new Random().nextInt(FORM_CAPTCHA_CHALLENGE.size());
        dtoForm.setChallenge(FORM_CAPTCHA_CHALLENGE.get(challengeIndex));
        dtoForm.setIndex(challengeIndex);

        return dtoForm;
    }

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
            entry("EventUpdate", "بروزرسانی رخداد"),
            entry("CatalogRegister", "ثبت کاتالوگ"),
            entry("CatalogUpdate", "بروزرسانی کاتالوگ"),
            entry("UnassignedDeviceRegister", "ثبت تجهیز جدید"),
            entry("RackDeviceOrderUpdated", "بروزرسانی جانمایی رک"),
            entry("StorageRegister", "ثبت ماژول جدید (هارد)"),
            entry("StorageProblematic", "اعلام خرابی استوریج"),
            entry("StorageClear", "رفع خرابی استوریج"),
            entry("StorageDisable", "حذف استوریج"),
            entry("DecreaseInventory", "کاهش موجودی مخزن")
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

    public static final Map<Integer, String> GENERAL_EVENT_CATEGORY_ID = Map.ofEntries(
            entry(1, "اختلال برق"),
            entry(2, "اختلال سیستم برودتی پکیج ها و فن ها"),
            entry(3, "مشکل درب و قفل"),
            entry(4, "اختلال سیستم روشنایی"),
            entry(5, "مفقودی هرگونه تجهیز، ماژول، دیگر وسایل و ابزار"),
            entry(6, "اختلال عملکرد تجهیزات ایمنی شامل اطفاء حریق و کپسول آتش نشانی"),
            entry(7, "بازدید از مرکز یا هرگونه مراسم مرتبط")
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
