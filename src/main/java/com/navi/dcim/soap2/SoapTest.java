package com.navi.dcim.soap2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/soap")
public class SoapTest {

    private final SoapClientService soapClientService;
    @Autowired
    public SoapTest(SoapClientService soapClientService) {
        this.soapClientService = soapClientService;
    }

    @GetMapping("/test")
    public String wsTest() {
        soapClientService.sendMessage("Hello", "09127016653");
        return soapClientService.getResponse();
    }
}
