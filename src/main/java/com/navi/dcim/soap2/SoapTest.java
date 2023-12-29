package com.navi.dcim.soap2;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/soap")
public class SoapTest {

    @GetMapping("/test")
    public String wsTest() {
        SoapClientService soapClientService = new SoapClientService("Hello", "09127016653");
        return soapClientService.getResponse();
    }
}
