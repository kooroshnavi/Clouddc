package ir.tic.clouddc.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import ir.tic.clouddc.api.data.DataService;
import ir.tic.clouddc.api.response.Response;
import ir.tic.clouddc.api.token.TokenService;
import ir.tic.clouddc.cloud.CloudService;
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
@RequestMapping("/api")
public class ApiController {

    private final TokenService tokenService;

    private final DataService dataService;

    private final CloudService cloudService;

    @Autowired
    public ApiController(TokenService tokenService, DataService dataService, CloudService cloudService) {
        this.tokenService = tokenService;
        this.dataService = dataService;
        this.cloudService = cloudService;
    }

    @GetMapping("/ceph/cluster")
    public ResponseEntity<Response> getCephClusterData(HttpServletRequest request) throws JsonProcessingException {
        var response = dataService.getCephClusterDataResponse();
        tokenService.postRequestRecord(request, response.getStatus());

        if (Objects.equals(response.getStatus(), "OK")) {
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
        }
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/ceph/messenger/usage")
    public ResponseEntity<Response> getCephMessengerUsageData(HttpServletRequest request) throws JsonProcessingException {
        var response = dataService.getCephMessengerUsageDataResponse();
        tokenService.postRequestRecord(request, response.getStatus());

        if (Objects.equals(response.getStatus(), "OK")) {
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
        }
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/ceph/xas/usage")
    public ResponseEntity<Response> getXasUsageData(HttpServletRequest request) {
        var response = cloudService.getXasCephUsageData();
        tokenService.postRequestRecord(request, response.getStatus());

        if (Objects.equals(response.getStatus(), "OK")) {
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
        }
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @GetMapping("/ceph/sedad/usage")
    public ResponseEntity<Response> getSedadUsageData(HttpServletRequest request) {
        var response = cloudService.getSedadUsageData();
        tokenService.postRequestRecord(request, response.getStatus());

        if (Objects.equals(response.getStatus(), "OK")) {
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
        }
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
