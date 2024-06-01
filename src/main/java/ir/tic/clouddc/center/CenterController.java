package ir.tic.clouddc.center;

import ir.tic.clouddc.report.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/center")
public class CenterController {

    private final CenterService centerService;

    private final ReportService reportService;

    @Autowired
    public CenterController(CenterService centerService, ReportService reportService) {
        this.centerService = centerService;
        this.reportService = reportService;
    }

    @GetMapping("/overview")
    public String showCenterLandingPage(Model model) {

        centerService.getCenterLandingPageModel(model);
        return "centerLandingPage";
    }

    @GetMapping("/location/{locationId}/detail") // Covers Room Rack and salon
    public String showLocationDetail(Model model, @RequestParam("locationId") int locationId) {
        centerService.getLocationDetailModel(locationId, model);
        return "locationView";
    }

    @GetMapping("/temperature/history")
    public String getTemperatureHistory(Model model) {
        model.addAttribute("temperatureHistoryList", centerService.getTemperatureHistoryList());
        return "temperatureHistoryView";
    }

    @GetMapping("/temperature/salon/form")
    public String showSalonTemperatureForm(Model model) {
        model.addAttribute("temperatureForm", new TemperatureForm());
        return "dailyTemperatureForm";
    }

    @GetMapping("/temperature/rack/form")
    public String showRackTemperatureForm(Model model) {
        model.addAttribute("temperatureForm", new TemperatureForm());
        return "dailyTemperatureForm";
    }

    @PostMapping("/temperature/form")
    public String submitTemperatureForm(Model model, @ModelAttribute("temperatureForm") TemperatureForm temperatureForm) {
        model.addAttribute("temperatureHistoryList", centerService.saveDailyTemperature(temperatureForm, reportService.findActive(true).get()));
        return "temperatureHistoryView";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        centerService.modelForCenterController(model);
    }

}
