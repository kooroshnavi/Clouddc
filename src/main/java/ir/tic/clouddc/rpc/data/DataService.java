package ir.tic.clouddc.rpc.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import ir.tic.clouddc.rpc.response.Response;

public interface DataService {

    Response getCephClusterDataResponse() throws JsonProcessingException;

    Response getCephMessengerUsageDataResponse() throws JsonProcessingException;
}
