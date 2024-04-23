package ir.tic.clouddc.center;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/center")
public class CenterController {

    private final CenterService centerService;

    @Autowired
    public CenterController(CenterService centerService) {
        this.centerService = centerService;
    }

    @GetMapping("/temperature/history")
    public String getTemperatureHistory(Model model) {
        model.addAttribute("temperatureHistoryList", centerService.getTemperatureHistoryList());
        return "temperatureHistoryView";
    }
    @GetMapping("/temperature/form")
    public String getTemperatureForm(Model model) {
        model.addAttribute("temperatureForm", new TemperatureForm());
        return "dailyTemperatureForm";
    }

    @PostMapping("/temperature/form")
    public String submitTemperatureForm(Model model, @ModelAttribute("temperatureForm") TemperatureForm temperatureForm) {
        if (temperatureForm.getSalon1Temp().isBlank() && temperatureForm.getSalon2Temp().isBlank()) {
            model.addAttribute("temperatureForm", new TemperatureForm());
            return "dailyTemperatureForm";
        }
        model.addAttribute("temperatureHistoryList", centerService.saveDailyTemperature(temperatureForm));
        return "temperatureHistoryView";
    }


    @ModelAttribute
    public void addAttributes(Model model) {
        centerService.modelForCenterController(model);
    }

}
