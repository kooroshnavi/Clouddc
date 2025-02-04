package ir.tic.clouddc.dashboard;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardRest {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardRest(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/greeting")
    public String helloWorld() throws FileNotFoundException {

        return "Hello World";
    }

    @GetMapping("/ceph")
    public List<Response> getApiResult() throws JsonProcessingException {
        return dashboardService.getCephResponseList();
    }
}
