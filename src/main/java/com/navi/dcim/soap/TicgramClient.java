package com.navi.dcim.soap;

import com.sun.net.httpserver.Headers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapHeader;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.io.StringReader;
import java.util.Map;

@Configuration
@Slf4j
public class TicgramClient {

/*
    public void customSendAndReceive() {
        StreamSource source = new StreamSource(new StringReader(MESSAGE));
        StreamResult result = new StreamResult(System.out);
        SoapHeader soapHeader =
        webServiceTemplate.mas("http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso",
                source, result);

        System.out.println(result);
    }*/

    private final static String wsUrl = "http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso";

    private static final String MESSAGE =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "  <soap:Body>\n" +
                    "    <CapitalCity xmlns=\"http://www.oorsprong.org/websamples.countryinfo\">\n" +
                    "      <sCountryISOCode>US</sCountryISOCode>\n" +
                    "    </CapitalCity>\n" +
                    "  </soap:Body>\n" +
                    "</soap:Envelope>";
    public void getCustomerInfo() {

        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/soap+xml");

            HttpEntity<String> request = new HttpEntity<>(String.format(MESSAGE),  headers);

            String result = restTemplate.postForObject(wsUrl, request, String.class);

            System.out.println(result);

            if(!result.contains("<SOAP-ENV:Fault>")) {
                // Do SOAP Envelope body parsing here
            }
        }
        catch(Exception e) {
            log.error(e.getMessage(), e);
        }

    }
}
