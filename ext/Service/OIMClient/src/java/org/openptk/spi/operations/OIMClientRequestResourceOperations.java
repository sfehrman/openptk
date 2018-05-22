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

import Thor.API.Exceptions.tcAPIException;
import Thor.API.Exceptions.tcColumnNotFoundException;
import Thor.API.Operations.tcObjectOperationsIntf;
import Thor.API.tcResultSet;

import oracle.iam.platform.entitymgr.vo.SearchCriteria;
import oracle.iam.request.exception.RequestServiceException;
import oracle.iam.request.vo.RequestBeneficiaryEntity;
import oracle.iam.request.vo.RequestBeneficiaryEntityAttribute;
import oracle.iam.request.vo.RequestConstants;
import oracle.iam.request.vo.RequestSearchCriteria;

import org.openptk.api.State;
import org.openptk.common.AttrIF;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.exception.OperationException;

/**
 *
 * @author Scott Fehrman, Oracle America, Inc.
 * created: 2011-05-24: 
 */
//===================================================================
public class OIMClientRequestResourceOperations extends OIMClientRequestOperations
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String PROP_ATTRIBUTE_RESOURCENAME = "request.attribute.resourcename";
   private static final String RESOURCE_ATTR_NAME = "Objects.Name"; // Attribute Name for Name
   private static final String RESOURCE_ATTR_KEY = "Objects.Key";   // Attribute Name for Key
   private static final String SEARCH_TEMPLATE_PROVISION_RESOURCE = "Provision Resource";
   private String _attrResourceName = null;
   private tcObjectOperationsIntf _resourceService = null; // Thor API

   //----------------------------------------------------------------
   public OIMClientRequestResourceOperations()
   //----------------------------------------------------------------
   {
      super();

      this.setDescription(OIMClientRequestOperations.DESCRIPTION);
      OIMClientRequestOperations.RESPONSE_DESC = CLASS_NAME + ", Response";

      /*
       * Specify which operations are enabled (by default)
       * Can be changed via configuration or at run-time
       */

      this.setEnabled(Operation.CREATE, true);
      this.setEnabled(Operation.READ, true);
      this.setEnabled(Operation.DELETE, true);
      this.setEnabled(Operation.SEARCH, true);

      return;
   }

   //----------------------------------------------------------------
   @Override
   public void startup()
   //----------------------------------------------------------------
   {
      super.startup();

      try
      {
         _attrResourceName = this.getCheckProp(PROP_ATTRIBUTE_RESOURCENAME);
      }
      catch (OperationException ex)
      {
         this.setState(State.ERROR);
         this.setStatus(ex.getMessage());
      }

      _resourceService = _oimClientProxy.getService(tcObjectOperationsIntf.class);

      return;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */
   /**
    * @param request
    * @param objMap
    * @throws OperationException
    */
   //----------------------------------------------------------------
   @Override
   protected RequestSearchCriteria preSearch(final RequestIF request, final Set<String> attrNames) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      RequestSearchCriteria rsc = null; // OIMClient

      rsc = new RequestSearchCriteria();

      try
      {
         rsc.addExpression(RequestConstants.REQUEST_TEMPLATE_NAME, SEARCH_TEMPLATE_PROVISION_RESOURCE, RequestSearchCriteria.Operator.EQUAL);
      }
      catch (RequestServiceException ex)
      {
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      return rsc;
   }

   //----------------------------------------------------------------
   @Override
   protected RequestBeneficiaryEntity getBeneficiaryEntity(Map<String, AttrIF> attributes) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String resourceName = null;
      RequestBeneficiaryEntity entity = null;  // OIMClient

      resourceName = this.getResourceNameFromAttributes(attributes);

      if (resourceName == null || resourceName.length() < 1)
      {
         this.handleError(METHOD_NAME + "Resouce name is null/empty");
      }

      entity = new RequestBeneficiaryEntity();
      entity.setEntityType(_entityType);
      entity.setEntitySubType(resourceName);
      entity.setEntityKey(this.getResourceKeyFromResourceName(resourceName));

      return entity;
   }

   //----------------------------------------------------------------
   @Override
   protected void updateSubjectFromBeneficiaryEntities(RequestIF request, ComponentIF subject, List<RequestBeneficiaryEntity> entities) throws OperationException
   //----------------------------------------------------------------
   {
      String resourceKey = null;
      String resourceName = null;
      String attrFwName = null;
      List<String> resources = null;
      List<RequestBeneficiaryEntityAttribute> entityAttrList = null; // OIMClient

      if (entities != null && !entities.isEmpty())
      {
         resources = new ArrayList<String>();

         for (RequestBeneficiaryEntity entity : entities)
         {
            if (entity != null)
            {
               resourceKey = entity.getEntityKey();
               if (resourceKey != null && resourceKey.length() > 0)
               {
                  resourceName = this.getResourceNameFromResourceKey(resourceKey);
                  if (resourceName != null && resourceName.length() > 0)
                  {
                     resources.add(resourceName);
                  }
               }

               entityAttrList = entity.getEntityData();
               if (entityAttrList != null && !entityAttrList.isEmpty())
               {
                  this.updateSubjectFromEntityAttributes(request, subject, entityAttrList);
               }
            }
         }

         attrFwName = request.getService().getFwName(request.getOperation(), _attrResourceName);

         if (resources.size() == 1)
         {
            this.addAttrToSubject(subject, attrFwName, resources.get(0));
         }
         else if (resources.size() > 1)
         {
            this.addAttrToSubject(subject, attrFwName, resources.toArray(new String[resources.size()]));
         }
      }

      return;
   }

   /*
    * ===============
    * PRIVATE METHODS
    * ===============
    */
   //----------------------------------------------------------------
   private String getResourceNameFromAttributes(Map<String, AttrIF> attributes) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String attrValueStr = null;
      AttrIF attribute = null;

      attribute = attributes.get(_attrResourceName);
      if (attribute == null)
      {
         this.handleError(METHOD_NAME + "Required attribute '"
            + _attrResourceName + "' is null");
      }

      attrValueStr = attribute.getValueAsString();
      if (attrValueStr == null || attrValueStr.length() < 1)
      {
         this.handleError(METHOD_NAME + "Required attribute '"
            + _attrResourceName + "' has no value");
      }

      attributes.remove(_attrResourceName); // take it out of the Map

      return attrValueStr;
   }

   //----------------------------------------------------------------
   private String getResourceKeyFromResourceName(String resourceName) throws OperationException
   //----------------------------------------------------------------
   {
      Long resKey = 0L;
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String resourceKey = null;
      String msg = null;
      StringBuilder err = new StringBuilder();
      Map<String, String> searchMap = null;
      tcResultSet resultSet = null;         // Thor
      SearchCriteria criteria = null;       // OIMClient

      /*
       * Use the "resourceName" to obtain the internal "resourceKey"
       */

      if (resourceName == null || resourceName.length() < 1)
      {
         this.handleError(METHOD_NAME + "Resource name is empty/null");
      }

      searchMap = new HashMap<String, String>();
      searchMap.put(RESOURCE_ATTR_NAME, resourceName);

      try
      {
         resultSet = _resourceService.findObjects(searchMap);
         resKey = resultSet.getLongValue(RESOURCE_ATTR_KEY);
      }
      catch (tcAPIException ex)
      {
         err.append(METHOD_NAME).append(ex.getMessage());
         this.checkException(ex, err);
         this.handleError(err.toString());
      }
      catch (tcColumnNotFoundException ex)
      {
         msg = ex.getMessage();
      }

      if (msg != null)
      {
         this.handleError(METHOD_NAME + msg);
      }

      resourceKey = Long.toString(resKey);

      return resourceKey;
   }

   //----------------------------------------------------------------
   private String getResourceNameFromResourceKey(String resourceKey) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String resourceName = null;
      StringBuilder err = new StringBuilder();
      Map<String, String> searchMap = null;
      tcResultSet resultSet = null;         // Thor

      /*
       * Use the internal "resourceKey" to obtain the "resourceName"
       */

      if (resourceKey != null && resourceKey.length() > 0)
      {
         searchMap = new HashMap<String, String>();
         searchMap.put(RESOURCE_ATTR_KEY, resourceKey);

         try
         {
            resultSet = _resourceService.findObjects(searchMap);
         }
         catch (tcAPIException ex)
         {
            err.append(METHOD_NAME).append(ex.getMessage());
            this.checkException(ex, err);
            this.handleError(err.toString());
         }

         if (resultSet != null)
         {
            try
            {
               resourceName = resultSet.getStringValue(RESOURCE_ATTR_NAME);
            }
            catch (tcAPIException ex)
            {
               resourceName = ex.getMessage();
            }
            catch (tcColumnNotFoundException ex)
            {
               resourceName = ex.getMessage();
            }
         }
      }

      if (resourceName == null)
      {
         resourceName = "(Name not found for '" + (resourceKey != null ? resourceKey : "null") + "')";
      }

      return resourceName;
   }
}
