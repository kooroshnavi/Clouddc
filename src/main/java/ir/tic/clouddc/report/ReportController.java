package ir.tic.clouddc.report;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/report")
public class ReportController {

    @GetMapping("/dailyReport")
    public String viewDailyReport() {

        return "dailyReportView";
    }
}
