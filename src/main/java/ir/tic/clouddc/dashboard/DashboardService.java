package ir.tic.clouddc.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import ir.tic.clouddc.api.Response;

public interface DashboardService {

    Response getCephClusterResponseList() throws JsonProcessingException;

    Response getCephMessengerUsageResponseList() throws JsonProcessingException;
}
