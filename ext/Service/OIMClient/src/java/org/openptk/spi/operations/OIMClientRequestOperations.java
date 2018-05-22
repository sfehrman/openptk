/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2011 Oracle America, Inc.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import oracle.iam.request.api.RequestService;
import oracle.iam.request.exception.BulkBeneficiariesAddException;
import oracle.iam.request.exception.BulkEntitiesAddException;
import oracle.iam.request.exception.InvalidRequestDataException;
import oracle.iam.request.exception.InvalidRequestException;
import oracle.iam.request.exception.NoRequestPermissionException;
import oracle.iam.request.exception.RequestServiceException;
import oracle.iam.request.vo.Beneficiary;
import oracle.iam.request.vo.Request;
import oracle.iam.request.vo.RequestBeneficiaryEntity;
import oracle.iam.request.vo.RequestBeneficiaryEntityAttribute;
import oracle.iam.request.vo.RequestConstants;
import oracle.iam.request.vo.RequestData;
import oracle.iam.request.vo.RequestSearchCriteria;

import org.openptk.api.State;
import org.openptk.common.AttrIF;
import org.openptk.common.BasicAttr;
import org.openptk.common.Category;
import org.openptk.common.Component;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.exception.OperationException;

/**
 *
 * @author Scott Fehrman, Oracle America, Inc.
 * updated: 2011-05-24: converted to abstract class
 */
/*
 * Meaning of Response State:
 *
 * ERROR   : An error with the infrastructure / configuration / connection
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
public abstract class OIMClientRequestOperations extends OIMClientOperations
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   protected static final String PROP_ATTRIBUTE_FAILURE = "request.attribute.failure";
   protected static final String PROP_ATTRIBUTE_JUSTIFICATION = "request.attribute.justification";
   protected static final String PROP_ATTRIBUTE_STATUS = "request.attribute.status";
   protected static final String PROP_ATTRIBUTE_TEMPLATE = "request.attribute.template";
   protected static final String PROP_ATTRIBUTE_USERID = "request.attribute.userid";
   protected static final String PROP_BENEFICIARYTYPE = "request.beneficiarytype";
   protected static final String PROP_ENTITYTYPE = "request.entitytype";
   protected static final String PROP_MODEL = "request.model";
   protected String _attrJustification = null;
   protected String _attrFailure = null;
   protected String _attrStatus = null;
   protected String _attrTemplate = null;
   protected String _attrUserId = null;
   protected String _beneficiaryType = null;
   protected String _entityType = null;
   protected String _template = null;
   protected RequestService _reqSrvc = null;

   //----------------------------------------------------------------
   public OIMClientRequestOperations()
   //----------------------------------------------------------------
   {
      super();

      /*
       * Specify which operations are implemented
       */

      this.setImplemented(Operation.CREATE, true);
      this.setImplemented(Operation.READ, true);
      this.setImplemented(Operation.DELETE, true);
      this.setImplemented(Operation.SEARCH, true);

      return;
   }

   //----------------------------------------------------------------
   @Override
   public void startup()
   //----------------------------------------------------------------
   {
      super.startup();

      /*
       * Get the OIM Service that handles "Requests"
       */

      _reqSrvc = _oimClientProxy.getService(RequestService.class);

      /*
       * Get the following properties:
       *    entitytype ... "Role"
       *    beneficiarytype ... "user"
       *
       * The following properties are for attribute names that need to be used
       * as arguments to various OIMClient methods calls, vs. being treated
       * as a "typical" attribute:
       *    justification
       *    template
       *    userId
       *
       * These propteries define attributes that return data from
       * the OIMClient operations READ and SEARCH
       *    status
       *    failure
       */

      try
      {
         _entityType = this.getCheckProp(PROP_ENTITYTYPE);
         _beneficiaryType = this.getCheckProp(PROP_BENEFICIARYTYPE);
         _attrJustification = this.getCheckProp(PROP_ATTRIBUTE_JUSTIFICATION);
         _attrTemplate = this.getCheckProp(PROP_ATTRIBUTE_TEMPLATE);
         _attrUserId = this.getCheckProp(PROP_ATTRIBUTE_USERID);
         _attrStatus = this.getCheckProp(PROP_ATTRIBUTE_STATUS);
         _attrFailure = this.getCheckProp(PROP_ATTRIBUTE_FAILURE);
      }
      catch (OperationException ex)
      {
         this.setState(State.ERROR);
         this.setStatus(ex.getMessage());
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
   @Override
   protected void doCreate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String msg = null;
      String result = null;
      StringBuilder err = new StringBuilder();
      RequestData reqData = null; // OIMClient

      reqData = new RequestData();

      try
      {
         this.preCreate(request, reqData);
      }
      catch (OperationException ex)
      {
         msg = ex.getMessage();
         response.setState(State.ERROR);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      /*
       * execute the implementation logic for a CREATE
       */

      try
      {
         result = _reqSrvc.submitRequest(reqData);
      }
      catch (InvalidRequestException ex)
      {
         response.setState(State.INVALID);
         msg = ex.getMessage();
      }
      catch (InvalidRequestDataException ex)
      {
         response.setState(State.INVALID);
         msg = ex.getMessage();
      }
      catch (BulkBeneficiariesAddException ex)
      {
         response.setState(State.ERROR);
         msg = ex.getMessage();
      }
      catch (BulkEntitiesAddException ex)
      {
         response.setState(State.ERROR);
         msg = ex.getMessage();
      }
      catch (RequestServiceException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());

         response.setState(State.ERROR);
         response.setStatus(err.toString());

         this.checkException(ex, err);
         this.handleError(err.toString());
      }

      if (msg != null)
      {
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      /*
       * Post process the response
       */

      this.postCreate(response, result);

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void doRead(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      Object uid = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String msg = null;
      StringBuilder err = new StringBuilder();
      Request req = null; // OIMClient
      ComponentIF subject = null;

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject (from Request) is null");
      }

      uid = subject.getUniqueId();
      if (uid == null)
      {
         this.handleError(METHOD_NAME + "UniqueId is null");
      }

      try
      {
         req = _reqSrvc.getBasicRequestData(uid.toString());
      }
      catch (NoRequestPermissionException ex)
      {
         response.setState(State.ERROR);
         msg = ex.getMessage();
      }
      catch (RequestServiceException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());

         response.setState(State.ERROR);
         response.setStatus(err.toString());

         this.checkException(ex, err);
         this.handleError(err.toString());
      }

      if (msg != null)
      {
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      this.postRead(response, req);

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void doUpdate(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String msg = null;
      HashMap<String, Object> objMap = new HashMap<String, Object>();
      ComponentIF subject = null;

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject (from Request) is null");
      }

      try
      {
         this.preUpdate(request, objMap);
      }
      catch (OperationException ex)
      {
         msg = ex.getMessage();
         response.setState(State.ERROR);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      /*
       * This method is not implemented
       */


      if (msg != null)
      {
         response.setState(State.FAILED);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      /*
       * Post process the response
       */

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void doDelete(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      Object uid = null;
      String msg = null;
      StringBuilder err = new StringBuilder();
      ComponentIF subject = null;

      subject = request.getSubject();
      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Subject (from Request) is null");
      }

      uid = subject.getUniqueId();
      if (uid == null)
      {
         this.handleError(METHOD_NAME + "UniqueId is null");
      }

      /*
       * There is no public method for "deleting" an OIMClient Request
       * We'll do the next best thing ... "withdraw it"
       */

      try
      {
         _reqSrvc.withdrawRequest(uid.toString());
      }
      catch (RequestServiceException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());

         response.setStatus(err.toString());
         response.setState(State.ERROR);

         this.checkException(ex, err);
         this.handleError(err.toString());
      }

      if (msg != null)
      {
         this.handleError(METHOD_NAME + msg);
      }

      response.setStatus("Request Closed");
      response.setState(State.SUCCESS);

      return;
   }

   /**
    * @param request
    * @param response
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected void doSearch(final RequestIF request, final ResponseIF response) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String msg = null;
      Set<String> attrNames = null;
      StringBuilder err = new StringBuilder();
      HashMap<String, Object> mapParams = null;
      RequestSearchCriteria criteria = null;    // OIMClient
      List<Request> oimRequests = null;         // OIMClient

      try
      {
         criteria = this.preSearch(request, attrNames);
      }
      catch (OperationException ex)
      {
         msg = ex.getMessage();
         response.setState(request.getState());
         response.setStatus(request.getStatus());
         this.handleError(METHOD_NAME + msg);
      }

      mapParams = new HashMap<String, Object>();
//      mapParams.put("STARTROW", 0);
//      mapParams.put("ENDROW", -1);
      mapParams.put(RequestConstants.SEARCH_SORTORDER, RequestConstants.SortOrder.ASCENDING);
      mapParams.put(RequestConstants.SEARCH_SORTBY, RequestConstants.REQUEST_KEY);

      try
      {
         oimRequests = _reqSrvc.search(criteria, attrNames, mapParams);
      }
      catch (RequestServiceException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());

         response.setState(State.ERROR);
         response.setStatus(err.toString());

         this.checkException(ex, err);
         this.handleError(err.toString());
      }

      this.postSearch(response, oimRequests);

      return;
   }

   /**
    * 
    * @param request
    * @param reqData
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void preCreate(final RequestIF request, RequestData reqData) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String userKey = null;
      String attrValueStr = null;
      String[] attrValueArray = null;
      RequestBeneficiaryEntity entity = null;                        // OIMClient
      Beneficiary beneficiary = null;                                // OIMClient
      Set<String> roleNames = null;                                  // OIMClient
      List<RequestBeneficiaryEntity> entities = null;                // OIMClient
      List<RequestBeneficiaryEntityAttribute> entityAttrList = null; // OIMClient
      ComponentIF subject = null;                                    // OpenPTK
      AttrIF attribute = null;                                       // OpenPTK
      Map<String, AttrIF> attributes = null;                         // OpenPTK

      /*
       * By default, OpenPTK Subject Attributes are converted to
       * OMClient RequestBeneficiaryEntityAttribute objects.
       *
       * NOTE:
       * Some of the OpenPTK Subject Attributes need to be processed as
       * arguments to specific OIMClient object methods.
       * These need to be extracted from from the Subject attributes
       * before they are converted.
       */

      subject = request.getSubject();

      if (subject == null)
      {
         this.handleError(METHOD_NAME + "Request Subject is null");
      }

      /*
       * get all of the Subject's attributes
       */

      attributes = subject.getAttributes();

      if (attributes == null || attributes.isEmpty())
      {
         this.handleError(METHOD_NAME + "There are no Attributes (from the Subject)");
      }

      /*
       * Set the request "template" attribute
       */

      attrValueStr = this.getTemplateFromAttributes(attributes);
      if (attrValueStr == null || attrValueStr.length() < 1)
      {
         this.handleError(METHOD_NAME + "Attribute '" + _attrTemplate + "' has a empty/null value");
      }

      reqData.setRequestTemplateName(attrValueStr);

      /*
       * Get the userId value
       * Use the value, and the attribute name, to get the internal userKey
       * The "userKey" is how a "beneficiary" is identified
       */

      attrValueStr = this.getUserIdFromAttributes(attributes);
      userKey = this.getUserKeyFromUserLogin(attrValueStr);

      /*
       * Get the justification. If there's a value, add it to the request data
       */

      attrValueStr = this.getJustificationFromAttributes(attributes);
      if (attrValueStr != null && attrValueStr.length() > 0)
      {
         reqData.setJustification(attrValueStr);
      }

      /*
       * Get the Beneficiary from the sub-class
       */

      entity = this.getBeneficiaryEntity(attributes);

      /*
       * Get a List of BeneficiaryEntityAttributes from attributes
       */

      if (!attributes.isEmpty())
      {
         entityAttrList = this.getBeneficiaryEntityAttributes(attributes);
      }

      /*
       * Add the attributes, to the beneficiary entity, if there are any
       */

      if (entityAttrList != null && !entityAttrList.isEmpty())
      {
         entity.setEntityData(entityAttrList);
      }

      entities = new ArrayList<RequestBeneficiaryEntity>();
      entities.add(entity);

      /*
       * Create the beneficiary, set the type and user key
       * Add the list of attributes
       */

      beneficiary = new Beneficiary();
      beneficiary.setBeneficiaryType(_beneficiaryType);
      beneficiary.setBeneficiaryKey(userKey);
      beneficiary.setTargetEntities(entities);

      /*
       * Add the beneficiaries to the request data
       */

      List<Beneficiary> beneficiaries = new ArrayList<Beneficiary>();
      beneficiaries.add(beneficiary);

      reqData.setBeneficiaries(beneficiaries);

      return;
   }

   /**
    * @param request
    * @param objMap
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void preUpdate(final RequestIF request, final HashMap<String, Object> objMap) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";

      return;
   }

   /**
    * @param request
    * @param objMap
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected RequestSearchCriteria preSearch(final RequestIF request, final Set<String> attrNames) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      RequestSearchCriteria criteria = null; // OIMClient

      criteria = new RequestSearchCriteria();

      return criteria;
   }

   /**
    * @param response
    * @param result
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void postCreate(final ResponseIF response, final String result) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";

      if (result != null && result.length() > 0)
      {
         response.setUniqueId(result);
         response.setState(State.SUCCESS);
         response.setStatus("Request Id ='" + result + "'");
         response.setProperty("requestid", result);
      }
      else
      {
         response.setState(State.ERROR);
         response.setStatus("Create result is null");
      }

      return;
   }

   /**
    * @param response
    * @param request
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void postRead(final ResponseIF response, final Request oimRequest) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String msg = null;
      ComponentIF subject = null; // OpenPTK
      AttrIF ptkAttr = null;      // OpenPTK

      if (oimRequest == null)
      {
         msg = "OIMClient Request is null";
         response.setResults(new ArrayList<ComponentIF>());
         response.setState(State.ERROR);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      response.setUniqueId(oimRequest.getRequestID());

      try
      {
         subject = this.getComponentFromRequest(response.getRequest(), oimRequest);
      }
      catch (OperationException ex)
      {
         msg = ex.getMessage();
      }

      if (msg != null)
      {
         response.setState(State.ERROR);
         response.setStatus(msg);
      }
      else
      {
         response.setStatus("Entry found");
         response.addResult(subject);
         response.setState(State.SUCCESS);
      }

      return;
   }

   /**
    * 
    * @param response
    * @param oimRequests
    * @throws OperationException
    */
   //----------------------------------------------------------------
   protected void postSearch(final ResponseIF response, final List<Request> oimRequests) throws OperationException
   //----------------------------------------------------------------
   {
      int iItems = 0;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String msg = null;
      ComponentIF subject = null; // OpenPTK

      response.setDescription(RESPONSE_DESC + ": Search");

      if (oimRequests == null)
      {
         msg = "List (of Requests) is NULL";
         response.setState(State.FAILED);
         response.setStatus(msg);
         this.handleError(METHOD_NAME + msg);
      }

      if (oimRequests.isEmpty())
      {
         msg = "Nothing was found";
         response.setStatus(msg);
         response.setState(State.SUCCESS);
         response.setDescription(RESPONSE_DESC + ": Search, " + msg);
      }
      else
      {
         iItems = oimRequests.size();

         for (Request oimRequest : oimRequests)
         {
            if (oimRequest == null)
            {
               msg = "OIM Request is NULL";
               response.setState(State.FAILED);
               response.setStatus(msg);
               this.handleError(METHOD_NAME + msg);
            }

            subject = this.getComponentFromRequest(response.getRequest(), oimRequest);

            if (subject == null)
            {
               msg = "OIMClient Result had a null subject";
               response.setState(State.FAILED);
               response.setStatus(msg);
               this.handleError(METHOD_NAME + msg);
            }

            subject.setDebugLevel(response.getDebugLevel());
            subject.setDebug(response.isDebug());
            response.addResult(subject);
         }

         response.setStatus("" + iItems + " entries returned");
         response.setState(State.SUCCESS);
         response.setDescription(RESPONSE_DESC + ": Search, " + iItems + " items");
      }

      return;
   }

   //----------------------------------------------------------------
   protected String getUserIdFromAttributes(Map<String, AttrIF> attributes) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String attrValueStr = null;
      AttrIF attribute = null;

      /*
       * The "attributes" (from the OpenPTK Request/Subject) must contain
       * an attribute that is designated as the "userid".
       * The "userId" is the attribute that indicates the user that
       * would benefit from the Request.
       * The OIMClient API uses a specific method for setting the "userId"
       * Remove the "userId" from the collection of attributes,
       * it should not be loaded as a typical attribute.
       */

      attribute = attributes.get(_attrUserId);
      if (attribute == null)
      {
         this.handleError(METHOD_NAME + "Required attribute '"
            + _attrUserId + "' is null");
      }
      attrValueStr = attribute.getValueAsString();
      if (attrValueStr == null || attrValueStr.length() < 1)
      {
         this.handleError(METHOD_NAME + "Required attribute '"
            + _attrUserId + "' has no value");
      }
      attributes.remove(_attrUserId); // take it out of the Map

      return attrValueStr;
   }

   //----------------------------------------------------------------
   protected String getJustificationFromAttributes(Map<String, AttrIF> attributes)
   //----------------------------------------------------------------
   {
      String attrValueStr = null;
      AttrIF attribute = null;

      /*
       * The "attributes" (from the OpenPTK Request/Subject) must contain
       * an attribute that is designated as the "justification".
       * The "justification" is the attribute that contains a String
       * provided by the user ... why they "need/want" the resource.
       * The OIMClient API uses a specific method for setting the "justification"
       * Remove the "justification" from the collection of attributes,
       * it should NOT be loaded an a typical attribute.
       */

      attribute = attributes.get(_attrJustification);
      if (attribute != null)
      {
         attrValueStr = attribute.getValueAsString();
         if (attrValueStr != null && attrValueStr.length() > 0)
         {
            attributes.remove(_attrJustification); // take it out of the Map
         }
      }

      return attrValueStr;
   }

   //----------------------------------------------------------------
   protected String getTemplateFromAttributes(Map<String, AttrIF> attributes)
   //----------------------------------------------------------------
   {
      String attrValueStr = null;
      AttrIF attribute = null;

      /*
       * The "attributes" (from the OpenPTK Request/Subject) must contain
       * an attribute that is designated as the "template".
       * The "template" is the attribute that indicates what "type" of
       * OIM request that is being made.
       * The OIMClient API uses a specific method for setting the "template"
       * Remove the "template" from the collection of attributes,
       * it should not be loaded as a typical attribute.
       */

      attribute = attributes.get(_attrTemplate);
      if (attribute != null)
      {
         attrValueStr = attribute.getValueAsString();
         if (attrValueStr != null && attrValueStr.length() > 0)
         {
            attributes.remove(_attrTemplate); // take it out of the Map
         }
      }

      return attrValueStr;
   }

   //----------------------------------------------------------------
   protected void addAttrToSubject(ComponentIF subject, String name, String value)
   //----------------------------------------------------------------
   {
      AttrIF ptkAttr = null;         // OpenPTK

      if (value == null)
      {
         value = "";
      }

      ptkAttr = new BasicAttr(name, value);

      subject.setAttribute(name, ptkAttr);

      return;
   }

   //----------------------------------------------------------------
   protected void addAttrToSubject(ComponentIF subject, String name, String[] value)
   //----------------------------------------------------------------
   {
      AttrIF ptkAttr = null; // OpenPTK

      if (value == null)
      {
         value = new String[0];
      }

      ptkAttr = new BasicAttr(name, value);

      subject.setAttribute(name, ptkAttr);

      return;
   }

   //----------------------------------------------------------------
   protected void updateSubjectFromEntityAttributes(RequestIF request, ComponentIF subject, List<RequestBeneficiaryEntityAttribute> entityAttrList)
   //----------------------------------------------------------------
   {
      String srvcName = null;
      String fwName = null;
      AttrIF attr = null;
      RequestBeneficiaryEntityAttribute.TYPE type = null;

      /*
       * Process all of the BeneficiaryEntityAttributes
       * For each BeneficiaryEntityAttribute
       * Create a OpenPTK Attr and add it to the Subject
       *    Be sure to use the "Framework" name for the attribute
       */

      if (subject != null && entityAttrList != null && !entityAttrList.isEmpty())
      {
         for (RequestBeneficiaryEntityAttribute entityAttr : entityAttrList)
         {
            if (entityAttr != null)
            {
               srvcName = entityAttr.getName();
               fwName = request.getService().getFwName(request.getOperation(), srvcName);

               type = entityAttr.getType();
               switch (type)
               {
                  case String:
                     attr = new BasicAttr(fwName, (String) entityAttr.getValue());
                     break;
                  case Integer:
                     attr = new BasicAttr(fwName, (Integer) entityAttr.getValue());
                     break;
                  case Long:
                     attr = new BasicAttr(fwName, (Long) entityAttr.getValue());
                     break;
                  default:
                     attr = new BasicAttr(fwName, "Unsupported type: '" + type.toString() + "'");
                     break;
               }
               subject.setAttribute(fwName, attr);
            }
         }
      }

      return;
   }

   protected abstract RequestBeneficiaryEntity getBeneficiaryEntity(Map<String, AttrIF> attributes) throws OperationException;

   protected abstract void updateSubjectFromBeneficiaryEntities(RequestIF request, ComponentIF subject, List<RequestBeneficiaryEntity> entities) throws OperationException;

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private ComponentIF getComponentFromRequest(final RequestIF request, final Request oimRequest) throws OperationException
   //----------------------------------------------------------------
   {
      String name = null;
      String value = null;
      String userKey = null;
      String userLogin = null;
      ComponentIF subject = null;                     // OpenPTK
      List<Beneficiary> beneficiaries = null;         // OIMClient
      List<RequestBeneficiaryEntity> entities = null; // OIMClient
      Beneficiary beneficiary = null;                 // OIMClient

      /*
       * Create the OpenPTK "subject",
       * this will hold the OIMClient "request" data
       */

      subject = new Component();
      subject.setCategory(Category.SUBJECT);
      subject.setDescription("OIM Client Request");

      subject.setUniqueId(oimRequest.getRequestID());

      name = _attrJustification;
      value = oimRequest.getJustification();
      this.addAttrToSubject(subject, name, value);

      name = _attrFailure;
      value = oimRequest.getReasonForFailure();
      this.addAttrToSubject(subject, name, value);

      name = _attrStatus;
      value = oimRequest.getRequestStatus();
      this.addAttrToSubject(subject, name, value);

      name = _attrTemplate;
      value = oimRequest.getRequestTemplateName();
      this.addAttrToSubject(subject, name, value);

      /*
       * Get the beneficiary object (the user)
       * get the userkey then get the userlogin
       */

      beneficiaries = oimRequest.getBeneficiaries();
      if (beneficiaries != null && !beneficiaries.isEmpty())
      {
         beneficiary = beneficiaries.get(0); // ONLY THE FIRST ONE
         if (beneficiary != null
            && beneficiary.getBeneficiaryType().equalsIgnoreCase("user"))
         {
            userKey = beneficiary.getBeneficiaryKey();
            if (userKey != null && userKey.length() > 0)
            {
               userLogin = this.getUserLoginFromUserKey(userKey);
               name = this.getProperty(PROP_ATTRIBUTE_USERID);
               if (userLogin != null && userLogin.length() > 0)
               {
                  value = userLogin;
               }
               else
               {
                  value = userKey;
               }
               this.addAttrToSubject(subject, name, value);
            }

            /*
             * Update the subject from the beneficiary entities
             * This is done from a sub-class method implementation
             */

            entities = beneficiary.getTargetEntities();

            if (entities != null)
            {
               this.updateSubjectFromBeneficiaryEntities(request, subject, entities);
            }
         }
      }

      return subject;
   }

   //----------------------------------------------------------------
   private List<RequestBeneficiaryEntityAttribute> getBeneficiaryEntityAttributes(Map<String, AttrIF> attributes) throws OperationException
   //----------------------------------------------------------------
   {
      Object attrValue = null;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String serviceName = null;
      AttrIF attribute = null;                                       // OpenPTK
      RequestBeneficiaryEntityAttribute entityAttr = null;           // OIMClient
      List<RequestBeneficiaryEntityAttribute> entityAttrList = null; // OIMClient

      /*
       * Process the OpenPTK Attributes
       * For each OpenPTK attribute
       * create a OIMClient (BeneficiaryEntity) attribute
       * and return a List of the (BeneficiaryEntity) attributes
       */

      entityAttrList = new ArrayList<RequestBeneficiaryEntityAttribute>();

      for (String attrName : attributes.keySet())
      {
         attribute = attributes.get(attrName);
         if (attribute == null)
         {
            this.handleError(METHOD_NAME + "Attribute '" + attrName + "' is null");
         }

         attrValue = attribute.getValue();
         if (attrValue == null)
         {
            this.handleError(METHOD_NAME + "Value (for Attribute '" + attrName + "') is null");
         }

         serviceName = attribute.getServiceName();

         if (attribute.isMultivalued())
         {
            switch (attribute.getType())
            {
               case STRING:
                  entityAttr = this.getEntityAttr(serviceName, (String[]) attrValue);
                  break;
               case INTEGER:
                  entityAttr = this.getEntityAttr(serviceName, (Integer[]) attrValue);
                  break;
               case LONG:
                  entityAttr = this.getEntityAttr(serviceName, (Long[]) attrValue);
                  break;
               case BOOLEAN:
                  entityAttr = this.getEntityAttr(serviceName, (Boolean[]) attrValue);
                  break;
               default:
                  this.handleError(METHOD_NAME + "Attribute '" + attrName
                     + "' has a DataType of '" + attribute.getType().toString()
                     + "' which is not supported (Multivalued)");
                  break;
            }
         }
         else
         {
            switch (attribute.getType())
            {
               case STRING:
                  entityAttr = this.getEntityAttr(serviceName, (String) attrValue);
                  break;
               case INTEGER:
                  entityAttr = this.getEntityAttr(serviceName, (Integer) attrValue);
                  break;
               case LONG:
                  entityAttr = this.getEntityAttr(serviceName, (Long) attrValue);
                  break;
               case BOOLEAN:
                  entityAttr = this.getEntityAttr(serviceName, (Boolean) attrValue);
                  break;
               default:
                  this.handleError(METHOD_NAME + "Attribute '" + attrName
                     + "' has a DataType of '" + attribute.getType().toString()
                     + "' which is not supported.");
                  break;
            }
         }

         entityAttrList.add(entityAttr);

      }
      return entityAttrList;
   }

   //----------------------------------------------------------------
   private RequestBeneficiaryEntityAttribute getEntityAttr(String name, Boolean value)
   //----------------------------------------------------------------
   {
      RequestBeneficiaryEntityAttribute attr = null;

      attr = new RequestBeneficiaryEntityAttribute(name, value,
         RequestBeneficiaryEntityAttribute.TYPE.Boolean);

      return attr;
   }

   //----------------------------------------------------------------
   private RequestBeneficiaryEntityAttribute getEntityAttr(String name, String value)
   //----------------------------------------------------------------
   {
      RequestBeneficiaryEntityAttribute attr = null;

      attr = new RequestBeneficiaryEntityAttribute(name, value,
         RequestBeneficiaryEntityAttribute.TYPE.String);

      return attr;
   }

   //----------------------------------------------------------------
   private RequestBeneficiaryEntityAttribute getEntityAttr(String name, Integer value)
   //----------------------------------------------------------------
   {
      RequestBeneficiaryEntityAttribute attr = null;

      attr = new RequestBeneficiaryEntityAttribute(name, value,
         RequestBeneficiaryEntityAttribute.TYPE.Integer);

      return attr;
   }

   //----------------------------------------------------------------
   private RequestBeneficiaryEntityAttribute getEntityAttr(String name, Long value)
   //----------------------------------------------------------------
   {
      RequestBeneficiaryEntityAttribute attr = null;

      attr = new RequestBeneficiaryEntityAttribute(name, value,
         RequestBeneficiaryEntityAttribute.TYPE.Long);

      return attr;
   }

   //----------------------------------------------------------------
   private RequestBeneficiaryEntityAttribute getEntityAttr(String name, Boolean[] value)
   //----------------------------------------------------------------
   {
      RequestBeneficiaryEntityAttribute attr = null;

      attr = new RequestBeneficiaryEntityAttribute(name, value,
         RequestBeneficiaryEntityAttribute.TYPE.Boolean);

      return attr;
   }

   //----------------------------------------------------------------
   private RequestBeneficiaryEntityAttribute getEntityAttr(String name, String[] value)
   //----------------------------------------------------------------
   {
      RequestBeneficiaryEntityAttribute attr = null;

      attr = new RequestBeneficiaryEntityAttribute(name, value,
         RequestBeneficiaryEntityAttribute.TYPE.String);

      return attr;
   }

   //----------------------------------------------------------------
   private RequestBeneficiaryEntityAttribute getEntityAttr(String name, Integer[] value)
   //----------------------------------------------------------------
   {
      RequestBeneficiaryEntityAttribute attr = null;

      attr = new RequestBeneficiaryEntityAttribute(name, value,
         RequestBeneficiaryEntityAttribute.TYPE.Integer);

      return attr;
   }

   //----------------------------------------------------------------
   private RequestBeneficiaryEntityAttribute getEntityAttr(String name, Long[] value)
   //----------------------------------------------------------------
   {
      RequestBeneficiaryEntityAttribute attr = null;

      attr = new RequestBeneficiaryEntityAttribute(name, value,
         RequestBeneficiaryEntityAttribute.TYPE.Long);

      return attr;
   }
}
