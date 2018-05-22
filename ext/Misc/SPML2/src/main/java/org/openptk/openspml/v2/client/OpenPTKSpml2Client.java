/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at /OPENSPML_V2_TOOLKIT.LICENSE
 * or http://www.openspml.org/v2/licensing.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at /OPENSPML_V2_TOOLKIT.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/*
 * Copyright 2009 Sun Microsystems, Inc.  All rights reserved.
 * Use is subject to license terms.
 */

package org.openptk.openspml.v2.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openspml.v2.msg.XMLMarshaller;
import org.openspml.v2.msg.XMLUnmarshaller;
import org.openspml.v2.msg.spml.Request;
import org.openspml.v2.msg.spml.Response;
import org.openspml.v2.profiles.DSMLProfileRegistrar;
import org.openspml.v2.transport.RPCRouterMonitor;
import org.openspml.v2.util.Spml2Exception;
import org.openspml.v2.util.Spml2ExceptionWithResponse;
import org.openspml.v2.util.xml.OpenPTKReflectiveXMLMarshaller; // OpenPTK
import org.openspml.v2.util.xml.XmlParser;
import org.openspml.v2.util.xml.XmlUtil;
import org.openspml.v2.util.xml.ReflectiveDOMXMLUnmarshaller;
import org.openspml.v2.util.xml.ObjectFactory;
import org.openspml.v2.util.xml.ObjectFactory.ProfileRegistrar;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class OpenPTKSpml2Client
//===================================================================
{
   private static final String HTTP_POST = "POST";
   private static final String HTTP_CONTENT_LENGTH = "Content-Length";
   private static final String HTTP_CONTENT_TYPE = "Content-Type";
   private static final String HTTP_SOAP_ACTION = "SOAPAction";
   private static final String TYPE_XML_UTF8 = "text/xml; charset=UTF-8";
   private static final String XML_DECLARE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
   private static final String SOAPENV_ENVELOPE_BEGIN =
      "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
      + "xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" "
      + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
      + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
   private static final String SOAPENV_ENVELOPE_END = "</soapenv:Envelope>";
   private static final String SOAPENV_HEADER_BEGIN = "<soapenv:Header>";
   private static final String SOAPENV_HEADER_END = "</soapenv:Header>";
   private static final String SOAPENV_BODY_BEGIN = "<soapenv:Body>";
   private static final String SOAPENV_BODY_END = "</soapenv:Body>";
   private static final String SOAPELEMENT_BEGIN = "<sOAPElement>";
   private static final String SOAPELEMENT_END = "</sOAPElement>";
   private static final String OPENPTK_NAMESPACE = "http://xmlns.oracle.com/OIM/provisioning";
   private static final String OPENPTK_USER_BEGIN = "<wsa1:OIMUser soapenv:mustUnderstand=\"0\" "
      + "xmlns:wsa1=\"" + OPENPTK_NAMESPACE + "\">";
   private static final String OPENPTK_USER_END = "</wsa1:OIMUser>";
   private static final String OPENPTK_USER_ID_BEGIN = "<wsa1:OIMUserId "
      + "xmlns:wsa1=\"" + OPENPTK_NAMESPACE + "\">";
   private static final String OPENPTK_USER_ID_END = "</wsa1:OIMUserId>";
   private static final String OPENPTK_USER_PASSWORD_BEGIN = "<wsa1:OIMUserPassword "
      + "xmlns:wsa1=\"" + OPENPTK_NAMESPACE + "\">";
   private static final String OPENPTK_USER_PASSWORD_END = "</wsa1:OIMUserPassword>";
   private static final String OPENPTK_PROCESSREQUEST_BEGIN = "<m:processRequest "
      + "xmlns:m=\"" + OPENPTK_NAMESPACE + "\">";
   private static final String OPENPTK_PROCESSREQUEST_END = "</m:processRequest>";
   private static final String OPENPTK_PROCESSREQUESTRESPONSE = "processRequestResponse";
   private boolean _trace = false;
   private String _username = null;
   private String _password = null;
   private String _action = null;
   private String _proxyHost = null;
   private int _proxyPort = 8080;
   private RPCRouterMonitor _monitor = null;
   private URL _url = null;
   private static final ProfileRegistrar mDSMLRegistrar = new DSMLProfileRegistrar();

   //----------------------------------------------------------------
   public OpenPTKSpml2Client(final String url) throws Spml2Exception
   //----------------------------------------------------------------
   {
      this(url, null, null);
      return;
   }

   //----------------------------------------------------------------
   public OpenPTKSpml2Client(final String url, final String username,
      final String password) throws Spml2Exception
   //----------------------------------------------------------------
   {

      this(url, username, password, null, 8080);
      return;
   }

   //----------------------------------------------------------------
   public OpenPTKSpml2Client(final String url, final String username,
      final String password, final String proxyHost, final int proxyPort) throws Spml2Exception
   //----------------------------------------------------------------
   {
      _username = username;
      _password = password;
      _proxyHost = proxyHost;
      _proxyPort = proxyPort;

      try
      {
         _url = new URL(url);
      }
      catch (MalformedURLException e)
      {
         throw new Spml2Exception(e);
      }

      ObjectFactory.getInstance().register(mDSMLRegistrar);

      return;
   }

   //----------------------------------------------------------------
   public synchronized void setSOAPAction(final String action)
   //----------------------------------------------------------------
   {
      _action = action;
      return;
   }

   //----------------------------------------------------------------
   public synchronized void setTrace(final boolean b)
   //----------------------------------------------------------------
   {
      _trace = b;
      return;
   }

   //----------------------------------------------------------------
   public synchronized void setMonitor(final RPCRouterMonitor m)
   //----------------------------------------------------------------
   {
      _monitor = m;
      return;
   }

   /**
    * Send an XmlObject as the body of a SOAP request. <p> We use an XmlObject
    * because XmlBeans makes it rather difficult to use anything else (to
    * narrow the type to a particular type in the schema.)  You should be
    * passing instances of the 'RequestType' enclosing 'Document' objects, e.g.
    * ListTargetsDocument and AddRequestDocument.  (These do NOT have a common
    * base class, and using RequestType sub-interfaces makes for odd tricks...
    * like setting a request type to a Document...) <p> Not accounting for SOAP
    * faults...
    *
    * @param req An XmlOBject (*Document interface) that is the body of the
    *            request.
    * @return one of the *ResponseDocument types from the xml beans bindings.
    * @throws Spml2Exception
    * @throws Spml2ExceptionWithResponse
    */
   //----------------------------------------------------------------
   public synchronized Response send(final Request req) throws Spml2Exception, Spml2ExceptionWithResponse
   //----------------------------------------------------------------
   {
      String requestXML = null;
      String responseXML = null;
      String payloadXML = null;
      XMLMarshaller marshaller = null;
      XMLUnmarshaller unmarshaller = null;
      Response response = null;

      marshaller = new OpenPTKReflectiveXMLMarshaller(); // OpenPTK
      requestXML = req.toXML(marshaller);

      responseXML = this.send(requestXML);

      if (_trace)
      {
         this.trace(responseXML);
      }

      payloadXML = this.getResponseContent(responseXML);
      unmarshaller = new ReflectiveDOMXMLUnmarshaller();
      response = (Response) unmarshaller.unmarshall(payloadXML);

//      this.throwErrors(response);

      return response;
   }

   //----------------------------------------------------------------
   public synchronized String sendRequest(final Request req) throws Spml2Exception
   //----------------------------------------------------------------
   {
      String xml = null;
      XMLMarshaller marshaller = null;

      marshaller = new OpenPTKReflectiveXMLMarshaller(); // OpenPTK

      xml = req.toXML(marshaller);

      return this.send(xml);
   }

   //----------------------------------------------------------------
   public synchronized String send(final String xml) throws Spml2Exception
   //----------------------------------------------------------------
   {
      String response = null;

      if (_trace)
      {
         trace("SpmlClient: sending to " + _url.toString() + "\n" + xml);
      }

      try
      {
         response = this.sendAndReceive(_url, _action, xml);
      }
      catch (IOException ex)
      {
         throw new Spml2Exception(ex);
      }

      if (_trace)
      {
         trace("SpmlClient: received\n" + response);
      }

      return response;
   }

   //----------------------------------------------------------------
   public void throwErrors(Response res) throws Spml2ExceptionWithResponse
   //----------------------------------------------------------------
   {
      String[] msgs = null;
      if (res != null)
      {
         msgs = res.getErrorMessages();
         if (msgs != null && msgs.length != 0)
         {
            throw new Spml2ExceptionWithResponse(msgs[0], res);
         }
      }
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private void trace(final String msg)
   //----------------------------------------------------------------
   {
      Logger.getLogger(this.getClass().getSimpleName()).log(Level.INFO, msg);
      return;
   }

   //----------------------------------------------------------------
   private String sendAndReceive(final URL url, final String action, final String xml) throws IOException
   //----------------------------------------------------------------
   {

      byte[] xmlBytes = null;
      String soapXml = null;
      String host = null;
      String inputLine = null;
      String toReturn = null;
      StringBuffer result = null;
      URLConnection connection = url.openConnection();
      HttpURLConnection httpConn = (HttpURLConnection) connection;
      OutputStream out = null;
      InputStreamReader isr = null;
      BufferedReader in = null;

      if (_proxyHost != null)
      {
         host = System.getProperty("http.proxyHost");
         if (host == null)
         {
            System.setProperty("http.proxyHost", _proxyHost);
            System.setProperty("http.proxyPort", _proxyPort + "");
         }
         else
         {
            System.out.println("WARNING: Did not override system's http.proxyHost settings.");
         }
      }

      soapXml = buildXmlSOAP(xml);

      xmlBytes = soapXml.getBytes();

      // Set the appropriate HTTP parameters.

      httpConn.setRequestProperty(HTTP_CONTENT_LENGTH, String.valueOf(xmlBytes.length));
      httpConn.setRequestProperty(HTTP_CONTENT_TYPE, TYPE_XML_UTF8);

      if (action != null)
      {
         httpConn.setRequestProperty(HTTP_SOAP_ACTION, action);
      }

      httpConn.setRequestMethod(HTTP_POST);
      httpConn.setDoOutput(true);
      httpConn.setDoInput(true);

      if (_monitor != null)
      {
         _monitor.send(soapXml);
      }

      out = httpConn.getOutputStream();
      out.write(xmlBytes);
      out.close();

      isr = new InputStreamReader(httpConn.getInputStream());
      in = new BufferedReader(isr);

      result = new StringBuffer();
      while ((inputLine = in.readLine()) != null)
      {
         result.append(inputLine);
      }

      in.close();

      toReturn = result.length() == 0 ? null : result.toString();

      if (_monitor != null)
      {
         _monitor.receive(toReturn);
      }

      return toReturn;
   }

   //----------------------------------------------------------------
   private String buildXmlSOAP(final String xml)
   //----------------------------------------------------------------
   {
      StringBuilder buf = new StringBuilder();

      buf.append(XML_DECLARE);
      buf.append(SOAPENV_ENVELOPE_BEGIN); // <soapenv:Envelope ... />

      /*
       * SOAP Header
       */

      buf.append(SOAPENV_HEADER_BEGIN); // <soapenv:Header>

      buf.append(OPENPTK_USER_BEGIN); // <wsa1: ...

      buf.append(OPENPTK_USER_ID_BEGIN); // <wsa1: ...
      if (_username != null && _username.length() > 0)
      {
         buf.append(_username);
      }
      buf.append(OPENPTK_USER_ID_END); // </wsa1:...>

      buf.append(OPENPTK_USER_PASSWORD_BEGIN); // <wsa1: ...
      if (_password != null && _password.length() > 0)
      {
         buf.append(_password);
      }
      buf.append(OPENPTK_USER_PASSWORD_END); // </wsa1: ...>

      buf.append(OPENPTK_USER_END); // </wsa1:>

      buf.append(SOAPENV_HEADER_END); // </soapenv:Header>

      /*
       * SOAP Body
       */

      buf.append(SOAPENV_BODY_BEGIN); // <soapenv:Body>
      buf.append(OPENPTK_PROCESSREQUEST_BEGIN); // <m:processRequest ...
      buf.append(SOAPELEMENT_BEGIN); // <sOAPElement>

      /*
       * Add the SPMLv2  "payload"
       */

      buf.append(xml);

      buf.append(SOAPELEMENT_END); // </sOAPElement>
      buf.append(OPENPTK_PROCESSREQUEST_END);// </m:processRequest>
      buf.append(SOAPENV_BODY_END); // </soapenv:Body>

      buf.append(SOAPENV_ENVELOPE_END); // </soapenv:Envelope>

      return buf.toString();
   }

   //----------------------------------------------------------------
   private String getResponseContent(String message) throws Spml2Exception
   //----------------------------------------------------------------
   {
      Document doc = null;
      NodeList list = null;
      Node node = null;

      doc = XmlParser.parse(message);

      list = doc.getElementsByTagNameNS(OPENPTK_NAMESPACE, OPENPTK_PROCESSREQUESTRESPONSE);
      node = list.item(0);

      node = XmlUtil.getFirstChildElement(node);

      if (node == null)
      {
         throw new Spml2Exception("Response Context is null '" + OPENPTK_PROCESSREQUESTRESPONSE + "'");
      }

      return XmlUtil.serializeNode(node);
   }

}
