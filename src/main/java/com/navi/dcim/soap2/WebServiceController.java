package com.navi.dcim.soap2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.stream.StreamResult;

@RestController
@RequestMapping("/soap")
public class WebServiceController {

    @Autowired
    private final TicgramClient ticgramClient;

    public WebServiceController(TicgramClient ticgramClient) {
        this.ticgramClient = ticgramClient;
    }

    @GetMapping("/test")
    public String getCapital(){
        return ticgramClient.customSendAndReceive();
    }
}
