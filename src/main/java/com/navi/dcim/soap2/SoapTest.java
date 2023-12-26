package com.navi.dcim.soap2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/soap")
public class SoapTest {

    private final TicgramClient ticgramClient;

    @Autowired
    public SoapTest(TicgramClient ticgramClient) {
        this.ticgramClient = ticgramClient;
    }
    @GetMapping("/test")
    public String wsTest(){
       return ticgramClient.sendMessage();
    }
}
