package ir.tic.clouddc.center;

import ir.tic.clouddc.report.DailyReport;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CenterService {
    Center getCenter(int centerId);
    List<Center> getDefaultCenterList();
    List<Center> getCenterList();
    Model modelForCenterController(Model model);
    List<Temperature> saveDailyTemperature(TemperatureForm temperatureForm, DailyReport dailyReport);
    void setDailyTemperatureReport(DailyReport currentReport);
    List<Temperature> getTemperatureHistoryList();

    Map<Integer, Float> getWeeklyTemperature(List<LocalDate> weeklyDateList, int centerId);



}
