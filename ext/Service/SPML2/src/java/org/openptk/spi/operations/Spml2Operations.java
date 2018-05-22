/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2009-2011 Project OpenPTK
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
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.spi.operations;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.openspml.v2.msg.OpenContentElement;
import org.openspml.v2.msg.pass.ResetPasswordRequest;
import org.openspml.v2.msg.pass.ResetPasswordResponse;
import org.openspml.v2.msg.pass.SetPasswordRequest;
import org.openspml.v2.msg.pass.SetPasswordResponse;
import org.openspml.v2.msg.spml.AddRequest;
import org.openspml.v2.msg.spml.AddResponse;
import org.openspml.v2.msg.spml.DeleteRequest;
import org.openspml.v2.msg.spml.DeleteResponse;
import org.openspml.v2.msg.spml.ExecutionMode;
import org.openspml.v2.msg.spml.Extensible;
import org.openspml.v2.msg.spml.LookupRequest;
import org.openspml.v2.msg.spml.LookupResponse;
import org.openspml.v2.msg.spml.Modification;
import org.openspml.v2.msg.spml.ModificationMode;
import org.openspml.v2.msg.spml.ModifyRequest;
import org.openspml.v2.msg.spml.ModifyResponse;
import org.openspml.v2.msg.spml.PSO;
import org.openspml.v2.msg.spml.PSOIdentifier;
import org.openspml.v2.msg.spml.ReturnData;
import org.openspml.v2.msg.spml.Selection;
import org.openspml.v2.msg.spml.StatusCode;
import org.openspml.v2.msg.spmlsearch.SearchRequest;
import org.openspml.v2.msg.spmlsearch.SearchResponse;
import org.openspml.v2.profiles.dsml.DSMLAttr;
import org.openspml.v2.profiles.dsml.DSMLModification;
import org.openspml.v2.profiles.dsml.DSMLValue;
import org.openspml.v2.profiles.dsml.Filter;
import org.openspml.v2.util.xml.ReflectiveXMLMarshaller;

import org.openptk.api.State;
import org.openptk.common.AttrIF;
import org.openptk.common.BasicAttr;
import org.openptk.common.Category;
import org.openptk.common.Component;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.debug.DebugLevel;
import org.openptk.exception.OperationException;
import org.openptk.exception.QueryException;
import org.openptk.logging.Logger;
import org.openptk.spi.QueryConverterIF;
import org.openptk.spi.Spml2QueryConverter;

/**
 *
 * @author Derrick Harcey
 * @author Scott Fehrman
 */
/*
 * Meaning of Response State:
 *
 * ERROR   : An error with the SPML2 infrastructure / configuration / connection
 *           An Exception will be thrown
 *
 * SUCCESS : The operation was successful
 *           Response is returned, error = false (default)
 *
 * INVALID : There is something wrong / missing from the input
 *           Response is returned, error = true
 *
 * FAILED  : The operation failed due to some business logic problem
 *           Response is returned, error = true
 *
 * NOTEXIST : The "entry" being referenced was not found
 *           Response is returned, error = true
 *
 */
//===================================================================
public class Spml2Operations extends Operations
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String DESCRIPTION = "Service Provision Markup Language (SPML) 2.0";
   protected static final String PROP_CLIENTFACADE_CLASSNAME = "clientfacade.classname";
   protected static final String PROP_SPML2_TRACE = "spmlTrace";
   protected static final String PROP_SPML2_OBJECTCLASS = "objectclass";
   protected static final String PROP_XML_REQUEST = "spml.xml.request";
   protected static final String PROP_XML_RESPONSE = "spml.xml.response";
   protected static final String EXTREQ_OPER_CHANGEPWD = "extreq.oper.changepwd";
   protected static final String EXTREQ_OPER_RESETPWD = "extreq.oper.resetpwd";
   protected static final String EXTREQ_ATTR_UID_NAME = "extreq.attr.uid.name";
   protected static final String EXTREQ_ATTR_PWD_NAME = "extreq.attr.pwd.name";
   protected static final String EXTREQ_ATTR_PWD_SUBJECT_ATTR = "extreq.attr.pwd.subject.attr";
   protected static final String EXTREQ_ATTR_RESOURCES_KEY = "extreq.attr.resources.key";
   protected static final String EXTREQ_ATTR_RESOURCES_VALUE = "extreq.attr.resources.value";
   protected ClientFacadeIF _facade = null;

   //----------------------------------------------------------------
   public Spml2Operations()
   //----------------------------------------------------------------
   {
      super();
      this.init();
      return;
   }

   //----------------------------------------------------------------
   @Override
   public void startup()
   //----------------------------------------------------------------
   {
      int iPort = 8080;
      String METHOD_NAME = CLASS_NAME + ":startup(): ";
      String username = null;
      String password = null;
      String url = null;
      String host = null;
      String port = null;
      String clientClass = null;
      Object obj = null;

      super.startup();

      /*
       * get "url", must exist and be non-null
       */

      url = this.getProperty(OperationsIF.PROP_URL);
      if (url == null || url.length() < 1)
      {
         this.setError(true);
         this.setState(State.ERROR);
         this.setStatus(METHOD_NAME
            + "Property '" + OperationsIF.PROP_URL
            + "' has a null value or is not set.");
      }
      else
      {
         /*
          * check for a username and password (optional)
          */

         username = this.getValue(OperationsIF.PROP_USER_NAME);
         password = this.getValue(OperationsIF.PROP_USER_PASSWORD);

         /*
          * check the proxy hostname and port (optional)
          */

         host = this.getProperty(OperationsIF.PROP_PROXY_HOST);
         port = this.getProperty(OperationsIF.PROP_PROXY_PORT);

         try
         {
            iPort = Integer.parseInt(port);
         }
         catch (NumberFormatException ex)
         {
            iPort = 8080;
         }

         /*
          * create the "wrapper" class for ClientFacadeIF interface
          * get the property, instanciate it by-name, initialize it
          */

         clientClass = this.getProperty(PROP_CLIENTFACADE_CLASSNAME);
         if (clientClass == null || clientClass.length() < 1)
         {
            this.setError(true);
            this.setState(State.ERROR);
            this.setStatus(METHOD_NAME
               + "Property '" + PROP_CLIENTFACADE_CLASSNAME
               + "' has a null value or is not set.");
         }
         else
         {
            /*
             * initialize the client
             */
            try
            {
               obj = Class.forName(clientClass).newInstance();
               _facade = (ClientFacadeIF) obj;
               _facade.init(url, username, password, host, iPort);
            }
            catch (Exception ex)
            {
               this.setError(true);
               this.setState(State.ERROR);
               this.setStatus(METHOD_NAME + ex.getMessage());
            }
         }
      }

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   public synchronized void execute(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      Operation oper = null;

      if (request == null)
      {
         this.handleError(METHOD_NAME + "Request is null");
      }

      if (response == null)
      {
         this.handleError(METHOD_NAME + "Response is null");
      }

      response.setUniqueId(CLASS_NAME);

      oper = request.getOperation();

      switch (oper)
      {
         case CREATE:
            this.doCreate(request, response);
            break;
         case READ:
            this.doRead(request, response);
            break;
         case UPDATE:
            this.doUpdate(request, response);
            break;
         case DELETE:
            this.doDelete(request, response);
            break;
         case SEARCH:
            this.doSearch(request, response);
            break;
         case PWDCHANGE:
            this.doPasswordChange(request, response);
            break;
         case PWDRESET:
            this.doPasswordReset(request, response);
            break;
         case PWDFORGOT:
            this.doPasswordForgot(request, response);
            break;
         default:
            this.handleError(METHOD_NAME + "Unimplemented Operation: "
               + oper.toString());
      }
      return;
   }

   //  =============================
   //  ===== PROTECTED METHODS =====
   //  =============================
   //
   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doCreate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doCreate(): ";
      AddRequest createRequest = null;   // SPML2
      AddResponse createResponse = null; // SPML2

      createRequest = new AddRequest(null, // String requestId,
         ExecutionMode.SYNCHRONOUS, // ExecutionMode executionMode,
         null, // PSOIdentifier type,
         null, // PSOIdentifier containerID,
         null, // Extensible data,
         null, // CapabilityData[] capabilityData,
         null, // String targetId,
         null // ReturnData returnData
         );

      try
      {
         this.preCreate(request, createRequest);
      }
      catch (OperationException ex)
      {
         /*
          * pre-create failed, update response and (re-) throw Exception
          */
         response.setError(true);
         response.setState(request.getState());
         response.setStatus(request.getStatus());
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      try
      {
         createResponse = (AddResponse) _facade.send(createRequest);
         _facade.throwErrors(createResponse);
      }
      catch (Exception ex)
      {
         response.setError(true);
         response.setState(State.ERROR);
         response.setStatus(ex.getMessage());
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      response.setUniqueId(request);

      this.postCreate(response, createResponse);

      this.saveXML(response, createRequest, createResponse);

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doRead(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doRead(): ";
      LookupRequest readRequest = null;   // SPML2
      LookupResponse readResponse = null; // SPML2

      readRequest = new LookupRequest();

      try
      {
         this.preRead(request, readRequest);
      }
      catch (OperationException ex)
      {
         /*
          * pre-read failed, update response and (re-) throw Exception
          */
         response.setError(true);
         response.setState(request.getState());
         response.setStatus(request.getStatus());
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      try
      {
         readResponse = (LookupResponse) _facade.send(readRequest);
         _facade.throwErrors(readResponse);
      }
      catch (Exception ex)
      {
         response.setError(true);
         response.setState(State.ERROR);
         response.setStatus(ex.getMessage());
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      this.postRead(response, readResponse);

      this.saveXML(response, readRequest, readResponse);

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doUpdate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doUpdate(): ";
      ModifyRequest modifyRequest = null;   // SPML2
      ModifyResponse modifyResponse = null; // SPML2

      modifyRequest = new ModifyRequest();

      try
      {
         this.preUpdate(request, modifyRequest);
      }
      catch (OperationException ex)
      {
         /*
          * pre-update failed, update response and (re-) throw Exception
          */
         response.setError(true);
         response.setState(request.getState());
         response.setStatus(request.getStatus());
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      try
      {
         modifyResponse = (ModifyResponse) _facade.send(modifyRequest);
         _facade.throwErrors(modifyResponse);
      }
      catch (Exception ex)
      {
         response.setError(true);
         response.setState(State.ERROR);
         response.setStatus(ex.getMessage());
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      this.postUpdate(response, modifyResponse);

      this.saveXML(response, modifyRequest, modifyResponse);

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doDelete(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doDelete(): ";
      DeleteRequest deleteRequest = null;   // SPML2
      DeleteResponse deleteResponse = null; // SPML2

      deleteRequest = new DeleteRequest("request-2",
         ExecutionMode.SYNCHRONOUS,
         null,
         true);

      try
      {
         this.preDelete(request, deleteRequest);
      }
      catch (OperationException ex)
      {
         /*
          * pre-delete failed, update response and (re-) throw Exception
          */
         response.setError(true);
         response.setState(request.getState());
         response.setStatus(request.getStatus());
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      try
      {
         deleteResponse = (DeleteResponse) _facade.send(deleteRequest);
         _facade.throwErrors(deleteResponse);
      }
      catch (Exception ex)
      {
         response.setError(true);
         response.setState(State.ERROR);
         response.setStatus(ex.getMessage());
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      this.postDelete(response, deleteResponse);

      this.saveXML(response, deleteRequest, deleteResponse);

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doSearch(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doSearch(): ";
      SearchRequest searchRequest = null;   // SPML
      SearchResponse searchResponse = null; // SPML

      searchRequest = new SearchRequest();

      try
      {
         this.preSearch(request, searchRequest);
      }
      catch (OperationException ex)
      {
         /*
          * pre-search failed, update response and (re-) throw Exception
          */
         response.setError(true);
         response.setState(request.getState());
         response.setStatus(request.getStatus());
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      try
      {
         searchResponse = (SearchResponse) _facade.send(searchRequest);
         _facade.throwErrors(searchResponse);
      }
      catch (Exception ex)
      {
         response.setError(true);
         response.setState(State.ERROR);
         response.setStatus(ex.getMessage());
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      this.postSearch(response, searchResponse);

      this.saveXML(response, searchRequest, searchResponse);

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doPasswordChange(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doPasswordChange(): ";
      SetPasswordResponse setPwdResponse = null;
      SetPasswordRequest setPwdRequest = null;

      setPwdRequest = new SetPasswordRequest();

      try
      {
         this.prePasswordChange(request, setPwdRequest);
      }
      catch (OperationException ex)
      {
         /*
          * pre-passwordChange failed, update response and (re-) throw Exception
          */
         response.setError(true);
         response.setState(request.getState());
         response.setStatus(request.getStatus());
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      try
      {
         setPwdResponse = (SetPasswordResponse) _facade.send(setPwdRequest);
      }
      catch (Exception ex)
      {
         response.setError(true);
         response.setState(State.ERROR);
         response.setStatus(ex.getMessage());
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      this.postPasswordChange(response, setPwdResponse);

      this.saveXML(response, setPwdRequest, setPwdResponse);

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void doPasswordReset(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":doPasswordReset(): ";
      ResetPasswordResponse resetPwdResponse = null;
      ResetPasswordRequest resetPwdRequest = null;

      resetPwdRequest = new ResetPasswordRequest();

      try
      {
         this.prePasswordReset(request, resetPwdRequest);
      }
      catch (OperationException ex)
      {
         /*
          * pre-passwordReset failed, update response and (re-) throw Exception
          */
         response.setError(true);
         response.setState(request.getState());
         response.setStatus(request.getStatus());
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      try
      {
         resetPwdResponse = (ResetPasswordResponse) _facade.send(resetPwdRequest);
         _facade.throwErrors(resetPwdResponse);
      }
      catch (Exception ex)
      {
         response.setError(true);
         response.setState(State.ERROR);
         response.setStatus(ex.getMessage());
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      this.postPasswordReset(response, resetPwdResponse);

      this.saveXML(response, resetPwdRequest, resetPwdResponse);

      return;
   }

   /**
    * @param request
    * @param createRequest
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void preCreate(final RequestIF request, final AddRequest createRequest) throws OperationException
   //----------------------------------------------------------------
   {
      String requestId = null;
      String METHOD_NAME = CLASS_NAME + ":preCreate(): ";
      String mapKey = null;
      String uniqueId = null;
      String objectClass = null;
      String err = null;
      Map<String, AttrIF> attrMap = null;
      Iterator<String> iter = null;
      DSMLAttr dsmlAttr = null;
      Extensible data = null;

      uniqueId = request.getSubject().getUniqueId().toString();

      if (uniqueId == null || uniqueId.length() < 1)
      {
         err = "uniqueId is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      request.setUniqueId(uniqueId);

      objectClass = this.findProperty(request, PROP_SPML2_OBJECTCLASS);

      try
      {
         data = new Extensible();

         if (objectClass == null || objectClass.length() < 1)
         {
            requestId = uniqueId;
         }
         else
         {
            requestId = objectClass + ":" + uniqueId;
            data.addOpenContentElement(new DSMLAttr("objectclass", objectClass));
         }

         data.addOpenContentElement(new DSMLAttr(request.getKey(), uniqueId));

         /*
          * Get each PTK Attribute that the Subject has
          * create a new DSML Attribute from the PTK Attribute
          * add the Attribute to the SPML2 createRequest (AddRequest)
          */

         attrMap = request.getSubject().getAttributes();

         if (attrMap == null || attrMap.isEmpty())
         {
            err = "Subject has no attributes";
            request.setError(true);
            request.setState(State.FAILED);
            request.setStatus(err);
            this.handleError(METHOD_NAME + err);
         }

         iter = attrMap.keySet().iterator();
         while (iter.hasNext())
         {
            mapKey = iter.next();
            dsmlAttr = this.getDsmlAttribute(attrMap.get(mapKey));
            data.addOpenContentElement(dsmlAttr);
         }

         createRequest.setRequestID(requestId);
         createRequest.setData(data);
      }
      catch (Exception ex)
      {
         err = ex.getMessage();
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      return;
   }

   /**
    * @param response
    * @param createResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void postCreate(final ResponseIF response, final AddResponse createResponse) throws OperationException
   //----------------------------------------------------------------
   {
      response.setDescription(RESPONSE_DESC + ": Create");

      if (!response.isError())
      {
         if (createResponse.getStatus().equals(StatusCode.SUCCESS))
         {
            response.setStatus("Created");
            response.setState(State.SUCCESS);
         }
         else if (createResponse.getStatus().equals(StatusCode.FAILURE))
         {
            response.setStatus("Failure: " + createResponse.getErrorMessages().toString());
            response.setState(State.FAILED);
            response.setError(true);
         }
         else if (createResponse.getStatus().equals(StatusCode.PENDING))
         {
            response.setStatus("Pending: " + createResponse.getErrorMessages().toString());
            response.setState(State.FAILED);
            response.setError(true);
         }
         else
         {
            response.setStatus("Unknow StatusCode: " + createResponse.getErrorMessages().toString());
            response.setState(State.FAILED);
            response.setError(true);
         }
      }
      else
      {
         if (createResponse != null)
         {
            if (createResponse.getStatus().equals(StatusCode.FAILURE))
            {
               response.setError(true);
               response.setState(State.FAILED);
               response.setStatus(createResponse.getErrorMessages().toString()); //should handle list of errors
            }
         }
      }

      return;
   }

   /**
    * @param request
    * @param readRequest
    * @return
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void preRead(final RequestIF request, final LookupRequest readRequest) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":preRead(): ";
      String subjectUid = null;
      String err = null;
      ComponentIF subject = null; // OpenPTK
      PSOIdentifier psoId = null;

      subject = request.getSubject();

      if (subject == null)
      {
         err = "Subject is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      subjectUid = subject.getUniqueId().toString();

      if (subjectUid == null || subjectUid.length() < 1)
      {
         err = "Subject uniqueId is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      psoId = new PSOIdentifier(subjectUid, null, null);
      readRequest.setPsoID(psoId);
      readRequest.setExecutionMode(ExecutionMode.SYNCHRONOUS);
      readRequest.setReturnData(ReturnData.EVERYTHING);

      return;
   }

   /**
    * @param response
    * @param readResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void postRead(final ResponseIF response, final LookupResponse readResponse) throws OperationException
   //----------------------------------------------------------------
   {
      PSO pso = null;           // SPML2
      Component subject = null; // OpenPTK

      response.setDescription(RESPONSE_DESC + ": Read");

      if (!response.isError())
      {
         if (readResponse != null)
         {
            if (readResponse.getStatus().equals(StatusCode.FAILURE))
            {
               response.setError(true);
               response.setState(State.FAILED);
               response.setStatus(readResponse.getErrorMessages().toString());  //should handle all messages as list
            }
            else if (readResponse.getStatus().equals(StatusCode.PENDING))
            {
               response.setError(false);
               response.setState(State.FAILED); // was WAITING
               response.setStatus(readResponse.getErrorMessages().toString());  //should handle all messages as list
            }
            else if (readResponse.getStatus().equals(StatusCode.SUCCESS))
            {
               pso = readResponse.getPso();
               if (pso.isValid())
               {
                  subject = this.getComponent(response.getRequest(), pso);
                  response.setState(State.SUCCESS);

                  if (subject != null)
                  {
                     subject.setDebugLevel(response.getDebugLevel());
                     subject.setDebug(response.isDebug());

                     response.addResult(subject);
                  }
                  else
                  {
                     response.setStatus("Result had a null subject");
                     response.setState(State.FAILED);
                     response.setError(true);
                  }
               }
               else
               {
                  response.setState(State.FAILED);
                  response.setStatus("Response has an invalid PSO");
                  response.setError(true);
               }
            }
         }
         else
         {
            response.setState(State.FAILED);
            response.setStatus("Response is NULL");
            response.setError(true);
         }
      }

      return;
   }

   /**
    * @param request
    * @param modifyRequest
    * @return
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void preUpdate(final RequestIF request, final ModifyRequest modifyRequest) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":preUpdate(): ";
      String subjectUid = null;
      String mapKey = null;
      String err = null;
      Map<String, AttrIF> attrMap = null;
      Iterator<String> iter = null;
      PSOIdentifier psoId = null;  //      SPML2
      Modification modification = null; // SPML2
      ComponentIF subject = null;  //      OpenPTK

      /*
       * Get each PTK Attribute that the Subject has,
       * create a new SPML2 Attribute from the PTK Attribute
       * add the SPML2 Attribute to the SPML2 createRequest (AddRequest)
       */

      subject = request.getSubject();

      if (subject == null)
      {
         err = "Subject is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      subjectUid = subject.getUniqueId().toString();

      if (subjectUid == null || subjectUid.length() < 1)
      {
         err = "Subject uniqueid is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      psoId = new PSOIdentifier(subjectUid, null, null);
      modifyRequest.setPsoID(psoId);

      attrMap = subject.getAttributes();

      if (attrMap == null || attrMap.isEmpty())
      {
         err = "Subject has no attributes";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      iter = attrMap.keySet().iterator();
      while (iter.hasNext())
      {
         mapKey = iter.next();
         modification = this.getDsmlModification(attrMap.get(mapKey));
         if (modification != null)
         {
            modifyRequest.addModification(modification);
         }
      }

      return;
   }

   /**
    * @param response
    * @param modifyResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void postUpdate(final ResponseIF response, final ModifyResponse modifyResponse) throws OperationException
   //----------------------------------------------------------------
   {
      response.setDescription(RESPONSE_DESC + ": Update");

      if (!response.isError())
      {
         if (modifyResponse.getStatus().equals(StatusCode.SUCCESS))
         {
            response.setStatus("updated");
            response.setState(State.SUCCESS);
         }
         else if (modifyResponse.getStatus().equals(StatusCode.PENDING))
         {
            response.setError(true);
            response.setState(State.FAILED); // was WAITING
            response.setStatus("pending");
         }
         else
         {
            response.setError(true);
            response.setState(State.FAILED);
            response.setStatus(modifyResponse.getErrorMessages().toString());  // Should handle list of errors
         }
      }

      return;
   }

   /**
    * @param request
    * @param deleteRequest
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void preDelete(final RequestIF request, final DeleteRequest deleteRequest) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":preDelete(): ";
      String subjectUid = null;
      String objectClass = null;
      String err = null;
      PSOIdentifier psoId = null; // SPML2
      ComponentIF subject = null; // OpenPTK

      subject = request.getSubject();

      if (subject == null)
      {
         err = "Subject is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      subjectUid = subject.getUniqueId().toString();

      if (subjectUid == null || subjectUid.length() < 1)
      {
         err = "Subject uniqueId is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      psoId = new PSOIdentifier(null, null, null);

      objectClass = this.findProperty(request, PROP_SPML2_OBJECTCLASS);

      if (objectClass == null || objectClass.length() < 1)
      {
         psoId.setID(subjectUid);
      }
      else
      {
         psoId.setID(objectClass + ":" + subjectUid);
      }

      deleteRequest.setPsoID(psoId);

      return;
   }

   /**
    * @param response
    * @param deleteResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void postDelete(final ResponseIF response, final DeleteResponse deleteResponse) throws OperationException
   //----------------------------------------------------------------
   {
      response.setDescription(RESPONSE_DESC + ": Delete");

      if (!response.isError())
      {
         if (deleteResponse.getStatus().equals(StatusCode.SUCCESS))
         {
            response.setStatus("deleted");
            response.setState(State.SUCCESS);
         }
         else if (deleteResponse.getStatus().equals(StatusCode.PENDING))
         {
            response.setError(true);
            response.setState(State.FAILED); // was WAITING
            response.setStatus("waiting");
         }
         else
         {
            response.setError(true);
            response.setState(State.FAILED);
            response.setStatus(deleteResponse.getErrorMessages().toString());  //This is a list of error messages which should be handled
         }
      }

      return;
   }

   /**
    * @param request
    * @param searchRequest
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void preSearch(final RequestIF request, final SearchRequest searchRequest) throws OperationException
   //----------------------------------------------------------------
   {
      searchRequest.setExecutionMode(ExecutionMode.SYNCHRONOUS);
      searchRequest.setReturnData(ReturnData.EVERYTHING);

      this.addSearchRequestFilter(searchRequest, request);

      return;
   }

   /**
    * @param response
    * @param searchResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void postSearch(final ResponseIF response, final SearchResponse searchResponse) throws OperationException
   //----------------------------------------------------------------
   {
      int iItems = 0;
      PSO psosResult[] = null;  // SPML2
      Component subject = null; // OpenPTK

      response.setDescription(RESPONSE_DESC + ": Search");

      /*
       * check for error condition and update the OpenPTK response
       */

      if (!response.isError())
      {
         if (searchResponse != null)
         {
            if (!searchResponse.isValid())
            {
               response.setError(true);
               response.setState(State.FAILED);
               response.setStatus(searchResponse.getErrorMessages().toString());
            }
            else
            {
               psosResult = searchResponse.getPSOs();

               if (psosResult != null)
               {
                  iItems = psosResult.length;

                  for (int k = 0; k < iItems; k++)
                  {
                     // OpenContentElement[] attrs = psosResult[k].getData().getOpenContentElements();

                     subject = this.getComponent(response.getRequest(), psosResult[k]);

                     if (subject != null)
                     {
                        subject.setDebugLevel(response.getDebugLevel());
                        subject.setDebug(response.isDebug());
                        response.addResult(subject);
                     }
                     else
                     {
                        response.setError(true);
                        response.setState(State.FAILED);
                        response.setStatus("SPML2 Result had a null subject");
                     }
                  }

                  response.setStatus("" + iItems + " entries returned");
                  response.setState(State.SUCCESS);
                  response.setDescription(RESPONSE_DESC + ": Search, " + iItems + " items");
               }
               else
               {
                  response.setStatus("Nothing was found");
                  response.setState(State.SUCCESS);
                  response.setDescription(RESPONSE_DESC + ": Search, Nothing was found");
               }
            }
         }
         else
         {
            response.setError(true);
            response.setState(State.FAILED);
            response.setStatus("SPML2 searchResponse is NULL");
         }
      }

      return;
   }

   /**
    * @param request
    * @param setPwdRequest
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void prePasswordChange(final RequestIF request, final SetPasswordRequest setPwdRequest) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":prePasswordChange(): ";
      Object attrValue = null;
      String subjectUid = null;
      String subjectAttr = null;
      String newPwd = null;
      String err = null;
      PSOIdentifier psoId = null; // SPML2
      ComponentIF subject = null; // OpenPTK
      AttrIF attr = null;         // OpenPTK

      subject = request.getSubject();

      if (subject == null)
      {
         err = "Subject is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      subjectUid = subject.getUniqueId().toString();

      if (subjectUid == null || subjectUid.length() < 1)
      {
         err = "Subject uniqueId is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      subjectAttr = this.findProperty(request, EXTREQ_ATTR_PWD_SUBJECT_ATTR);

      if (subjectAttr == null || subjectAttr.length() < 1)
      {
         err = "Property '" + EXTREQ_ATTR_PWD_SUBJECT_ATTR + "' is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      attr = subject.getAttribute(subjectAttr);

      if (attr == null)
      {
         err = "Subjects attribute '" + subjectAttr + "' is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      attrValue = attr.getValue();

      if (attrValue instanceof String)
      {
         newPwd = (String) attrValue;

         psoId = new PSOIdentifier(subjectUid, null, null);
         setPwdRequest.setPsoID(psoId);
         setPwdRequest.setPassword(newPwd);
      }
      else
      {
         err = "Password value is not a String";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      return;
   }

   /**
    * @param response
    * @param setPwdResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void postPasswordChange(final ResponseIF response, final SetPasswordResponse setPwdResponse) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":postPasswordChange(): ";
      StatusCode code = null;

      response.setDescription(RESPONSE_DESC + ": Password Change");

      code = setPwdResponse.getStatus();

      if (code.equals(StatusCode.SUCCESS))
      {
         response.setStatus("password changed");
         response.setState(State.SUCCESS);
      }
      else if (code.equals(StatusCode.FAILURE))
      {
         response.setError(true);
         response.setState(State.FAILED);
         response.setStatus(setPwdResponse.getErrorMessages().toString());  //This is a string array and should be processed
      }
      else if (code.equals(StatusCode.PENDING))
      {
         response.setStatus("pending: " + setPwdResponse.getErrorMessages().toString());
         response.setState(State.FAILED); // was WAITING
      }
      else
      {
         response.setStatus("unknown status code");
         response.setState(State.ERROR);
         response.setError(true);
         this.handleError(METHOD_NAME + response.getStatus());
      }

      return;
   }

   /**
    * @param request
    * @param resetPwdRequest
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void prePasswordReset(final RequestIF request, final ResetPasswordRequest resetPwdRequest) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":prePasswordReset(): ";
      String err = null;
      String subjectUid = null;
      PSOIdentifier psoId = null; // SPML2
      ComponentIF subject = null; // OpenPTK

      subject = request.getSubject();

      if (subject == null)
      {
         err = "Subject is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      subjectUid = subject.getUniqueId().toString();

      if (subjectUid == null || subjectUid.length() < 1)
      {
         err = "Subject is null";
         request.setError(true);
         request.setState(State.FAILED);
         request.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      psoId = new PSOIdentifier(subjectUid, null, null);
      resetPwdRequest.setPsoID(psoId);

      return;
   }

   /**
    * @param response
    * @param resetPwdResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void postPasswordReset(final ResponseIF response, final ResetPasswordResponse resetPwdResponse) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":postPasswordReset(): ";
      Component resource = null;
      AttrIF attr = null;
      StringTokenizer strTok = null;
      String resList = null;
      String resName = null;
      String resPassword = null;
      StatusCode code = null;

      response.setDescription(RESPONSE_DESC + ": Password Reset");

      code = resetPwdResponse.getStatus();

      if (code.equals(StatusCode.SUCCESS))
      {
         response.setStatus("password reset");
         response.setState(State.SUCCESS);

         // add a Result (Resource) for each password that was reset

         resList = this.findProperty(response.getRequest(), Spml2Operations.EXTREQ_ATTR_RESOURCES_VALUE);
         strTok = new StringTokenizer(resList);  // [Lighthouse ... ...]
         while (strTok.hasMoreTokens())
         {
            resName = strTok.nextToken();
            resPassword = resetPwdResponse.getPassword();

            if (resPassword != null)
            {
               attr = new BasicAttr("password", resPassword);

               resource = new Component();
               resource.setUniqueId(resName);
               resource.setDescription("Result: Password Reset");
               resource.setDebugLevel(response.getDebugLevel());
               resource.setDebug(response.isDebug());
               resource.setCategory(Category.RESOURCE);
               resource.setAttribute("password", attr);
               response.addResult(resource);
            }
         }
      }
      else if (code.equals(StatusCode.FAILURE))
      {
         response.setError(true);
         response.setState(State.FAILED);
         response.setStatus(resetPwdResponse.getErrorMessages().toString()); //This is a string array should be handled
      }
      else if (code.equals(StatusCode.PENDING))
      {
         response.setError(false);
         response.setState(State.FAILED); // was WAITING
         response.setStatus("pending: " + resetPwdResponse.getErrorMessages().toString());
      }
      else
      {
         response.setStatus("unknown status code");
         response.setState(State.ERROR);
         response.setError(true);
         this.handleError(METHOD_NAME + response.getStatus());
      }

      return;
   }

//   /**
//    * @param request
//    * @param propName
//    * @return
//    */
//   //----------------------------------------------------------------
//   protected String findProperty(final RequestIF request, final String propName)
//   //----------------------------------------------------------------
//   {
//      String propValue = null;
//
//      /*
//       * Check the Request first, then "this" object's properties
//       */
//
//      if (request != null)
//      {
//         propValue = request.getProperty(propName);
//      }
//
//      if (propValue == null)
//      {
//         propValue = this.getProperty(propName);
//      }
//
//      return propValue;
//   }
   /**
    * @param method
    * @param req
    * @param res
    */
   //----------------------------------------------------------------
   protected synchronized void logXML(final String method, final SearchRequest req, final SearchResponse res)
   //----------------------------------------------------------------
   {
      StringBuilder buf = null;
      if (this.isDebug() == true && this.getDebugLevel() == DebugLevel.FINEST)
      {
         buf = new StringBuilder();

         if (method != null)
         {
            buf.append(method);
         }
         if (req != null)
         {
            buf.append("\n").append(req.toString());  //This needs to be marshalled and converted to xml
            req.toString();
         }
         else
         {
            buf.append("\n(null SpmlRequest)");
         }

         if (res != null)
         {
            buf.append("\n").append(res.toString()); //This needs to be marchalled and converted to xml
         }
         else
         {
            buf.append("\n(null SpmlResponse)");
         }
         Logger.logError(buf.toString());
      }
      return;
   }

   //----------------------------------------------------------------
   protected final Component getComponent(final RequestIF request, final PSO pso) throws OperationException
   //----------------------------------------------------------------
   {
      int iVals = 0;
      String METHOD_NAME = CLASS_NAME + ":getComponent(): ";
      String attrSrvcName = null;
      String attrFwName = null;
      String strValue = null;
      String srvcKey = null;
      String[] strArray = null;
      Extensible data = null;                  // SPML2
      OpenContentElement[] ocElemArray = null; // SPML2
      OpenContentElement ocElem = null;        // SPML2
      DSMLAttr dsmlAttr = null;                // SPML2
      DSMLValue[] dsmlValuesArray = null;      // SPML2
      Component subject = null;                // OpenPTK
      AttrIF ptkAttr = null;                   // OpenPTK

      /*
       * Create a OpenPTK Component object from the SPML2 PSO object
       */

      // Process the PSO

      data = pso.getData();
      if (data == null)
      {
         this.handleError(METHOD_NAME + "PSO Data is null");
      }

      ocElemArray = data.getOpenContentElements();

      // Return the attributes that were returned
      // in the PSO.  They return DSML Attributes which
      // contain a lot of info about each attribute (i.e. type, ...)

      if (ocElemArray != null && ocElemArray.length > 0)
      {
         srvcKey = request.getKey();  // getKey() returns the Service Name
         if (srvcKey == null || srvcKey.length() < 1)
         {
            this.handleError(METHOD_NAME + "Request Key is NULL");
         }

         // Create the OpenPTK Component

         subject = new Component();
         subject.setCategory(Category.SUBJECT);
         subject.setDescription("SPML2 subject");

         for (int k = 0; k < ocElemArray.length; k++)
         {
            // Process the OpenContentElement[] .....
            ocElem = ocElemArray[k];
            if (ocElem instanceof DSMLAttr)
            {
               // initialize
               iVals = 0;
               attrSrvcName = null;
               attrFwName = null;
               dsmlAttr = null;
               dsmlValuesArray = null;

               dsmlAttr = (DSMLAttr) ocElem;

               // Get the Name and value of the Attribute on the Service (SPML2)

               attrSrvcName = dsmlAttr.getName();

               attrFwName = request.getService().getFwName(request.getOperation(), attrSrvcName);
               if (attrFwName == null)
               {
                  attrFwName = attrSrvcName;
               }

               dsmlValuesArray = dsmlAttr.getValues();

               // Create a PTK Attribute object with the framework name

               if (dsmlValuesArray != null)
               {
                  iVals = dsmlValuesArray.length;

                  if (attrSrvcName.equals(srvcKey))
                  {
                     //get and set the uniqueId for this subject
                     subject.setUniqueId(dsmlValuesArray[0].getValue());
                  }

                  if (iVals > 1)
                  {
                     strArray = new String[iVals];
                     for (int i = 0; i < iVals; i++)
                     {
                        strValue = null;
                        strValue = dsmlValuesArray[i].getValue();
                        if (strValue != null)
                        {
                           strArray[i] = strValue;
                        }
                     }

                     ptkAttr = new BasicAttr(attrFwName, strArray);
                  }
                  else
                  {
                     ptkAttr = new BasicAttr(attrFwName, dsmlValuesArray[0].getValue());
                  }
               }
               else
               {
                  ptkAttr = new BasicAttr(attrFwName);
               }
               ptkAttr.setServiceName(attrSrvcName);
               subject.setAttribute(attrFwName, ptkAttr);
            }
         }
         subject.setState(State.READY);
         subject.setStatus("Has " + ocElemArray.length + " attributes");
      }
      else
      {
         subject.setState(State.ERROR);
         subject.setStatus("No attributes");
      }

      return subject;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private void saveXML(final ComponentIF comp,
      final org.openspml.v2.msg.spml.Request req,
      final org.openspml.v2.msg.spml.Response res) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":saveXML(): ";
      // Create marshaller for toXML methods
      ReflectiveXMLMarshaller marshaller = new ReflectiveXMLMarshaller();

      if (this.isDebug() && this.getDebugLevel() == DebugLevel.FINEST)
      {
         if (req != null)
         {
            try
            {
               comp.setProperty(PROP_XML_REQUEST, req.toXML(marshaller));
            }
            catch (Exception e)
            {
               this.handleError(METHOD_NAME + e);
            }
         }
         else
         {
            comp.setProperty(PROP_XML_REQUEST, "null");
         }

         if (res != null)
         {
            try
            {
               comp.setProperty(PROP_XML_RESPONSE, res.toXML(marshaller));
            }
            catch (Exception e)
            {
               this.handleError(METHOD_NAME + e);
            }
         }
         else
         {
            comp.setProperty(PROP_XML_REQUEST, "null");
         }
      }
      return;
   }

   //----------------------------------------------------------------
   @SuppressWarnings("rawtypes")
   private DSMLAttr getDsmlAttribute(final AttrIF ptkAttr) throws OperationException
   //----------------------------------------------------------------
   {
      /*
       * Convert the OpenPTK AttrIF (ptkAttr) to a DSML Attribute
       * used by doCreate
       */

      boolean bMultivalued = false;
      int iVal = 0;
      Object obj = null;
      String METHOD_NAME = CLASS_NAME + ":getDsmlAttribute(): ";
      String err = null;
      Iterator attrIter = null;
      DSMLAttr dsmlAttr = null;    // SPML2
      DSMLValue[] dsmlVals = null; // SPML2
      DSMLValue dsmlVal = null;    // SPML2

      try
      {
         obj = ptkAttr.getValue();
         bMultivalued = ptkAttr.isMultivalued();

         switch (ptkAttr.getType())
         {
            case BOOLEAN:
               if (bMultivalued)
               {
                  attrIter = Arrays.asList((Boolean[]) obj).iterator();
                  dsmlVals = new DSMLValue[Arrays.asList((Boolean[]) obj).size()];
                  while (attrIter.hasNext())
                  {
                     dsmlVal = new DSMLValue((String) attrIter.next());
                     dsmlVals[iVal] = dsmlVal;
                     iVal++;
                  }

                  dsmlAttr = new DSMLAttr(ptkAttr.getServiceName(), dsmlVals);
               }
               else
               {
                  dsmlAttr = new DSMLAttr(ptkAttr.getServiceName(), ((Boolean) obj).toString());
               }
               break;
            case INTEGER:
               if (bMultivalued)
               {
                  attrIter = Arrays.asList((Integer[]) obj).iterator();
                  dsmlVals = new DSMLValue[Arrays.asList((Integer[]) obj).size()];
                  while (attrIter.hasNext())
                  {
                     dsmlVal = new DSMLValue((String) attrIter.next());
                     dsmlVals[iVal] = dsmlVal;
                     iVal++;
                  }
                  dsmlAttr = new DSMLAttr(ptkAttr.getServiceName(), dsmlVals);
               }
               else
               {
                  dsmlAttr = new DSMLAttr(ptkAttr.getServiceName(), ((Integer) obj).toString());
               }
               break;
            case LONG:
               if (bMultivalued)
               {
                  attrIter = Arrays.asList((Long[]) obj).iterator();
                  dsmlVals = new DSMLValue[Arrays.asList((Long[]) obj).size()];
                  while (attrIter.hasNext())
                  {
                     dsmlVal = new DSMLValue((String) attrIter.next());
                     dsmlVals[iVal] = dsmlVal;
                     iVal++;
                  }
                  dsmlAttr = new DSMLAttr(ptkAttr.getServiceName(), dsmlVals);
               }
               else
               {
                  dsmlAttr = new DSMLAttr(ptkAttr.getServiceName(), ((Long) obj).toString());
               }
               break;
            case STRING:
               if (bMultivalued)
               {
                  attrIter = Arrays.asList((String[]) obj).iterator();
                  dsmlVals = new DSMLValue[Arrays.asList((String[]) obj).size()];
                  while (attrIter.hasNext())
                  {
                     dsmlVal = new DSMLValue((String) attrIter.next());
                     dsmlVals[iVal] = dsmlVal;
                     iVal++;
                  }
                  dsmlAttr = new DSMLAttr(ptkAttr.getServiceName(), dsmlVals);
               }
               else
               {
                  dsmlAttr = new DSMLAttr(ptkAttr.getServiceName(), (String) obj);
               }
               break;
            default:
               dsmlAttr = new DSMLAttr(ptkAttr.getServiceName(), obj.toString());
         }

      }
      catch (Exception e)
      {
         err = "Error getting DSML attribute: '" + ptkAttr.getServiceName()
            + "': " + e.getMessage();

         this.setError(true);
         this.setState(State.ERROR);
         this.setStatus(err);
         this.handleError(METHOD_NAME + err);
      }

      return dsmlAttr;
   }

   //----------------------------------------------------------------
   @SuppressWarnings("rawtypes")
   private Modification getDsmlModification(final AttrIF ptkAttr) throws OperationException
   //----------------------------------------------------------------
   {
      /*
       * Convert the PTK Attribute to a SPML2 Modification
       * used by doUpdate
       */
      boolean bMultivalued = false;
      int iVal = 0;
      Object obj = null;
      String str = null;
      String METHOD_NAME = CLASS_NAME + ":getDsmlModification(): ";
      Iterator modIter = null;
      Modification mod = null;         // SPML2: Modification
      DSMLModification dsmlMod = null; // SPML2
      DSMLValue dsmlVal = null;        // SPML2
      DSMLValue[] dsmlVals = null;     // SPML2
      Extensible data = null;          // SPML2
      Selection component = null;      // SPML2

      obj = ptkAttr.getValue();
      bMultivalued = ptkAttr.isMultivalued();

      try
      {
         data = new Extensible();

         switch (ptkAttr.getType())
         {
            case BOOLEAN:
               if (bMultivalued)
               {
                  modIter = Arrays.asList((Boolean[]) obj).iterator();
                  dsmlVals = new DSMLValue[Arrays.asList((Boolean[]) obj).size()];
                  while (modIter.hasNext())
                  {
                     dsmlVal = new DSMLValue((String) modIter.next());
                     dsmlVals[iVal] = dsmlVal;
                     iVal++;
                  }
                  dsmlMod = new DSMLModification(ptkAttr.getServiceName(), dsmlVals, ModificationMode.REPLACE);
               }
               else
               {
                  dsmlMod = new DSMLModification(ptkAttr.getServiceName(), ((Boolean) obj).toString(), ModificationMode.REPLACE);
               }
               break;
            case INTEGER:
               if (bMultivalued)
               {
                  modIter = Arrays.asList((Integer[]) obj).iterator();
                  dsmlVals = new DSMLValue[Arrays.asList((Integer[]) obj).size()];
                  while (modIter.hasNext())
                  {
                     dsmlVal = new DSMLValue((String) modIter.next());
                     dsmlVals[iVal] = dsmlVal;
                     iVal++;
                  }

                  dsmlMod = new DSMLModification(ptkAttr.getServiceName(), dsmlVals, ModificationMode.REPLACE);
               }
               else
               {
                  str = ((Integer) obj).toString();
                  if ((str == null || str.length() < 1) && _emptyRemove)
                  {
                     dsmlMod = null;
                  }
                  else
                  {
                     dsmlMod = new DSMLModification(ptkAttr.getServiceName(), str, ModificationMode.REPLACE);
                  }
               }
               break;
            case LONG:
               if (bMultivalued)
               {
                  modIter = Arrays.asList((Long[]) obj).iterator();
                  dsmlVals = new DSMLValue[Arrays.asList((Long[]) obj).size()];
                  while (modIter.hasNext())
                  {
                     dsmlVal = new DSMLValue((String) modIter.next());
                     dsmlVals[iVal] = dsmlVal;
                     iVal++;
                  }

                  dsmlMod = new DSMLModification(ptkAttr.getServiceName(), dsmlVals, ModificationMode.REPLACE);
               }
               else
               {
                  str = ((Long) obj).toString();
                  if ((str == null || str.length() < 1) && _emptyRemove)
                  {
                     dsmlMod = null;
                  }
                  else
                  {
                     dsmlMod = new DSMLModification(ptkAttr.getServiceName(), str, ModificationMode.REPLACE);
                  }
               }
               break;
            case STRING:
               if (bMultivalued)
               {
                  modIter = Arrays.asList((String[]) obj).iterator();
                  dsmlVals = new DSMLValue[Arrays.asList((String[]) obj).size()];
                  while (modIter.hasNext())
                  {
                     dsmlVal = new DSMLValue((String) modIter.next());
                     dsmlVals[iVal] = dsmlVal;
                     iVal++;
                  }
                  dsmlMod = new DSMLModification(ptkAttr.getServiceName(), dsmlVals, ModificationMode.REPLACE);
               }
               else
               {
                  str = obj.toString();
                  if ((str == null || str.length() < 1) && _emptyRemove)
                  {
                     dsmlMod = null;
                  }
                  else
                  {
                     dsmlMod = new DSMLModification(ptkAttr.getServiceName(), str, ModificationMode.REPLACE);
                  }
               }
               break;
            default:
               dsmlMod = new DSMLModification(ptkAttr.getServiceName(), obj.toString(), ModificationMode.REPLACE);
         }

         if (dsmlMod != null)
         {
            data.addOpenContentElement(dsmlMod);
            mod = new Modification(component, data, null, ModificationMode.REPLACE);
         }
      }
      catch (Exception ex)
      {
         this.handleError(METHOD_NAME + ex);
      }

      return mod;
   }

   //----------------------------------------------------------------
   protected void addSearchRequestFilter(final SearchRequest spml2Req, final RequestIF ptkReq) throws OperationException
   //----------------------------------------------------------------
   {
      /*
       * Add a SPML2 search Filter (with one or more filterTerms) to the
       * SPML2 searchRequest:
       *
       * if there's a user supplied Query and a Service Query:
       *    "AND" the two of them together in a new Query
       * else if there's only a Service Query
       *    use the Service Query
       * else if there's only a User Query
       *    use the User Query
       *
       * The Query is converted the SPML2 Filter and added to the searchRequest
       */

      String METHOD_NAME = CLASS_NAME + ":addSearchRequestFilter(): ";
      String err = null;
      Filter filter = null;                          // SPML2
      org.openspml.v2.msg.spmlsearch.Query q = null; // SPML2
      org.openptk.api.Query srvcQuery = null;        // OpenTPK
      org.openptk.api.Query userQuery = null;        // OpenTPK
      org.openptk.api.Query spml2Query = null;       // OpenTPK
      QueryConverterIF qConverter = null;            // OpenTPK

      srvcQuery = ptkReq.getService().getQuery(ptkReq.getOperation());
      userQuery = ptkReq.getQuery();

      /*
       * if the Service Query is set
       * set its "serviceName" equal to the "frameworkName"
       * the "frameworkName is used by the Spml2QueryConverter
       */

      if (srvcQuery != null)
      {
         this.updateQueryServiceName(srvcQuery);
      }

      /*
       * if there's a Service Query and a User Query
       * create a new Query and "AND" them together
       * else use either the User or Service Query
       */

      if (srvcQuery != null && userQuery != null)
      {
         if (userQuery.getType() == org.openptk.api.Query.Type.EQ)
         {
            spml2Query = new org.openptk.api.Query(org.openptk.api.Query.Type.AND);
            spml2Query.addQuery(userQuery);
            spml2Query.addQuery(srvcQuery);
         }
         else
         {
            err = "Operation only supports a Query of Type EQ";
            ptkReq.setError(true);
            ptkReq.setState(State.ERROR);
            ptkReq.setStatus(err);
            this.handleError(METHOD_NAME + err);
         }
      }
      else if (srvcQuery != null)
      {
         spml2Query = srvcQuery;
      }
      else if (userQuery != null)
      {
         spml2Query = userQuery;
      }

      if (spml2Query != null)
      {
         qConverter = new Spml2QueryConverter(spml2Query);
         try
         {
            filter = (Filter) qConverter.convert();
         }
         catch (QueryException ex)
         {
            this.handleError(METHOD_NAME + ex.getMessage());
         }

         q = new org.openspml.v2.msg.spmlsearch.Query();
         q.addQueryClause(filter);

         spml2Req.setQuery(q);

         // update the RequestIF to have the new Query

         ptkReq.setQuery(spml2Query);
      }

      return;
   }

   //----------------------------------------------------------------
   private void init()
   //----------------------------------------------------------------
   {
      this.setDescription(Spml2Operations.DESCRIPTION);
      Spml2Operations.RESPONSE_DESC = CLASS_NAME + ", Provision Response";
      this.setType(OperationsType.SPML20);

      /*
       * Specify which operations are implemented
       */

      this.setImplemented(Operation.CREATE, true);
      this.setImplemented(Operation.READ, true);
      this.setImplemented(Operation.UPDATE, true);
      this.setImplemented(Operation.DELETE, true);
      this.setImplemented(Operation.SEARCH, true);
      this.setImplemented(Operation.PWDCHANGE, true);
      this.setImplemented(Operation.PWDRESET, true);
      this.setImplemented(Operation.PWDFORGOT, true);

      /*
       * Specify which operations are enabled (by default)
       * Can be changed at run-time
       */

      this.setEnabled(Operation.CREATE, true);
      this.setEnabled(Operation.READ, true);
      this.setEnabled(Operation.UPDATE, true);
      this.setEnabled(Operation.DELETE, true);
      this.setEnabled(Operation.SEARCH, true);
      this.setEnabled(Operation.PWDCHANGE, true);
      this.setEnabled(Operation.PWDRESET, true);
      this.setEnabled(Operation.PWDFORGOT, true);

      return;
   }
}
