package com.navi.dcim.soap2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

@Configuration
public class WebServiceConfiguration {

    @Bean
    public SaajSoapMessageFactory messageFactory(){
        return new SaajSoapMessageFactory();
    }

    @Bean
    public WebServiceTemplate webServiceTemplate(){
        return new WebServiceTemplate(messageFactory());
    }
}
