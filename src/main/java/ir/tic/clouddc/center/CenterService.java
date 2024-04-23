package ir.tic.clouddc.center;

import org.springframework.ui.Model;

import java.util.List;

public interface CenterService {
    Center getCenter(int centerId);
    List<Center> getDefaultCenterList();
    List<Center> getCenterList();
    Model modelForCenterController(Model model);
    List<Temperature> saveDailyTemperature(TemperatureForm temperatureForm);
    List<Temperature> getTemperatureHistoryList();


}
