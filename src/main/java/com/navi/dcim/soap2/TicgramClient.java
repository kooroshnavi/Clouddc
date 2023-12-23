package com.navi.dcim.soap2;

import jakarta.xml.soap.MimeHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.transport.TransportConstants;

import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;

@Configuration
public class TicgramClient {

    @Autowired
    private final WebServiceTemplate webServiceTemplate;

    public TicgramClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    private static final String MESSAGE =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                    "  <soap:Body>\n" +
                    "    <sendmessage xmlns=\"http://tempuri.org/\">\n" +
                    "      <us>string</us>\n" +
                    "      <ps>string</ps>\n" +
                    "      <text>string</text>\n" +
                    "      <tell>string</tell>\n" +
                    "    </sendmessage>\n" +
                    "  </soap:Body>\n" +
                    "</soap:Envelope>\n";


    public String customSendAndReceive() {
        webServiceTemplate.setDefaultUri("http://emas.tic.ir/sendingsmsto.asmx");
        StreamSource source = new StreamSource(new StringReader(MESSAGE));
        StreamResult result = new StreamResult(System.out);
        webServiceTemplate.setCheckConnectionForFault(true);
        webServiceTemplate
                .sendSourceAndReceiveToResult(
                        source,
                        webServiceMessage -> {
                            SaajSoapMessage soapMessage = (SaajSoapMessage) webServiceMessage;
                            MimeHeaders headers = soapMessage.getSaajMessage().getMimeHeaders();
                            headers.removeAllHeaders();
                            headers.addHeader(TransportConstants.HEADER_CONTENT_TYPE, "text/xml; charset=utf-8");
                            headers.addHeader("SOAPAction", "http://tempuri.org/sendmessage");
                        }
                        , result);

        return result.getOutputStream().toString();
    }

}
