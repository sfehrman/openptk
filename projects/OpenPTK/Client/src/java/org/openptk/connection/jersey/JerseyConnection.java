/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2009-2010 Sun Microsystems, Inc.
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at
 * trunk/openptk/resource/legal-notices/OpenPTK.LICENSE
 * or https://openptk.dev.java.net/OpenPTK.LICENSE.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the reference to
 * trunk/openptk/resource/legal-notices/OpenPTK.LICENSE. If applicable,
 * add the following below this CDDL HEADER, with the fields enclosed
 * by brackets "[]" replaced with your own identifying information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 */

/*
 * Portions Copyright 2011-2012 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.connection.jersey;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.openptk.api.Input;
import org.openptk.api.Opcode;
import org.openptk.api.Output;
import org.openptk.api.State;
import org.openptk.connection.Connection;
import org.openptk.connection.ConnectionIF;
import org.openptk.connection.SetupIF;
import org.openptk.exception.AuthenticationException;
import org.openptk.exception.ConnectionException;
import org.openptk.exception.StructureException;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;
import org.openptk.structure.StructureType;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class JerseyConnection extends Connection
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String HEADER_LOCATION = "Location";
   private static final long SESSION_CACHE_TTL = 60000; // 60 seconds
   private ClientConfig _config = null;
   private Client _client = null;
   private String _mediaType = null;
   private List<NewCookie> _cookieList = new LinkedList<NewCookie>();
   private StructureIF _sessionStruct = null;
   private long _sessionTimeStamp = 0L;

   /**
    * Create a new ConnectionIF instance using the Jersey Client API.
    *
    * @throws Exception
    */
   //----------------------------------------------------------------
   public JerseyConnection(Properties props) throws ConnectionException, AuthenticationException
   //----------------------------------------------------------------
   {
      super(props);

      this.init();

      return;
   }

   /**
    * Close the open connection. The Session will be removed.
    *
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   public final void close() throws ConnectionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":close(): ";
      WebResource webRes = null;
      Builder builder = null;
      ClientResponse response = null;

      if (this.getState() != State.DESTROYED)
      {
         super.close();

         /*
          * Perform HTTP POST Operation for the "logout"
          */

         webRes = _client.resource(_uriLogout);
         builder = this.getBuilder(webRes);

         try
         {
            response = builder.post(ClientResponse.class);
         }
         catch (Exception ex)
         {
            /*
             * If there is a connect exception during the close, then
             * the server probably closed or there was a network issue.
             * Either way, the server connection is considered closed
             * from the client perspective, and we should continue.
             */

            this.logWarning(METHOD_NAME
                    + "An Exception occured while trying to logout from the Server: "
                    + ex.getMessage());
         }

         this.debug(METHOD_NAME
                 + (_sessionStruct != null ? _sessionStruct.toString() : "structure is null"));

         /*
          * Clear all the global variables
          */

         _cookieList = null;
         _uriClients = null;
         _uriContexts = null;
         _uriLogin = null;
         _uriLogout = null;
         _client = null;
         _config = null;
         _mediaType = null;
         _sessionStruct = null;
      }

      return;
   }

   /**
    * Get the String representing a Session data item.
    *
    * @return String Session data
    * @throws ConnectionException
    */
   //----------------------------------------------------------------
   @Override
   public final String getSessionData(ConnectionIF.Session session) throws ConnectionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getSessionData(): ";
      String childName = null;
      String value = null;
      String principalName = null;
      StructureIF structSession = null;
      StructureIF structChild = null;
      StructureIF structPrincipal = null;

      this.debug(METHOD_NAME + "name=" + session.toString());

      if (this.getState() == State.DESTROYED)
      {
         this.handleError(METHOD_NAME + "Connection has been closed");
      }

      structSession = this.getSessionStructure();

      if (structSession == null)
      {
         throw new ConnectionException(METHOD_NAME + "Session Structure is null");
      }

      switch (session)
      {
         case ID:
            childName = StructureIF.NAME_UNIQUEID;
            break;
         case TYPE:
            childName = StructureIF.NAME_TYPE;
            break;
         case PRINCIPAL:
            childName = StructureIF.NAME_PRINCIPAL;
            principalName = StructureIF.NAME_UNIQUEID;
            break;
         case AUTHEN:
            childName = StructureIF.NAME_PRINCIPAL;
            principalName = StructureIF.NAME_CONTEXTID;
            break;
         default:
            throw new ConnectionException(METHOD_NAME + "Invalid Session Data enum.");
      }

      if (structSession.hasChild(childName))
      {
         structChild = structSession.getChild(childName);
         if (structChild == null)
         {
            throw new ConnectionException(METHOD_NAME + "Child Structure is null");
         }
         if (session == Connection.Session.PRINCIPAL
                 || session == Connection.Session.AUTHEN)
         {
            if (structChild.hasChild(principalName))
            {
               structPrincipal = structChild.getChild(principalName);
               if (structPrincipal != null)
               {
                  value = structPrincipal.getValueAsString();
               }
            }
         }
         else
         {
            value = structChild.getValueAsString();
         }
      }
      else
      {
         throw new ConnectionException(METHOD_NAME + "Structure has no child '"
                 + childName + "'.");
      }

      this.debug(METHOD_NAME + "value='" + (value != null ? value : "(null)") + "'");

      return value;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   /**
    * Execute CREATE operation.
    *
    * @param input
    * @return Output
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   protected Output doCreate(final Input input) throws ConnectionException, StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doCreate(): ";
      String strIn = null;
      StringBuilder uri = null;
      Output output = null;
      StructureIF structIn = null;
      WebResource webRes = null;
      ClientResponse response = null;
      Builder builder = null;

      output = new Output();

      structIn = this.getStructureFromInput(Opcode.CREATE, input);

      strIn = this.encode(structIn);

      this.debug(METHOD_NAME + "input: " + structIn.toString());

      uri = new StringBuilder();
      uri = uri.append(_uriSubjects);

      this.debug(METHOD_NAME + "HTTP POST: url=" + uri.toString());

      webRes = _client.resource(uri.toString());
      builder = this.getBuilder(webRes);
      response = builder.post(ClientResponse.class, strIn);

      this.debug(METHOD_NAME + "HTTP Response: " + response.getStatus());

      this.postProcessResponse(response);

      this.updateOutputFromResponse(output, response);

      this.debug(METHOD_NAME + "state=" + output.getState().toString()
              + ", status='" + output.getStatus() + "'");


      return output;
   }

   /**
    * Execute READ operation.
    *
    * @param input
    * @return Output
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   protected Output doRead(final Input input) throws ConnectionException, StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doRead(): ";
      String strOut = null;
      String uid = null;
      StringBuilder uri = null;
      Output output = null;
      StructureIF structIn = null;
      StructureIF structOut = null;
      WebResource webRes = null;
      ClientResponse response = null;
      Builder builder = null;

      output = new Output();

      structIn = this.getStructureFromInput(Opcode.READ, input);

      uid = this.getUniqueId(structIn);
      if (uid == null || uid.length() < 1)
      {
         this.handleError("uid is empty/null");
      }

      this.debug(METHOD_NAME + "input: " + structIn.toString());

      uri = new StringBuilder();
      uri = uri.append(_uriSubjects);
      uri.append("/");
      uri.append(this.urlEncode(uid));

      this.debug(METHOD_NAME + "HTTP GET: url=" + uri.toString());

      webRes = _client.resource(uri.toString());
      builder = this.getBuilder(webRes);
      response = builder.get(ClientResponse.class);

      this.debug(METHOD_NAME + "HTTP Response: " + response.getStatus());

      this.postProcessResponse(response);

      this.updateOutputFromResponse(output, response);

      this.debug(METHOD_NAME + "state="
              + output.getState().toString() + ", status='" + output.getStatus() + "'");

      if (output.getState() == State.SUCCESS)
      {
         strOut = response.getEntity(String.class);
         structOut = this.decode(strOut);

         this.debug(METHOD_NAME + "output: " + structOut.toString());

         this.updateOutputFromReadStructure(output, structOut);
      }



      return output;
   }

   /**
    * Execute UPDATE operation.
    *
    * @param input
    * @return Output
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   protected Output doUpdate(final Input input) throws ConnectionException, StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doUpdate(): ";
      String strIn = null;
      String uid = null;
      StringBuilder uri = null;
      Output output = null;
      StructureIF structIn = null;
      WebResource webRes = null;
      ClientResponse response = null;
      Builder builder = null;

      output = new Output();

      structIn = this.getStructureFromInput(Opcode.UPDATE, input);

      uid = this.getUniqueId(structIn);
      if (uid == null || uid.length() < 1)
      {
         this.handleError("uid is empty/null");
      }

      strIn = this.encode(structIn);

      this.debug(METHOD_NAME + "input: " + structIn.toString());

      uri = new StringBuilder();
      uri = uri.append(_uriSubjects);

      uri.append("/");
      uri.append(this.urlEncode(uid));

      this.debug(METHOD_NAME + "HTTP PUT: url=" + uri.toString());

      webRes = _client.resource(uri.toString());
      builder = this.getBuilder(webRes);
      response = builder.put(ClientResponse.class, strIn);

      this.debug(METHOD_NAME + "HTTP Response: " + response.getStatus());

      this.postProcessResponse(response);

      this.updateOutputFromResponse(output, response);

      this.debug(METHOD_NAME + "state="
              + output.getState().toString() + ", status='" + output.getStatus() + "'");

      return output;
   }

   /**
    * Execute DELETE operation.
    *
    * @param input
    * @return Output
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   protected Output doDelete(final Input input) throws ConnectionException, StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doDelete(): ";
      String uid = null;
      StringBuilder uri = null;
      Output output = null;
      StructureIF structIn = null;
      WebResource webRes = null;
      ClientResponse response = null;
      Builder builder = null;

      output = new Output();

      structIn = this.getStructureFromInput(Opcode.DELETE, input);

      uid = this.getUniqueId(structIn);
      if (uid == null || uid.length() < 1)
      {
         this.handleError("uid is empty/null");
      }

      this.debug(METHOD_NAME + "input: " + structIn.toString());

      uri = new StringBuilder();
      uri = uri.append(_uriSubjects);

      uri.append("/");
      uri.append(this.urlEncode(uid));

      this.debug(METHOD_NAME + "HTTP DELETE: url=" + uri.toString());

      webRes = _client.resource(uri.toString());
      builder = this.getBuilder(webRes);
      response = builder.delete(ClientResponse.class);

      this.debug(METHOD_NAME + "HTTP Response: " + response.getStatus());

      this.postProcessResponse(response);

      this.updateOutputFromResponse(output, response);

      this.debug(METHOD_NAME + "state="
              + output.getState().toString() + ", status='" + output.getStatus() + "'");

      return output;
   }

   /**
    * Execute SEARCH operation.
    *
    * @param input
    * @return Output
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   protected Output doSearch(final Input input) throws ConnectionException, StructureException
   //----------------------------------------------------------------
   {
      Object[] values = null;
      String METHOD_NAME = CLASS_NAME + ":doSearch(): ";
      String qty = null;
      String search = null;
      StringBuilder uri = null;
      String strOut = null;
      Output output = null;
      StructureIF structIn = null;
      StructureIF structOut = null;
      StructureIF struct = null;
      WebResource webRes = null;
      ClientResponse response = null;
      Builder builder = null;

      output = new Output();

      structIn = this.getStructureFromInput(Opcode.SEARCH, input);

      this.debug(METHOD_NAME + "input: " + structIn.toString());

      uri = new StringBuilder();
      uri = uri.append(_uriSubjects);

      qty = input.getProperty(StructureIF.NAME_QUANTITY);
      if (qty == null || qty.length() < 1)
      {
         qty = "1000";
      }
      uri.append("?").append(StructureIF.NAME_QUANTITY).append("=").append(qty);

      if (structIn.hasChild(StructureIF.NAME_SEARCH))
      {
         struct = structIn.getChild(StructureIF.NAME_SEARCH);
         values = struct.getValuesAsArray();
         if (values != null && values.length > 0)
         {
            if (struct.getValueType() == StructureType.STRING)
            {
               search = (String) values[0];
               if (search != null)
               {
                  search = urlEncode(search.trim());
                  if (search.length() > 0)
                  {
                     uri.append("&").append(StructureIF.NAME_SEARCH).append("=").append(search);
                  }
               }
            }
         }
      }

      this.debug(METHOD_NAME + "HTTP GET: url=" + uri.toString());

      webRes = _client.resource(uri.toString());
      builder = this.getBuilder(webRes);
      response = builder.get(ClientResponse.class);

      this.debug(METHOD_NAME + "HTTP Response: " + response.getStatus());

      this.postProcessResponse(response);

      this.updateOutputFromResponse(output, response);

      this.debug(METHOD_NAME + "state=" + output.getState().toString()
              + ", status='" + output.getStatus() + "'");

      if (response.getClientResponseStatus() == Status.OK)
      {
         strOut = response.getEntity(String.class);
         structOut = this.decode(strOut);

         this.debug(METHOD_NAME + "output: " + structOut.toString());

         this.updateOutputFromSearchStructure(output, structOut);
      }

      return output;
   }

   /**
    * Execute PWDCHANGE operation.
    *
    * @param input
    * @return Output
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   protected Output doPwdChange(final Input input) throws ConnectionException, StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doPwdChange(): ";
      String uid = null;
      String strIn = null;
      StringBuilder uri = null;
      Output output = null;
      StructureIF structIn = null;
      WebResource webRes = null;
      ClientResponse response = null;
      Builder builder = null;

      output = new Output();

      structIn = this.getStructureFromInput(Opcode.PWDCHANGE, input);

      uid = this.getUniqueId(structIn);
      if (uid == null || uid.length() < 1)
      {
         this.handleError("uid is empty/null");
      }

      strIn = this.encode(structIn);

      this.debug(METHOD_NAME + "input: " + structIn.toString());

      uri = new StringBuilder();
      uri = uri.append(_uriSubjects);

      uri.append("/");
      uri.append(this.urlEncode(uid)).append("/");
      uri.append("password/change");

      this.debug(METHOD_NAME + "HTTP PUT: url=" + uri.toString());

      webRes = _client.resource(uri.toString());
      builder = this.getBuilder(webRes);
      response = builder.put(ClientResponse.class, strIn);

      this.debug(METHOD_NAME + "HTTP Response: " + response.getStatus());

      this.postProcessResponse(response);

      this.updateOutputFromResponse(output, response);

      this.debug(METHOD_NAME + "state=" + output.getState().toString()
              + ", status='" + output.getStatus() + "'");

      return output;
   }

   /**
    * Execute PWDRESET operation.
    *
    * @param input
    * @return Output
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   protected Output doPwdReset(final Input input) throws ConnectionException, StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doPwdReset(): ";
      String uid = null;
      String strOut = null;
      StringBuilder uri = null;
      Output output = null;
      StructureIF structIn = null;
      StructureIF structOut = null;
      WebResource webRes = null;
      ClientResponse response = null;
      Builder builder = null;

      output = new Output();

      structIn = this.getStructureFromInput(Opcode.PWDRESET, input);

      uid = this.getUniqueId(structIn);
      if (uid == null || uid.length() < 1)
      {
         this.handleError("uid is empty/null");
      }

      this.debug(METHOD_NAME + "input: " + structIn.toString());

      uri = new StringBuilder();
      uri = uri.append(_uriSubjects);
      uri.append("/");
      uri.append(this.urlEncode(uid)).append("/");
      uri.append("password/reset");

      this.debug(METHOD_NAME + "HTTP GET: url=" + uri.toString());

      webRes = _client.resource(uri.toString());
      builder = this.getBuilder(webRes);
      response = builder.get(ClientResponse.class);

      this.debug(METHOD_NAME + "HTTP Response: " + response.getStatus());

      this.postProcessResponse(response);

      this.updateOutputFromResponse(output, response);

      if (output.getState() == State.SUCCESS)
      {
         strOut = response.getEntity(String.class);
         structOut = decode(strOut);

         this.updateOutputFromReadStructure(output, structOut);
      }

      this.debug(METHOD_NAME + "state=" + output.getState().toString()
              + ", status='" + output.getStatus() + "'");

      return output;
   }

   /**
    * Execute PWDFORGOT operation.
    *
    * @param input
    * @return Output
    * @throws Exception
    */
   //----------------------------------------------------------------
   @Override
   protected Output doPwdForgot(final Input input) throws ConnectionException, StructureException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doPwdForgot(): ";
      String uid = null;
      String mode = null;
      String strIn = null;
      String strOut = null;
      StringBuilder uri = null;
      Output output = null;
      StructureIF structIn = null;
      StructureIF structOut = null;
      WebResource webRes = null;
      Builder builder = null;
      ClientResponse response = null;

      output = new Output();

      structIn = this.getStructureFromInput(Opcode.PWDFORGOT, input);

      uid = this.getUniqueId(structIn);
      if (uid == null || uid.length() < 1)
      {
         this.handleError("uid is empty/null");
      }

      mode = input.getProperty(StructureIF.NAME_MODE);

      if (mode == null || mode.length() < 1)
      {
         this.handleError(METHOD_NAME + "Input Property '"
                 + StructureIF.NAME_MODE + "' is missing or empty");
      }

      this.debug(METHOD_NAME + "mode='" + mode + "', input: " + structIn.toString());

      uri = new StringBuilder();
      uri = uri.append(_uriSubjects);
      uri.append("/");
      uri.append(this.urlEncode(uid)).append("/");
      uri.append("password/forgot/").append(mode);

      webRes = _client.resource(uri.toString());
      builder = this.getBuilder(webRes);

      if (mode.equalsIgnoreCase(StructureIF.NAME_QUESTIONS))
      {

         // Phase 1: get the questions

         this.debug(METHOD_NAME + "HTTP GET: url=" + uri.toString());

         response = builder.get(ClientResponse.class);

         this.debug(METHOD_NAME + "HTTP Response: " + response.getStatus());

         this.updateOutputFromResponse(output, response);

         this.debug(METHOD_NAME + "state=" + output.getState().toString()
                 + ", status='" + output.getStatus() + "'");

         if (output.getState() == State.SUCCESS)
         {
            strOut = response.getEntity(String.class);
            structOut = decode(strOut);

            this.updateOutputFromReadStructure(output, structOut);
         }
      }
      else if (mode.equalsIgnoreCase(StructureIF.NAME_ANSWERS))
      {
         // Phase 2: put the questions and answers

         strIn = this.encode(structIn);

         this.debug(METHOD_NAME + "HTTP PUT: url=" + uri.toString());

         response = builder.put(ClientResponse.class, strIn);

         this.debug(METHOD_NAME + "HTTP Response: " + response.getStatus());

         this.updateOutputFromResponse(output, response);

         this.debug(METHOD_NAME + "state=" + output.getState().toString()
                 + ", status='" + output.getStatus() + "'");
      }
      else if (mode.equalsIgnoreCase(StructureIF.NAME_CHANGE))
      {
         // Phase 3: Change the password

         strIn = this.encode(structIn);

         this.debug(METHOD_NAME + "HTTP PUT: url=" + uri.toString());

         response = builder.put(ClientResponse.class, strIn);

         this.debug(METHOD_NAME + "HTTP Response: " + response.getStatus());

         this.updateOutputFromResponse(output, response);

         this.debug(METHOD_NAME + "state=" + output.getState().toString()
                 + ", status='" + output.getStatus() + "'");
      }
      else
      {
         this.handleError(METHOD_NAME + "Invalid mode '" + mode + "'");
      }

      this.postProcessResponse(response);

      this.debug(METHOD_NAME + "output: "
              + output.getState().toString() + ", " + output.getStatus());

      return output;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private void init() throws ConnectionException, AuthenticationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":init(): ";
      String strClient = null;
      String clientId = null;
      String clientCred = null;
      String url = null;
      String msg = null;
      String mode = null;
      String token = null;
      String user = null;
      String password = null;
      String sessionId = null;
      String strResponse = null;
      StructureIF structClient = null;                // OpenPTK
      StructureIF structSession = null;               // OpenPTK
      WebResource webRes = null;                      // Jersey Client
      ClientResponse response = null;                 // Jersey Client
      Status status = null;                           // Jersey Client
      Builder builder = null;                         // Jersey Client
      NewCookie cookie = null;
      MultivaluedMap<String, String> formData = null; // JAX-RS

      /*
       * Check the properties and the values
       */

      mode = this.getProperty(SetupIF.PROP_CONNECTION_MODE);
      if (mode == null || mode.length() < 1)
      {
         this.handleError(METHOD_NAME + "Property '" + SetupIF.PROP_CONNECTION_MODE
                 + "' is null/empty");
      }

      if (mode.equals(SetupIF.CONNECTION_MODE_USERPASS))
      {
         user = this.getProperty(SetupIF.PROP_CONNECTION_USER);
         password = this.getProperty(SetupIF.PROP_CONNECTION_PASSWORD);
      }
      else if (mode.equals(SetupIF.CONNECTION_MODE_TOKEN))
      {
         token = this.getProperty(SetupIF.PROP_CONNECTION_TOKEN);
      }
      else if (mode.equals(SetupIF.CONNECTION_MODE_SESSIONID))
      {
         sessionId = this.getProperty(SetupIF.PROP_CONNECTION_SESSIONID);
         if (sessionId == null || sessionId.length() < 1)
         {
            this.handleError(METHOD_NAME + "SessionId is empty/null");
         }
      }
      else
      {
         this.handleError(METHOD_NAME + "Mode '" + mode + "' is not valid");
      }

      /*
       * Set the Media Type
       */

      switch (_converter.getType())
      {
         case XML:
            _mediaType = MediaType.APPLICATION_XML;
            break;
         case JSON:
            _mediaType = MediaType.APPLICATION_JSON;
            break;
      }

      _config = new DefaultClientConfig();
      _client = Client.create(_config);

      clientId = this.getProperty(SetupIF.PROP_CONNECTION_CLIENTID);

      /*
       * Create connection with either user/pass or sessionid
       */

      if ((mode.equals(SetupIF.CONNECTION_MODE_USERPASS)) || (mode.equals(SetupIF.CONNECTION_MODE_TOKEN)))
      {
         clientCred = this.encrypt(SetupIF.PROP_CONNECTION_CLIENTID + "=" + clientId);

         /*
          * Build the request parameters
          */

         formData = new MultivaluedMapImpl();
         formData.add(PARAM_CLIENTID, clientId);
         formData.add(PARAM_CLIENTCRED, clientCred);

         if (token != null && token.length() > 0)
         {
            formData.add(PARAM_TOKEN, this.encrypt(token));
         }

         if (user != null && user.length() > 0)
         {
            formData.add(PARAM_USER, this.encrypt(user));
            if (password != null && password.length() > 0)
            {
               formData.add(PARAM_PASSWORD, this.encrypt(password));
            }
         }

         /*
          * Perform HTTP POST Operation for the "login", get and save Cookies
          */

         url = _uriLogin;
         webRes = _client.resource(url);
         builder = this.getBuilder(webRes, "application/x-www-form-urlencoded");

         this.debug(METHOD_NAME + "HTTP POST: url=" + url);

         response = builder.post(ClientResponse.class, formData);

         this.debug(METHOD_NAME + "HTTP Response: " + response.getStatus());

         status = response.getClientResponseStatus();

         strResponse = response.getEntity(String.class);

         if (status != Status.OK)
         {
            msg = METHOD_NAME
                    + "Login Failed: "
                    + "[" + status.getStatusCode() + "] " + status.toString() + ", " + url;

            this.setState(State.NOTAUTHENTICATED);
            this.setStatus(msg);
            throw new AuthenticationException(msg);
         }

         this.postProcessResponse(response);

      }
      else // mode = sessionid
      {
         this.setSessionId(sessionId);

         /*
          * Create a cookie using the sessionId
          */

         cookie = new NewCookie(SetupIF.SESSION_ID, sessionId);
         _cookieList.add(cookie);

         this.debug(METHOD_NAME + "reusing connection: mode='" + mode + ", sessionId='" + sessionId
                 + "', new_cookie='" + cookie.toString()
                 + "', cookie_list=" + _cookieList.toString() + "'");

         structSession = this.getSessionStructure();
      }

      /*
       * Perform HTTP GET Operation to obtain Client Information
       */

      url = _uriClients + "/" + clientId;

      webRes = _client.resource(url);
      builder = this.getBuilder(webRes);

      this.debug(METHOD_NAME + "HTTP GET url=" + url);

      response = builder.get(ClientResponse.class);

      this.debug(METHOD_NAME + "HTTP Response: " + response.getStatus());

      status = response.getClientResponseStatus();

      if (status != Status.OK)
      {
         msg = METHOD_NAME
                 + "Failed to get client information: "
                 + "[" + status.getStatusCode() + "] " + status.toString() + ", " + url;

         this.setState(State.ERROR);
         this.setStatus(msg);
         this.handleError(msg);
      }

      this.postProcessResponse(response);

      strClient = response.getEntity(String.class);
      if (strClient == null || strClient.length() < 1)
      {
         msg = METHOD_NAME
                 + "Client information is null: "
                 + "[" + status.getStatusCode() + "] " + status.toString() + ", " + url;

         this.setState(State.ERROR);
         this.setStatus(msg);
         this.handleError(msg);
      }

      structClient = this.decode(strClient);

      this.debug(METHOD_NAME + "client info:" + structClient.toString());

      this.processClientStructure(structClient);

      this.setState(State.READY);

      this.debug(METHOD_NAME + "Connection created");

      return;
   }

   //----------------------------------------------------------------
   private StructureIF getSessionStructure() throws ConnectionException
   //----------------------------------------------------------------
   {
      boolean valid = false;
      long msec = 0L;
      String METHOD_NAME = CLASS_NAME + ":getSessionStructure(): ";
      String strResponse = null;
      String url = null;
      WebResource webRes = null;
      Builder builder = null;
      ClientResponse response = null;
      Status status = null;
      StructureIF structResponse = null;
      StructureIF structSession = null;

      if (this.getState() == State.DESTROYED)
      {
         this.handleError(METHOD_NAME + "Connection has been closed");
      }

      msec = System.currentTimeMillis() - _sessionTimeStamp;

      if (msec < SESSION_CACHE_TTL)
      {
         valid = true;
      }

      this.debug(METHOD_NAME + "valid_cache=" + valid + ", msec=" + msec
              + ", " + (_sessionStruct != null ? _sessionStruct.toString() : "(null)"));

      if (_sessionStruct != null && valid)
      {
         structSession = _sessionStruct;
      }
      else
      {
         structSession = new BasicStructure(StructureIF.NAME_SESSION);

         url = _uriSessionInfo;

         this.debug(METHOD_NAME + "HTTP GET url=" + url);

         webRes = _client.resource(url);
         builder = this.getBuilder(webRes);
         response = builder.get(ClientResponse.class);

         status = response.getClientResponseStatus();

         if (status != Status.OK)
         {
            this.setState(State.ERROR);
            this.handleError(METHOD_NAME
                    + "Failed to get session data: "
                    + "[" + status.getStatusCode() + "] " + status.toString() + ", " + url);
         }

         this.postProcessResponse(response);

         strResponse = response.getEntity(String.class);

         structResponse = this.decode(strResponse);

         this.debug(METHOD_NAME + "Response=" + status.toString()
                 + ": " + structResponse.toString());
         /*
          * Copy Structure data from the Response to the Session
          *
          * Response Format:
          *    response = {
          *       uri = "http://...";
          *       length = "1";
          *       sessions = {
          *          session = {
          *             uniqueid = "...";
          *             type = "...";
          *             principal={
          *                uniqueid="...";
          *                contextid="..."
          *             }
          *          }
          *       }
          *    }
          */

         try
         {
            if (structResponse.hasChild(StructureIF.NAME_SESSIONS))
            {
               structResponse = structResponse.getChild(StructureIF.NAME_SESSIONS); // get "sessions"
            }

            if (structResponse.hasChild(StructureIF.NAME_SESSION))
            {
               structResponse = structResponse.getChild(StructureIF.NAME_SESSION); // get "session"
            }

            if (structResponse.hasChild(StructureIF.NAME_UNIQUEID))
            {
               structSession.addChild(structResponse.getChild(StructureIF.NAME_UNIQUEID)); // get "uniqueid"
            }
            else
            {
               structSession.addChild(new BasicStructure(StructureIF.NAME_UNIQUEID));
            }

            if (structResponse.hasChild(StructureIF.NAME_TYPE))
            {
               structSession.addChild(structResponse.getChild(StructureIF.NAME_TYPE)); // get "type"
            }
            else
            {
               structSession.addChild(new BasicStructure(StructureIF.NAME_TYPE));
            }

            if (structResponse.hasChild(StructureIF.NAME_PRINCIPAL))
            {
               structSession.addChild(structResponse.getChild(StructureIF.NAME_PRINCIPAL));
            }
            else
            {
               structSession.addChild(new BasicStructure(StructureIF.NAME_PRINCIPAL));
            }
         }
         catch (StructureException ex)
         {
            this.handleError(METHOD_NAME + ex.getMessage());
         }
         _sessionStruct = structSession;
         _sessionTimeStamp = System.currentTimeMillis();

         this.debug(METHOD_NAME + "(server refresh) " + structSession.toString());
      }

      return structSession;
   }

   //----------------------------------------------------------------
   private String getUniqueId(final StructureIF structIn) throws ConnectionException
   //----------------------------------------------------------------
   {
      Object[] values = null;
      String METHOD_NAME = CLASS_NAME + ":getUniqueId(): ";
      String uniqueId = null;
      StructureIF struct = null;
      StructureType type = null;

      if (structIn.hasChild(StructureIF.NAME_UNIQUEID))
      {
         struct = structIn.getChild(StructureIF.NAME_UNIQUEID);
         values = struct.getValuesAsArray();
         if (values != null && values.length > 0)
         {
            type = struct.getValueType();

            switch (type)
            {
               case STRING:
                  uniqueId = (String) values[0];
                  break;
               case INTEGER:
                  uniqueId = ((Integer) values[0]).toString();
                  break;
               case LONG:
                  uniqueId = ((Long) values[0]).toString();
                  break;
               default:
                  this.handleError(METHOD_NAME + "Invalid type '" + type.toString() + "' for uniqueid");
                  break;
            }
         }
         else
         {
            this.handleError(METHOD_NAME + "Structure for uniqueId has no value");
         }
      }
      else
      {
         this.handleError(METHOD_NAME + "Structure for uniqueId is null");
      }

      return uniqueId;
   }

   //----------------------------------------------------------------
   private void updateOutputFromResponse(final Output output, final ClientResponse response) throws ConnectionException
   //----------------------------------------------------------------
   {
      String entity = null;
      String METHOD_NAME = CLASS_NAME + ":updateOutputFromResponse(): ";
      MultivaluedMap<String, String> headers = null;
      String location = null;

      if (output == null)
      {
         this.handleError(METHOD_NAME + "Output is null");
      }

      if (response == null)
      {
         this.handleError(METHOD_NAME + "ClientResponse is null");
      }

      output.setStatus(response.getClientResponseStatus().toString());

      switch (response.getClientResponseStatus())
      {
         case OK: // 200: OK
            output.setState(State.SUCCESS);
            break;
         case CREATED: // 201: CREATED
            output.setState(State.SUCCESS);
            headers = response.getHeaders();
            if (headers != null && headers.containsKey(HEADER_LOCATION))
            {
               location = urlDecode(headers.getFirst(HEADER_LOCATION));

               output.setStatus(response.getClientResponseStatus().toString() + ": "
                       + location);

               output.setUniqueId(location.substring(location.lastIndexOf("/") + 1));
            }
            break;
         case NO_CONTENT: // 204: NO_CONTENT
            output.setState(State.SUCCESS);
            break;
         case BAD_REQUEST: // 400: BAD_REQUEST
            entity = response.getEntity(String.class);
            if (entity != null && entity.length() > 0)
            {
               output.setStatus(entity);
            }
            output.setState(State.INVALID);
            output.setError(true);
            break;
         case UNAUTHORIZED: // 401: UNAUTHORIZED
            /*
             * If a 401 is returned, and we are processing the response,
             * then the user has encountered a DENIED condition where a
             * Decider has DENIED access to the operation.
             *
             * In the case of an Authenticaion, an UNAUTHORIZED may also
             * occur, but in that case, it is handled in the init() method
             * of this class.
             */

            output.setState(State.DENIED);
            output.setError(true);
            break;
         case FORBIDDEN: // 403: FORBIDDEN
            entity = response.getEntity(String.class);
            if (entity != null && entity.length() > 0)
            {
               output.setStatus(entity);
            }
            output.setState(State.FAILED);
            output.setError(true);
            break;
         case NOT_FOUND: // 404: NOT_FOUND
            output.setState(State.NOTEXIST);
            output.setError(true);
            break;
         case INTERNAL_SERVER_ERROR: // 500: INTERNAL_SERVER_ERROR
            output.setState(State.ERROR);
            output.setError(true);
            break;
         case SERVICE_UNAVAILABLE: // 503: SERVICE_UNAVAILABLE
            output.setState(State.ERROR);
            output.setError(true);
            break;
         default:
            output.setState(State.ERROR);
            output.setError(true);
            this.handleError(METHOD_NAME + "Invalid Response Code: "
                    + response.getStatus() + ", " + response.getClientResponseStatus().toString());
            break;
      }

      return;
   }

   //----------------------------------------------------------------
   private WebResource.Builder getBuilder(final WebResource webRes)
   //----------------------------------------------------------------
   {
      return this.getBuilder(webRes, null);
   }

   //----------------------------------------------------------------
   private WebResource.Builder getBuilder(final WebResource webRes, final String type)
   //----------------------------------------------------------------
   {
      WebResource.Builder builder = null;
      NewCookie cookie = null;
      Iterator<NewCookie> iter = null;

      /*
       * Create a "builder"
       * set the media type
       * set the "type"
       * add all the "cookies"
       */

      builder = webRes.getRequestBuilder();
      builder = builder.accept(_mediaType);

      if (type != null && type.length() > 0)
      {
         builder = builder.type(type);
      }
      else
      {
         builder = builder.type(_mediaType);
      }

      if (_cookieList != null && !_cookieList.isEmpty())
      {
         iter = _cookieList.iterator();
         while (iter.hasNext())
         {
            cookie = iter.next();
            if (cookie != null)
            {
               builder = builder.cookie(cookie);
            }
         }
      }

      return builder;
   }

   //----------------------------------------------------------------
   private synchronized void postProcessResponse(ClientResponse response)
   //----------------------------------------------------------------
   {
      int iCnt = 0;
      String METHOD_NAME = CLASS_NAME + ":postProcessResponse(): ";
      String name = null;
      String value = null;
      String currSession = null;
      List<NewCookie> cookieList = null;

      currSession = this.getSessionId();

      /*
       * Save the Cookies
       */

      cookieList = response.getCookies();

      /*
       * The "sessionid" is stored in a Cookie "OPENPTKSESSIONID"
       * If the Cookie exists, and has value,
       * set the session id
       */

      if (cookieList != null && !cookieList.isEmpty())
      {
         for (NewCookie cookie : cookieList)
         {
            if (cookie != null)
            {
               iCnt++;
               _cookieList.add(cookie);

               name = cookie.getName();
               if (name != null && name.equals(SetupIF.SESSION_ID))
               {
                  value = cookie.getValue();
                  if (value != null && value.length() > 0)
                  {
                     if (currSession == null)
                     {
                        this.setSessionId(value);
                     }
                     else
                     {
                        if (!currSession.equals(value))
                        {
                           this.setSessionChanged(true);
                           this.setSessionId(value);
                        }
                     }
                  }
               }
               this.debug(METHOD_NAME + "Cookie " + iCnt + ": "
                       + (name != null ? name : "(null)") + "='"
                       + (value != null ? value : "(null)") + "'");
            }
         }
      }

      return;
   }
}
