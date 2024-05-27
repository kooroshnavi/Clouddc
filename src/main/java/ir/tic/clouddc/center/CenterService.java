package ir.tic.clouddc.center;

import ir.tic.clouddc.report.DailyReport;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;

public interface CenterService {
    Salon getSalon(long salonId);
    List<Salon> getSalonList();
    List<Center> getCenterList();
    Model modelForCenterController(Model model);
    List<Temperature> saveDailyTemperature(TemperatureForm temperatureForm, DailyReport dailyReport);
    void setDailyTemperatureReport(DailyReport currentReport);
    List<Temperature> getTemperatureHistoryList();
    List<Float> getWeeklyTemperature(List<LocalDate> weeklyDateList, int centerId);
    List<String> getAllDataCenterNameList();
    List<String> getSalonNameList();
    List<Rack> getRackList();
}
