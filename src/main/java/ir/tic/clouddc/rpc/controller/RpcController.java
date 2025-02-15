package ir.tic.clouddc.rpc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import ir.tic.clouddc.rpc.data.DataService;
import ir.tic.clouddc.rpc.response.Response;
import ir.tic.clouddc.rpc.token.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/rpc")
public class RpcController {
    private final TokenService tokenService;

    private final DataService dataService;

    @Autowired
    public RpcController(TokenService tokenService, DataService dataService) {
        this.tokenService = tokenService;
        this.dataService = dataService;
    }

    @GetMapping("/ceph/cluster")
    public ResponseEntity<Response> getCephClusterData(HttpServletRequest request) throws JsonProcessingException {
        var response = dataService.getCephClusterDataResponse();
        tokenService.postRequestRecord(request, response.getStatus());

        if (Objects.equals(response.getStatus(), "OK")){
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
        }
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/ceph/messenger/usage")
    public ResponseEntity<Response> getCephMessengerUsageData(HttpServletRequest request) throws JsonProcessingException {
        var response = dataService.getCephMessengerUsageDataResponse();
        tokenService.postRequestRecord(request, response.getStatus());

        if (Objects.equals(response.getStatus(), "OK")){
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
        }
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
