package ir.tic.clouddc.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class DashboardController {


    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @RequestMapping(value = {"", "/",}, method = {RequestMethod.GET})
    public String mainDashboard(Model model) {
        dashboardService.prepareDashboardData(model);
        return "index";
    }


    @ModelAttribute
    public void defaultPageAttributes(Model model) {
        dashboardService.setDefaultAttributes(model);
    }
}
