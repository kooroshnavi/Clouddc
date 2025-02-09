package ir.tic.clouddc.dashboard;


import com.fasterxml.jackson.core.JsonProcessingException;
import ir.tic.clouddc.api.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardRestController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardRestController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/greeting")
    public String helloWorld() throws FileNotFoundException {

        return "Hello World";
    }

    @GetMapping("/ceph/cluster")
    public Response getCephClusterData() throws JsonProcessingException {
        return dashboardService.getCephClusterResponseList();
    }

    @GetMapping("/ceph/messenger/usage")
    public Response getCephUsageData() throws JsonProcessingException {
        return dashboardService.getCephMessengerUsageResponseList();
    }
}
