package com.navi.dcim.soap2;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

@Slf4j
public class SoapClientService {

    private static final String PRINCIPAL = "khadamatnovin.cen";

    private static final String CREDENTIAL = "yhnYHNujmUJM123!@#";

    private static final String WEBSERVICE_URL = "http://emas.tic.ir/sendingsmsto.asmx";

    private final String message;

    private final String address;

    private String response;

    public SoapClientService(String message, String address) {
        this.message = message + System.lineSeparator() + "     'مدیریت و پایش زیرساخت ابر'     ";
        this.address = address;
        this.sendMessage(this.message, this.address);
    }

    public String getResponse() {
        return response;
    }

    private void sendMessage(String message, String address) {


        URL url;
        URLConnection connection;
        HttpURLConnection httpConn;
        String responseString;
        String outputString = "";
        OutputStream out;
        InputStreamReader isr;
        BufferedReader in;

        final String xmlInput =
                "<soap12:Envelope xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                        "  <soap12:Body>\n" +
                        "    <sendmessage xmlns=\"http://tempuri.org/\">\n" +
                        "      <us>" + PRINCIPAL + "</us>\n" +
                        "      <ps>" + CREDENTIAL + "</ps>\n" +
                        "      <text>" + message +
                        "                        </text>\n" +
                        "      <tell>" + address + "</tell>\n" +
                        "    </sendmessage>\n" +
                        "  </soap12:Body>\n" +
                        "</soap12:Envelope>";

        String responseMessage = null;
        try {
            url = new URL(WEBSERVICE_URL);
            connection = url.openConnection();
            httpConn = (HttpURLConnection) connection;

            byte[] buffer = xmlInput.getBytes();

            httpConn.setRequestProperty("Content-Type",
                    "text/xml; charset=utf-8");

            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setConnectTimeout(3000);
            out = httpConn.getOutputStream();
            out.write(buffer);
            out.close();

            // Read the response and write it to standard out.
            isr = new InputStreamReader(httpConn.getInputStream());
            in = new BufferedReader(isr);

            while ((responseString = in.readLine()) != null) {
                outputString = outputString + responseString;
            }

            log.info(outputString);
            // Get the response from the web service call
            Document document = parseXmlFile(outputString);
            NodeList nodeLst = document.getElementsByTagName("sendmessageResponse");
            final String webServiceResponse = nodeLst.item(0).getTextContent();
            responseMessage = webServiceResponse;


        } catch (java.net.SocketTimeoutException socketTimeoutException) {
            log.error(socketTimeoutException.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        this.response = responseMessage;
    }

    private Document parseXmlFile(String input) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(input));
            return documentBuilder.parse(inputSource);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }

}
