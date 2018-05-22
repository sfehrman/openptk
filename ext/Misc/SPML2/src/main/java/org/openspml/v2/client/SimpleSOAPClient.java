/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
 * 
 * This file is available and licensed under the following license:
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer. 
 * 
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 
 * 
 * Neither the name of Sun Microsystems nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific
 * prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/*
 * kudos to SOAPClient4XG
 */
package org.openspml.v2.client;

import org.openspml.v2.transport.RPCRouterMonitor;
import org.openspml.v2.util.FileUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;


public class SimpleSOAPClient {

    private static final String code_id = "$Id: SimpleSOAPClient.java,v 1.3 2008/11/12 17:08:26 gh202020 Exp $";

    private String _username;
    private String _password;

    private String _proxyHost;
    private int _proxyPort;

    private String _header = null;
    private String _bodyAttributes = null;

    private RPCRouterMonitor _monitor = null;
    
    private Integer _timeout;

    public void setHeader(String header) {
        _header = header;
    }

    public void setBodyAttributes(String attrs) {
        _bodyAttributes = attrs;
    }

    public void setMonitor(RPCRouterMonitor monitor) {
        _monitor = monitor;
    }

    public void setReadTimeout(int timeout) {
        _timeout = new Integer(timeout);
    }

    public String sendAndReceive(final URL url,
                                 final String action,
                                 final String xml) throws IOException {

        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) connection;

        if (_username != null && _password != null) {
            Authenticator.setDefault(new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(_username,
                                                      _password.toCharArray());
                }
            });
        }

        if (_proxyHost != null) {
            String host = System.getProperty("http.proxyHost");
            if (host == null) {
                System.setProperty("http.proxyHost", _proxyHost);
                System.setProperty("http.proxyPort", _proxyPort + "");
            }
            else {
                System.out.println("WARNING: Did not override system's http.proxyHost settings.");
            }
        }

        String soapXml = addSOAPEnvelopeIfMissing(xml);

        byte[] xmlBytes = soapXml.getBytes();

        // Set the appropriate HTTP parameters.
        httpConn.setRequestProperty("Content-Length",
                                    String.valueOf(xmlBytes.length));
        httpConn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
        if (action != null) {
            httpConn.setRequestProperty("SOAPAction", action);
        }
        if (_timeout!=null)
            httpConn.setReadTimeout(_timeout.intValue());
        httpConn.setRequestMethod("POST");
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);

        if (_monitor != null)
            _monitor.send(soapXml);

        OutputStream out = httpConn.getOutputStream();
        out.write(xmlBytes);
        out.close();

        InputStreamReader isr =
                new InputStreamReader(httpConn.getInputStream());
        BufferedReader in = new BufferedReader(isr);

        String inputLine;
        StringBuffer result = new StringBuffer();
        while ((inputLine = in.readLine()) != null)
            result.append(inputLine);

        in.close();

        String toReturn =  result.length() == 0 ? null : result.toString();

        if (_monitor != null)
            _monitor.receive(toReturn);

        return toReturn;
    }

    private String addSOAPEnvelopeIfMissing(String xml) {

        if (xml.indexOf("http://schemas.xmlsoap.org/soap/envelope/") > 0) {
            return xml;

        }

        StringBuffer buffer = new StringBuffer();
        if (_header == null) {
            String[] header = {"<?xml version='1.0' encoding='UTF-8'?>",
                               "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">",
                               "<SOAP-ENV:Header/>"};

            for (int k = 0; k < header.length; k++) {
                buffer.append(header[k]);
            }
        }
        else {
            buffer.append(_header);
        }

        if (_bodyAttributes == null) {
            buffer.append("<SOAP-ENV:Body>");
        }
        else {
            buffer.append("<SOAP-ENV:Body ");
            buffer.append(_bodyAttributes);
            buffer.append(">");
        }

        buffer.append(xml);

        String[] footer = {"</SOAP-ENV:Body>",
                           "</SOAP-ENV:Envelope>"};

        for (int k = 0; k < footer.length; k++) {
            buffer.append(footer[k]);
        }

        return buffer.toString();
    }

    public SimpleSOAPClient(String username,
                            String password,
                            String proxyHost,
                            int proxyPort) {

        this._username = username;
        this._password = password;

        this._proxyHost = proxyHost;
        this._proxyPort = proxyPort;

    }

    public SimpleSOAPClient(String username,
                            String password,
                            String proxyHost) {

        this(username, password, proxyHost, 8080);
    }

    public SimpleSOAPClient(String username, String password) {
        this(username, password, null);
    }

    public SimpleSOAPClient() {
        this(null, null, null);
    }

    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            System.err.println("Usage:  java SimpleSOAPClient " +
                               "http://soapURL soapEnvelopeFile.xml" +
                               "[action=<SOAPAction>] <username> <password>");
            System.err.println("SOAPAction is optional.");
            System.exit(1);
        }

        String SOAPUrl = args[0];
        String xmlFile2Send = args[1];

        String SOAPAction = "";
        String username = null;
        String password = null;

        for (int k = 2; k < args.length; k++) {
            if (args[k].startsWith("action=")) {
                SOAPAction = args[k].substring("action=".length());
            }
            else if (username == null) {
                username = args[k];
            }
            else if (password == null) {
                password = args[k];
            }
        }

        URL url = new URL(SOAPUrl);
        String xml = FileUtil.readFile(xmlFile2Send);

        SimpleSOAPClient ssc = new SimpleSOAPClient(username, password);
        String result = ssc.sendAndReceive(url,
                                           SOAPAction,
                                           xml);

        // Read the response and write it to standard out.
        System.out.println("Result is:\n" + result);
    }

}
