package ir.tic.clouddc.api.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import ir.tic.clouddc.api.response.Response;

public interface DataService {

    Response getCephClusterDataResponse() throws JsonProcessingException;

    Response getCephMessengerUsageDataResponse() throws JsonProcessingException;

    Response getXasCephUsageData();

}
