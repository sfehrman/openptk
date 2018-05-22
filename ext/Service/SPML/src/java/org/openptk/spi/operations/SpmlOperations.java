/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2008-2010 Sun Microsystems, Inc.
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
 * Portions Copyright 2011 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.spi.operations;

import java.net.URL;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.openptk.api.Query;
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
import org.openptk.spi.SpmlQueryConverter;

import org.openspml.client.SpmlClient;
import org.openspml.message.AddRequest;
import org.openspml.message.AddResponse;
import org.openspml.message.Attribute;
import org.openspml.message.DeleteRequest;
import org.openspml.message.DeleteResponse;
import org.openspml.message.ExtendedRequest;
import org.openspml.message.ExtendedResponse;
import org.openspml.message.Filter;
import org.openspml.message.Identifier;
import org.openspml.message.Modification;
import org.openspml.message.ModifyRequest;
import org.openspml.message.ModifyResponse;
import org.openspml.message.SearchRequest;
import org.openspml.message.SearchResponse;
import org.openspml.message.SearchResult;
import org.openspml.message.SpmlRequest;
import org.openspml.message.SpmlResponse;
import org.openspml.util.SpmlException;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * @contributor Derrick Harcey, Sun Microsystems, Inc.
 */

/*
 * Meaning of Response State:
 *
 * ERROR   : An error with the SPML infrastructure / configuration / connection
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
public class SpmlOperations extends Operations
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String DESCRIPTION = "Service Provisioning Markup Language (SPML) 1.0";
   protected static final String PACKAGE = "org.openptk.provision.spi";
   protected static final String PROP_SPML_TRACE = "spmlTrace";
   protected static final String PROP_SPML_URL = "url";
   protected static final String PROP_SPML_OBJECTCLASS = "objectclass";
   protected static final String PROP_XML_REQUEST = "spml.xml.request";
   protected static final String PROP_XML_RESPONSE = "spml.xml.response";
   protected static final String EXTREQ_OPER_CHANGEPWD = "extreq.oper.changepwd";
   protected static final String EXTREQ_OPER_RESETPWD = "extreq.oper.resetpwd";
   protected static final String EXTREQ_ATTR_UID_NAME = "extreq.attr.uid.name";
   protected static final String EXTREQ_ATTR_PWD_NAME = "extreq.attr.pwd.name";
   protected static final String EXTREQ_ATTR_PWD_SUBJECT_ATTR = "extreq.attr.pwd.subject.attr";
   protected static final String EXTREQ_ATTR_RESOURCES_KEY = "extreq.attr.resources.key";
   protected static final String EXTREQ_ATTR_RESOURCES_VALUE = "extreq.attr.resources.value";
   protected static final String UNSUPPORTED_EXCEPTION = "UnsupportedOperationException";
   private SpmlClient _client = null;

   //----------------------------------------------------------------
   public SpmlOperations()
   //----------------------------------------------------------------
   {
      super();
      this.init();
      return;
   }

   //----------------------------------------------------------------
   private void init()
   //----------------------------------------------------------------
   {
      this.setDescription(SpmlOperations.DESCRIPTION);
      SpmlOperations.RESPONSE_DESC = CLASS_NAME + ", Provision Response ";
      this.setType(OperationsType.SPML10);
      this.setClient(new SpmlClient());

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

   //----------------------------------------------------------------
   @Override
   public void startup()
   //----------------------------------------------------------------
   {
      String strURL = null;
      URL spmlURL = null;

      this.getClient().setTrace(Boolean.parseBoolean(this.getProperty(SpmlOperations.PROP_SPML_TRACE)));

      strURL = this.getProperty(SpmlOperations.PROP_SPML_URL);

      try
      {
         spmlURL = new URL(strURL);
      }
      catch (Exception e)
      {
         this.setError(true);
         this.setState(State.ERROR);
         this.setStatus(e.getMessage());
      }

      if (spmlURL != null)
      {
         if (!this.isError())
         {
            this.getClient().setUrl(spmlURL);
         }
      }
      else
      {
         this.setError(true);
         this.setState(State.ERROR);
         this.setStatus("URL is null: " + SpmlOperations.PROP_SPML_URL + "='" + strURL + "'");
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

      AddRequest createRequest = null;   // SPML
      AddResponse createResponse = null; // SPML

      createRequest = new AddRequest();

      this.preCreate(request, createRequest);

      if (request.isError())
      {
         response.setError(true);
         response.setState(request.getState());
         response.setStatus(request.getStatus());
      }
      else
      {
         try
         {
            createResponse = (AddResponse) this.getClient().request(createRequest);
            this.getClient().throwErrors(createResponse);
         }
         catch (SpmlException ex)
         {
            this.logXML(METHOD_NAME, createRequest, createResponse);
            response.setError(true);
            response.setState(State.ERROR);
            response.setStatus(ex.getMessage());
            this.handleError(METHOD_NAME + ex.getMessage());
         }

         response.setUniqueId(request);
         
         this.postCreate(response, createResponse);

         this.saveXML(response, createRequest, createResponse);
      }

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

      SearchRequest readRequest = null;   // SPML
      SearchResponse readResponse = null; // SPML

      readRequest = new SearchRequest();

      this.preRead(request, readRequest);

      if (request.isError())
      {
         response.setError(true);
         response.setState(request.getState());
         response.setStatus(request.getStatus());
      }
      else
      {
         try
         {
            readResponse = (SearchResponse) this.getClient().request(readRequest);
            this.getClient().throwErrors(readResponse);
         }
         catch (SpmlException ex)
         {
            this.logXML(METHOD_NAME, readRequest, readResponse);
            response.setError(true);
            response.setState(State.ERROR);
            response.setStatus(ex.getMessage());
            this.handleError(METHOD_NAME + ex.getMessage());
         }

         this.postRead(response, readResponse);

         this.saveXML(response, readRequest, readResponse);
      }

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

      ModifyRequest modifyRequest = null;   // SPML
      ModifyResponse modifyResponse = null; // SPML

      modifyRequest = new ModifyRequest();

      this.preUpdate(request, modifyRequest);

      if (request.isError())
      {
         response.setError(true);
         response.setState(request.getState());
         response.setStatus(request.getStatus());
      }
      else
      {
         try
         {
            modifyResponse = (ModifyResponse) this.getClient().request(modifyRequest);
            this.getClient().throwErrors(modifyResponse);
         }
         catch (SpmlException ex)
         {
            this.logXML(METHOD_NAME, modifyRequest, modifyResponse);
            response.setError(true);
            response.setState(State.ERROR);
            response.setStatus(ex.getMessage());
            this.handleError(METHOD_NAME + ex.getMessage());
         }

         this.postUpdate(response, modifyResponse);

         this.saveXML(response, modifyRequest, modifyResponse);
      }

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

      DeleteRequest deleteRequest = null;   // SPML
      DeleteResponse deleteResponse = null; // SPML

      deleteRequest = new DeleteRequest();

      this.preDelete(request, deleteRequest);

      if (request.isError())
      {
         response.setError(true);
         response.setState(request.getState());
         response.setStatus(request.getStatus());
      }
      else
      {
         try
         {
            deleteResponse = (DeleteResponse) this.getClient().request(deleteRequest);
            this.getClient().throwErrors(deleteResponse);
         }
         catch (SpmlException ex)
         {
            this.logXML(METHOD_NAME, deleteRequest, deleteResponse);
            response.setError(true);
            response.setState(State.ERROR);
            response.setStatus(ex.getMessage());
            this.handleError(METHOD_NAME + ex.getMessage());
         }

         this.postDelete(response, deleteResponse);

         this.saveXML(response, deleteRequest, deleteResponse);
      }

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
      String msg = null;

      SearchRequest searchRequest = null;   // SPML
      SearchResponse searchResponse = null; // SPML

      searchRequest = new SearchRequest();

      this.preSearch(request, searchRequest);

      if (request.isError())
      {
         response.setError(true);
         response.setState(request.getState());
         response.setStatus(request.getStatus());
      }
      else
      {
         try
         {
            searchResponse = (SearchResponse) this.getClient().request(searchRequest);
            this.getClient().throwErrors(searchResponse);
         }
         catch (SpmlException ex)
         {
            this.logXML(METHOD_NAME, searchRequest, searchResponse);
            response.setError(true);
            msg = ex.getMessage();

            /*
             * The message contains an entire stack trace, not just the message.
             * If the meesage is related to an invalid search request, such as
             * SPE that has a missing search value, an unsupported exception is
             * thrown.  Check the message for a matching string.  If the string
             * is found ... it's really not an exception. Treat it as a FAILED
             * operation.
             */
            
            if (msg != null && msg.indexOf(UNSUPPORTED_EXCEPTION) > -1)
            {
               response.setState(State.FAILED);
               response.setStatus("The invoked operation is intentionally not supported");
            }
            else
            {
               response.setState(State.ERROR);
               response.setStatus(ex.getMessage());
               this.handleError(METHOD_NAME + ex.getMessage());
            }
         }

         this.postSearch(response, searchResponse);

         this.saveXML(response, searchRequest, searchResponse);
      }

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

      ExtendedRequest extRequest = null;   // SPML
      ExtendedResponse extResponse = null; // SPML

      extRequest = new ExtendedRequest();

      this.prePasswordChange(request, extRequest);

      if (request.isError())
      {
         response.setError(true);
         response.setState(request.getState());
         response.setStatus(request.getStatus());
      }
      else
      {
         try
         {
            extResponse = (ExtendedResponse) this.getClient().request(extRequest);
            this.getClient().throwErrors(extResponse);
         }
         catch (SpmlException ex)
         {
            this.logXML(METHOD_NAME, extRequest, extResponse);
            response.setError(true);
            response.setState(State.ERROR);
            response.setStatus(ex.getMessage());
            this.handleError(METHOD_NAME + ex.getMessage());
         }

         this.postPasswordChange(response, extResponse);


         this.saveXML(response, extRequest, extResponse);
      }

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

      ExtendedRequest extRequest = null;   // SPML
      ExtendedResponse extResponse = null; // SPML

      extRequest = new ExtendedRequest();

      this.prePasswordReset(request, extRequest);

      if (request.isError())
      {
         response.setError(true);
         response.setState(request.getState());
         response.setStatus(request.getStatus());
      }
      else
      {
         try
         {
            extResponse = (ExtendedResponse) this.getClient().request(extRequest);
            this.getClient().throwErrors(extResponse);
         }
         catch (SpmlException ex)
         {
            this.logXML(METHOD_NAME, extRequest, extResponse);
            response.setError(true);
            response.setState(State.ERROR);
            response.setStatus(ex.getMessage());
            this.handleError(METHOD_NAME + ex.getMessage());
         }

         this.postPasswordReset(response, extResponse);

         this.saveXML(response, extRequest, extResponse);
      }

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
      String propName = null;
      String propValue = null;
      String mapKey = null;
      String uniqueId = null;
      Map<String, AttrIF> attrMap = null;
      Iterator<String> iter = null;
      Attribute spmlAttr = null; // SPML

      uniqueId = request.getSubject().getUniqueId().toString();

      propName = SpmlOperations.PROP_SPML_OBJECTCLASS;
      propValue = this.findProperty(request, propName);

      if (propValue == null || propValue.length() < 1)
      {
         createRequest.setIdentifier(uniqueId);
      }
      else
      {
         createRequest.setIdentifier(propValue + ":" + uniqueId);
      }

      request.setUniqueId(uniqueId);

      /*
       * Get each PTK Attribute that the Subject has
       * create a new SPML Attribute from the PTK Attribute
       * add the SPML Attribute to the SPML createRequest (AddRequest)
       */

      attrMap = request.getSubject().getAttributes();

      if (attrMap != null)
      {
         iter = attrMap.keySet().iterator();
         while (iter.hasNext())
         {
            mapKey = iter.next();
            spmlAttr = this.getSpmlAttribute(attrMap.get(mapKey));
            createRequest.setAttribute(spmlAttr);
         }
      }
      else
      {
         request.setError(true);
         request.setState(State.INVALID);
         request.setStatus("Create Subject has no attributes");
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
         if (createResponse != null)
         {
            response.setStatus("created");
            response.setState(State.SUCCESS);
         }
         else
         {
            response.setError(true);
            response.setStatus("SPML Response is NULL");
            response.setState(State.FAILED);
         }
      }
      else
      {
         if (createResponse != null)
         {
            if (createResponse.isFailure())
            {
               response.setError(true);
               response.setState(State.FAILED);
               response.setStatus(createResponse.getErrorMessage());
            }
         }
      }

      return;
   }


   /**
    * @param request
    * @param readRequest
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void preRead(final RequestIF request, final SearchRequest readRequest) throws OperationException
   //----------------------------------------------------------------
   {
      String err = null;
      String subjectUid = null;
      ComponentIF subject = null; // OpenPTK
      Identifier ident = null;    // SPML

      ident = new Identifier();

      subject = request.getSubject();

      if (subject != null)
      {
         subjectUid = subject.getUniqueId().toString();
         if (subjectUid != null)
         {
            ident.setId(subjectUid);
            ident.setType(Identifier.TYPE_GUID);

            readRequest.setIdentifier(ident);
         }
         else
         {
            err = "Read requires an Identifier";
         }
      }
      else
      {
         err = "Read Request has a null Subject";
      }

      if (err == null)
      {
         this.addSearchRequestAttributes(readRequest, request);
         this.addSearchRequestFilter(readRequest, request);
      }
      else
      {
         request.setError(true);
         request.setState(State.INVALID);
         request.setStatus(err);
      }

      return;
   }


   /**
    * @param response
    * @param readResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @SuppressWarnings("rawtypes")
   protected void postRead(final ResponseIF response, final SearchResponse readResponse) throws OperationException
   //----------------------------------------------------------------
   {
      String subjectUid = null;
      String err = null;
      List results = null;
      Component subject = null;       // OpenPTK
      SearchResult readResult = null; // SPML API

      response.setDescription(RESPONSE_DESC + ": Read");

      if (!response.isError())
      {
         if (readResponse != null)
         {
            if (readResponse.isFailure())
            {
               response.setError(true);
               response.setState(State.FAILED);
               response.setStatus(readResponse.getErrorMessage());
            }
            else
            {
               subjectUid = readResponse.getRequestId();
               response.setState(State.SUCCESS);

               results = readResponse.getResults();

               if (results != null)
               {
                  readResult = (SearchResult) results.get(0);
                  subject = this.getComponent(response.getRequest(), readResult);

                  if (subject != null)
                  {
                     subject.setDebugLevel(response.getDebugLevel());
                     subject.setDebug(response.isDebug());

                     response.addResult(subject);
                  }
                  else
                  {
                     err = "SPML Result had a null subject";
                  }
               }
               else
               {
                  err = "Not found, Results are NULL";
               }
            }
         }
         else
         {
            err = "SPML readResponse is NULL";
         }
      }

      if (err != null)
      {
         response.setError(true);
         response.setState(State.FAILED);
         response.setStatus(err);
      }

      return;
   }


   /**
    * @param request
    * @param modifyRequest
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void preUpdate(final RequestIF request, final ModifyRequest modifyRequest) throws OperationException
   //----------------------------------------------------------------
   {
      String err = null;
      String propName = null;
      String propValue = null;
      String subjectUid = null;
      String mapKey = null;
      Map<String, AttrIF> attrMap = null;
      Iterator<String> iter = null;
      ComponentIF subject = null;  // OpenPTK
      Modification spmlMod = null; // SPML

      propName = SpmlOperations.PROP_SPML_OBJECTCLASS;
      propValue = this.findProperty(request, propName);

      /*
       * Get each PTK Attribute that the Subject has
       * create a new SPML Attribute from the PTK Attribute
       * add the SPML Attribute to the SPML createRequest (AddRequest)
       */

      subject = request.getSubject();

      if (subject != null)
      {
         subjectUid = subject.getUniqueId().toString();
         if (subjectUid != null)
         {
            if (propValue == null || propValue.length() < 1)
            {
               modifyRequest.setIdentifier(subjectUid);
            }
            else
            {
               modifyRequest.setIdentifier(propValue + ":" + subjectUid);
            }

            attrMap = subject.getAttributes();
            if (attrMap != null)
            {
               iter = attrMap.keySet().iterator();
               while (iter.hasNext())
               {
                  mapKey = iter.next();
                  spmlMod = this.getSpmlModification(attrMap.get(mapKey));
                  modifyRequest.addModification(spmlMod);
               }
            }
            else
            {
               err = "Subject has no attributes";
            }
         }
         else
         {
            err = "Update requires an Identifier";
         }
      }
      else
      {
         err = "Update Request's Subject is NULL";
      }

      if (err != null)
      {
         request.setError(true);
         request.setState(State.INVALID);
         request.setStatus(err);
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
         if (modifyResponse.isFailure())
         {
            response.setError(true);
            response.setState(State.FAILED);
            response.setStatus(modifyResponse.getErrorMessage());
         }
         else
         {
            response.setStatus("updated");
            response.setState(State.SUCCESS);
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
      String err = null;
      String propName = null;
      String propValue = null;
      String subjectUid = null;
      ComponentIF subject = null;

      propName = SpmlOperations.PROP_SPML_OBJECTCLASS;
      propValue = this.findProperty(request, propName);

      subject = request.getSubject();

      if (subject != null)
      {
         subjectUid = subject.getUniqueId().toString();
         if (subjectUid != null)
         {
            if (propValue == null || propValue.length() < 1)
            {
               deleteRequest.setIdentifier(subjectUid);
            }
            else
            {
               deleteRequest.setIdentifier(propValue + ":" + subjectUid);
            }
         }
         else
         {
            err = "Delete requires an Identifier";
         }
      }
      else
      {
         err = "Delete Request Subject is NULL";
      }

      if (err != null)
      {
         request.setError(true);
         request.setState(State.INVALID);
         request.setStatus(err);
      }

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
         if (deleteResponse.isFailure())
         {
            response.setError(true);
            response.setState(State.FAILED);
            response.setStatus(deleteResponse.getErrorMessage());
         }
         else
         {
            response.setStatus("deleted");
            response.setState(State.SUCCESS);
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
      this.addSearchRequestAttributes(searchRequest, request);
      this.addSearchRequestFilter(searchRequest, request);

      return;
   }


   /**
    * @param response
    * @param searchResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @SuppressWarnings("rawtypes")
   protected void postSearch(final ResponseIF response, final SearchResponse searchResponse) throws OperationException
   //----------------------------------------------------------------
   {
      int iItems = 0;
      List results = null;
      Iterator iterResults = null;
      SearchResult searchResult = null; // SPML
      Component subject = null;         // OpenPTK

      response.setDescription(RESPONSE_DESC + ": Search");

      /*
       * check for error condition and update the OpenPTK response
       */

      if (!response.isError())
      {
         if (searchResponse != null)
         {
            if (searchResponse.isFailure())
            {
               response.setError(true);
               response.setState(State.FAILED);
               response.setStatus(searchResponse.getErrorMessage());
            }
            else
            {
               results = searchResponse.getResults();

               if (results != null)
               {
                  iItems = results.size();
                  iterResults = results.iterator();

                  //
                  // For each "result" from the SPML search
                  // create a new Subject (Component) and add the results
                  //

                  while (iterResults.hasNext())
                  {
                     searchResult = (SearchResult) iterResults.next();

                     subject = this.getComponent(response.getRequest(), searchResult);

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
                        response.setStatus("SPML Result had a null subject");
                     }
                  }

                  response.setStatus(iItems + " entries returned");
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
            response.setStatus("SPML searchResponse is NULL");
         }
      }
      return;
   }


   /**
    * @param request
    * @param extRequest
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void prePasswordChange(final RequestIF request, final ExtendedRequest extRequest) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":prePasswordChange(): ";
      String err = null;
      Object attrValue = null;
      String propName = null;
      String propValue = null;
      String subjectUid = null;
      String newPwd = null;
      ComponentIF subject = null; // OpenPTK
      AttrIF attr = null;         // OpenPTK

      subject = request.getSubject();

      if (subject != null)
      {
         subjectUid = subject.getUniqueId().toString();

         if (subjectUid != null)
         {
            propName = SpmlOperations.EXTREQ_ATTR_PWD_SUBJECT_ATTR;
            propValue = this.findProperty(request, propName);

            if (propValue != null && propValue.length() > 0)
            {
               attr = subject.getAttribute(propValue);

               if (attr != null)
               {
                  attrValue = attr.getValue();

                  if (attrValue instanceof String)
                  {
                     newPwd = (String) attrValue;

                     // set extended request operation: "changeUserPassword"

                     extRequest.setOperationIdentifier(
                        this.findProperty(request, SpmlOperations.EXTREQ_OPER_CHANGEPWD));

                     // set an attribute for the unique id, "accountId"

                     extRequest.setAttribute(
                        this.findProperty(request, SpmlOperations.EXTREQ_ATTR_UID_NAME),
                        subjectUid);

                     // set the users password

                     extRequest.setAttribute(
                        this.findProperty(request, SpmlOperations.EXTREQ_ATTR_PWD_NAME),
                        newPwd);

                     // set attribute (accounts) which is a comma list of Resources

                     extRequest.setAttribute(
                        this.findProperty(request, SpmlOperations.EXTREQ_ATTR_RESOURCES_KEY),
                        this.findProperty(request, SpmlOperations.EXTREQ_ATTR_RESOURCES_VALUE));
                  }
                  else
                  {
                     err = "Password value is not a String";
                  }
               }
               else
               {
                  err = "Subjects attribute value is null: " + propValue;
               }
            }
            else
            {
               this.handleError(METHOD_NAME + "Property value is null: "
                  + propName);
            }
         }
         else
         {
            err = "Password Change requires an Identifier";
         }
      }
      else
      {
         err = "Request Subject is null";
      }

      if (err != null)
      {
         request.setError(true);
         request.setState(State.INVALID);
         request.setStatus(err);
      }

      return;
   }


   /**
    * @param response
    * @param extResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void postPasswordChange(final ResponseIF response, final ExtendedResponse extResponse) throws OperationException
   //----------------------------------------------------------------
   {
      String result = null;

      response.setDescription(RESPONSE_DESC + ": Password Change");

      if (!response.isError())
      {
         if (extResponse.isFailure())
         {
            response.setError(true);
            response.setState(State.FAILED);
            response.setStatus(extResponse.getErrorMessage());
         }
         else
         {
            result = extResponse.getResult();
            if (result == null)
            {
               result = "";
            }
            response.setStatus("password changed " + result);
            response.setState(State.SUCCESS);
         }
      }
      return;
   }


   /**
    * @param request
    * @param extRequest
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void prePasswordReset(final RequestIF request, final ExtendedRequest extRequest) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":prePasswordReset(): ";
      String err = null;
      String subjectUid = null;
      String propName = null;
      String propValue = null;
      ComponentIF subject = null;

      subject = request.getSubject();

      if (subject != null)
      {
         subjectUid = subject.getUniqueId().toString();

         if (subjectUid != null)
         {
            // set extended request operation: "resetUserPassword"

            propName = SpmlOperations.EXTREQ_OPER_RESETPWD;
            propValue = this.findProperty(request, propName);
            if (propValue != null && propValue.length() > 0)
            {
               extRequest.setOperationIdentifier(propValue);
            }
            else
            {
               this.handleError(METHOD_NAME + "Property is null: " + propName);
            }

            // set an attribute for the unique id, "accountId"

            propName = SpmlOperations.EXTREQ_ATTR_UID_NAME;
            propValue = this.findProperty(request, propName);
            if (propValue != null && propValue.length() > 0)
            {
               extRequest.setAttribute(propValue, subjectUid);
            }
            else
            {
               this.handleError(METHOD_NAME + "Property is null: " + propName);
            }

            // set attribute (accounts) which is a comma list of Resources


            propName = SpmlOperations.EXTREQ_ATTR_RESOURCES_KEY;
            propValue = this.findProperty(request, propName);

            if (propValue == null || propValue.length() == 0)
            {
               this.handleError(METHOD_NAME + "Property is null: " + propName);
            }

            propName = SpmlOperations.EXTREQ_ATTR_RESOURCES_VALUE;
            propValue = this.findProperty(request, propName);

            if (propValue == null || propValue.length() == 0)
            {
               this.handleError(METHOD_NAME + "Property is null: " + propName);
            }

            extRequest.setAttribute(
               this.findProperty(request, SpmlOperations.EXTREQ_ATTR_RESOURCES_KEY),
               this.findProperty(request, SpmlOperations.EXTREQ_ATTR_RESOURCES_VALUE));

         }
         else
         {
            err = "Password Reset requires an Identifier";
         }
      }
      else
      {
         err = "Request Subject is null";
      }

      if (err != null)
      {
         request.setError(true);
         request.setState(State.INVALID);
         request.setStatus(err);
      }

      return;
   }


   /**
    * @param response
    * @param extResponse
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void postPasswordReset(final ResponseIF response, final ExtendedResponse extResponse) throws OperationException
   //----------------------------------------------------------------
   {
      Component resource = null;
      AttrIF attr = null;
      StringTokenizer strTok = null;
      String resList = null;
      String resName = null;
      String resPassword = null;
      String result = null;

      response.setDescription(RESPONSE_DESC + ": Password Reset");

      if (!response.isError())
      {
         if (extResponse.isFailure())
         {
            response.setError(true);
            response.setState(State.FAILED);
            response.setStatus(extResponse.getErrorMessage());
         }
         else
         {
            result = extResponse.getResult();
            if (result == null)
            {
               result = "";
            }
            response.setStatus("password reset " + result);
            response.setState(State.SUCCESS);

            // add a Result (Resource) for each password that was reset

            resList = this.findProperty(response.getRequest(), SpmlOperations.EXTREQ_ATTR_RESOURCES_VALUE);
            strTok = new StringTokenizer(resList);  // [Lighthouse ... ...]
            while (strTok.hasMoreTokens())
            {
               resName = strTok.nextToken();
               resPassword = (String) extResponse.getAttributeValue(resName);

               if (resPassword != null)
               {
                  attr = new BasicAttr("password", resPassword);

                  resource = new Component();
                  resource.setUniqueId(resName);
                  resource.setDescription("Result: Password Reset");
                  resource.setState(response.getState());
                  resource.setStatus("Password set to: '" + resPassword + "'");
                  resource.setDebugLevel(response.getDebugLevel());
                  resource.setDebug(response.isDebug());
                  resource.setCategory(Category.RESOURCE);
                  resource.setAttribute("password", attr);
                  response.addResult(resource);
               }
            }
         }
      }

      return;
   }


   /**
    * @param clnt
    */
   //----------------------------------------------------------------
   protected void setClient(final SpmlClient clnt)
   //----------------------------------------------------------------
   {
      _client = clnt;
      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   protected SpmlClient getClient()
   //----------------------------------------------------------------
   {
      return _client;
   }

   
   /**
    * @param comp
    * @param req
    * @param res
    */
   //----------------------------------------------------------------
   protected void saveXML(final ComponentIF comp, final SpmlRequest req, final SpmlResponse res)
   //----------------------------------------------------------------
   {
      if (this.isDebug() == true
         && this.getDebugLevel() == DebugLevel.FINEST)
      {
         if (req != null)
         {
            comp.setProperty(PROP_XML_REQUEST, req.toXml());
         }
         else
         {
            comp.setProperty(PROP_XML_REQUEST, "null");
         }

         if (res != null)
         {
            comp.setProperty(PROP_XML_RESPONSE, res.toXml());
         }
         else
         {
            comp.setProperty(PROP_XML_REQUEST, "null");
         }
      }
      return;
   }


   /**
    * @param method
    * @param req
    * @param res
    */
   //----------------------------------------------------------------
   protected void logXML(final String method, final SpmlRequest req, final SpmlResponse res)
   //----------------------------------------------------------------
   {
      StringBuffer buf = null;
      if (this.isDebug() == true && this.getDebugLevel() == DebugLevel.FINEST)
      {
         buf = new StringBuffer();

         if (method != null)
         {
            buf.append(method);
         }
         if (req != null)
         {
            buf.append("\n" + req.toXml());
         }
         else
         {
            buf.append("\n(null SpmlRequest)");
         }

         if (res != null)
         {
            buf.append("\n" + res.toXml());
         }
         else
         {
            buf.append("\n(null SpmlResponse)");
         }
         Logger.logError(buf.toString());
      }
      return;
   }

   //  ===========================
   //  ===== PRIVATE METHODS =====
   //  ===========================
   //----------------------------------------------------------------
   @SuppressWarnings("rawtypes")
   private Component getComponent(final RequestIF request, final SearchResult result)
   //----------------------------------------------------------------
   {
      int iVals = 0;
      int iCnt = 0;
      Object attrValue = null;
      String attrSrvcName = null;
      String attrFwName = null;
      String strValue = null;
      String[] strArray = null;
      List listAttrs = null;
      Iterator iterAttrs = null;
      Iterator iterValues = null;
      Component subject = null;  // OpenPTK
      AttrIF ptkAttr = null;     // OpenPTK
      Attribute spmlAttr = null; // SPML

      subject = new Component();
      subject.setCategory(Category.SUBJECT);
      subject.setUniqueId(result.getIdentifierString());
      subject.setDescription("SPML subject");

      // Instead, we are going to return the entire set that was returned
      // from the SpmlSearchRequest.  They return openspml Attributes which
      // contain a lot of info about each attribute (i.e. type, ...)
      // In this case, we are only interested in the name.

      listAttrs = result.getAttributes();

      if (listAttrs != null)
      {
         iterAttrs = listAttrs.iterator();

         while (iterAttrs.hasNext())
         {
            spmlAttr = null;
            spmlAttr = (Attribute) iterAttrs.next();

            if (spmlAttr != null)
            {
               attrSrvcName = null;
               attrFwName = null;
               attrValue = null;

               // Get the Name and value of the Attribute on the Service (SPML)

               attrSrvcName = spmlAttr.getName();


               attrFwName = request.getService().getFwName(request.getOperation(), attrSrvcName);
               if (attrFwName == null)
               {
                  attrFwName = attrSrvcName;
               }

               attrValue = spmlAttr.getValue();

               // Create a PTK Attribute object with the framework name

               if (attrValue != null)
               {
                  if (attrValue instanceof List)
                  {
                     iCnt = 0;
                     iVals = ((List) attrValue).size();
                     strArray = new String[iVals];
                     iterValues = ((List) attrValue).iterator();
                     while (iterValues.hasNext())
                     {
                        strValue = null;
                        strValue = (String) iterValues.next();
                        if (strValue != null)
                        {
                           strArray[iCnt] = strValue;
                        }
                        iCnt++;
                     }

                     ptkAttr = new BasicAttr(attrFwName, strArray);
                  }
                  else
                  {
                     ptkAttr = new BasicAttr(attrFwName, (String) attrValue);
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
         subject.setStatus("Has " + result.getAttributes().size() + " attributes");
      }
      else
      {
         subject.setState(State.ERROR);
         subject.setStatus("No attributes");
      }
      return subject;
   }

   //----------------------------------------------------------------
   private Attribute getSpmlAttribute(final AttrIF ptkAttr)
   //----------------------------------------------------------------
   {
      /*
       * Convert the OpenPTK AttrIF (ptkAttr) to a SPML Attribute
       * used by doCreate
       */

      Object obj = null;
      boolean bMultivalued = false;
      Attribute spmlAttr = null; // SPML: org.openspml.message.Attribute

      spmlAttr = new Attribute();
      spmlAttr.setName(ptkAttr.getServiceName());

      obj = ptkAttr.getValue();
      bMultivalued = ptkAttr.isMultivalued();

      switch (ptkAttr.getType())
      {
         case BOOLEAN:
            if (bMultivalued)
            {
               spmlAttr.setValue(Arrays.asList((Boolean[]) obj));
            }
            else
            {
               spmlAttr.setValue(((Boolean) obj).toString());
            }
            break;
         case INTEGER:
            if (bMultivalued)
            {
               spmlAttr.setValue(Arrays.asList((Integer[]) obj));
            }
            else
            {
               spmlAttr.setValue(((Integer) obj).toString());
            }
            break;
         case LONG:
            if (bMultivalued)
            {
               spmlAttr.setValue(Arrays.asList((Long[]) obj));
            }
            else
            {
               spmlAttr.setValue(((Long) obj).toString());
            }
            break;
         case STRING:
            if (bMultivalued)
            {
               spmlAttr.setValue(Arrays.asList((String[]) obj));
            }
            else
            {
               spmlAttr.setValue((String) obj);
            }
            break;
         default:
            spmlAttr.setValue(obj.toString());
      }

      return spmlAttr;
   }

   //----------------------------------------------------------------
   private Modification getSpmlModification(final AttrIF ptkAttr)
   //----------------------------------------------------------------
   {
      /*
       * Convert the PTK Attribute to a SPML Modification
       * used by doUpdate
       */

      Object obj = null;
      boolean bMultivalued = false;
      Modification spmlMod = null; // SPML: org.openspml.message.Modification

      spmlMod = new Modification();
      spmlMod.setName(ptkAttr.getServiceName());

      obj = ptkAttr.getValue();
      bMultivalued = ptkAttr.isMultivalued();

      switch (ptkAttr.getType())
      {
         case BOOLEAN:
            if (bMultivalued)
            {
               spmlMod.setValue(Arrays.asList((Boolean[]) obj));
            }
            else
            {
               spmlMod.setValue(((Boolean) obj).toString());
            }
            break;
         case INTEGER:
            if (bMultivalued)
            {
               spmlMod.setValue(Arrays.asList((Integer[]) obj));
            }
            else
            {
               spmlMod.setValue(((Integer) obj).toString());
            }
            break;
         case LONG:
            if (bMultivalued)
            {
               spmlMod.setValue(Arrays.asList((Long[]) obj));
            }
            else
            {
               spmlMod.setValue(((Long) obj).toString());
            }
            break;
         case STRING:
            if (bMultivalued)
            {
               spmlMod.setValue(Arrays.asList((String[]) obj));
            }
            else
            {
               spmlMod.setValue((String) obj);
            }
            break;
         default:
            spmlMod.setValue(obj.toString());
      }

      return spmlMod;
   }

   //----------------------------------------------------------------
   private void addSearchRequestAttributes(final SearchRequest spmlReq, final RequestIF ptkReq) throws OperationException
   //----------------------------------------------------------------
   {
      /*
       * Get all the PTK Request Attributes and to the SPML searchRequest
       * used by doRead and doSearch
       */

      String mapKey = null;
      Map<String, AttrIF> map = null;
      Iterator<String> iter = null;

      map = ptkReq.getSubject().getAttributes();

      if (map != null)
      {
         iter = map.keySet().iterator();
         while (iter.hasNext())
         {
            mapKey = iter.next();
            spmlReq.addAttribute(map.get(mapKey).getServiceName());
         }
      }
      else
      {
         this.handleError("no return attributes were specified");
      }

      return;
   }

   //----------------------------------------------------------------
   private void addSearchRequestFilter(final SearchRequest spmlReq, final RequestIF ptkReq) throws OperationException
   //----------------------------------------------------------------
   {
      /*
       * Add a SPML search Filter (with one or more filterTerms) to the
       * SPML searchRequest:
       *
       * if there's a user supplied Query and a Service Query:
       *    "AND" the two of them together in a new Query
       * else if there's only a Service Query
       *    use the Service Query
       * else if there's only a User Query
       *    use the User Query
       *
       * The Query is converted the SPML Filter and added to the searchRequest
       */

      String METHOD_NAME = CLASS_NAME + ":addSearchRequestFilter(): ";
      Query srvcQuery = null;             // OpenTPK
      Query userQuery = null;             // OpenTPK
      Query spmlQuery = null;             // OpenTPK
      QueryConverterIF qConverter = null; // OpenTPK
      Filter filter = null;               // SPML

      srvcQuery = ptkReq.getService().getQuery(ptkReq.getOperation());
      userQuery = ptkReq.getQuery();

      /*
       * if the Service Query is set
       * set its "serviceName" equal to the "frameworkName"
       * the "frameworkName is used by the SpmlQueryConverter
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
         if (userQuery.getType() == Query.Type.EQ)
         {
            spmlQuery = new Query(Query.Type.AND);
            spmlQuery.addQuery(userQuery);
            spmlQuery.addQuery(srvcQuery);
         }
         else
         {
            this.handleError(METHOD_NAME + "Operation only supports a Query of Type EQ");
         }
      }
      else if (srvcQuery != null)
      {
         spmlQuery = srvcQuery;
      }
      else if (userQuery != null)
      {
         spmlQuery = userQuery;
      }

      if (spmlQuery != null)
      {
         qConverter = new SpmlQueryConverter(spmlQuery);
         try
         {
            filter = (Filter) qConverter.convert();
         }
         catch (QueryException ex)
         {
            throw new OperationException(ex.getMessage());
         }

         spmlReq.setFilter(filter);

         // update the RequestIF to have the new Query

         ptkReq.setQuery(spmlQuery);
      }

      return;
   }
}
