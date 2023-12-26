package com.navi.dcim.soap2;

import org.springframework.context.annotation.Configuration;
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

@Configuration
public class TicgramClient {


    public String sendMessage() {

        String wsURL = "http://www.dneonline.com/calculator.asmx";
        URL url;
        URLConnection connection;
        HttpURLConnection httpConn;
        String responseString;
        String outputString = "";
        OutputStream out;
        InputStreamReader isr;
        BufferedReader in;

        String xmlInput =
                "<soap12:Envelope xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                        "  <soap12:Body>\n" +
                        "    <Add xmlns=\"http://tempuri.org/\">\n" +
                        "      <intA>" + 20 +"</intA>\n" +
                        "      <intB>" + 20 +"</intB>\n" +
                        "    </Add>\n" +
                        "  </soap12:Body>\n" +
                        "</soap12:Envelope>";

        String responseMessage = null;
        try {
            url = new URL(wsURL);
            connection = url.openConnection();
            httpConn = (HttpURLConnection) connection;

            byte[] buffer = new byte[xmlInput.length()];
            buffer = xmlInput.getBytes();

            String SOAPAction = "";
            // Set the appropriate HTTP parameters.
           /* httpConn.setRequestProperty("Content-Length", String
                    .valueOf(buffer.length));*/
            httpConn.setRequestProperty("Content-Type",
                    "text/xml; charset=utf-8");

            httpConn.setRequestProperty("Host",
                    "www.dneonline.com");

            //httpConn.setRequestProperty("SOAPAction", SOAPAction);
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            out = httpConn.getOutputStream();
            out.write(buffer);
            out.close();

            // Read the response and write it to standard out.
            isr = new InputStreamReader(httpConn.getInputStream());
            in = new BufferedReader(isr);

            while ((responseString = in.readLine()) != null) {
                outputString = outputString + responseString;
            }
            System.out.println(outputString);
            System.out.println("");

            // Get the response from the web service call
            Document document = parseXmlFile(outputString);
            NodeList nodeLst = document.getElementsByTagName("AddResponse");
            String webServiceResponse = nodeLst.item(0).getTextContent();
            responseMessage = "The response from the web service call is : " + webServiceResponse;

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return responseMessage;
    }

    private Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
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
