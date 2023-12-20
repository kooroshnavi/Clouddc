package com.navi.dcim.soap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/soap")
public class SoapController {

    @Autowired
    private TicgramClient ticgramClient;

    @GetMapping("/test")
    public void test() {
        ticgramClient.getCustomerInfo();
    }
}
