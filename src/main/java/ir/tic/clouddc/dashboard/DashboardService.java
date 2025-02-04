package ir.tic.clouddc.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface DashboardService {

    List<Response> getCephResponseList() throws JsonProcessingException;
}
