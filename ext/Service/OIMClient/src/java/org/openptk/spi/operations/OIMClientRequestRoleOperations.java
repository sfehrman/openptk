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
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class OIMClientRequestRoleOperations extends OIMClientRequestOperations
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private static final String PROP_ATTRIBUTE_ROLENAME = "request.attribute.rolename";
   private static final String SEARCH_TEMPLATE_ASSIGN_ROLES = "Assign Roles";
   private static final String SEARCH_TEMPLATE_SELF_ASSIGN_ROLES = "Self Assign Roles";
   private String _attrRoleName = null;

   //----------------------------------------------------------------
   public OIMClientRequestRoleOperations()
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
         _attrRoleName = this.getCheckProp(PROP_ATTRIBUTE_ROLENAME);
      }
      catch (OperationException ex)
      {
         this.setState(State.ERROR);
         this.setStatus(ex.getMessage());
      }

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
      RequestSearchCriteria rsc = null;

      rsc = new RequestSearchCriteria();

      try
      {
         rsc.addExpression(RequestConstants.REQUEST_TEMPLATE_NAME, SEARCH_TEMPLATE_ASSIGN_ROLES, RequestSearchCriteria.Operator.EQUAL);
         rsc.addExpression(RequestConstants.REQUEST_TEMPLATE_NAME, SEARCH_TEMPLATE_SELF_ASSIGN_ROLES, RequestSearchCriteria.Operator.EQUAL);
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
      String roleName = null;
      RequestBeneficiaryEntity entity = null;

      roleName = this.getRoleNameFromAttributes(attributes);

      if (roleName == null || roleName.length() < 1)
      {
         this.handleError(METHOD_NAME + "Role name is null/empty");
      }

      entity = new RequestBeneficiaryEntity();
      entity.setEntityType(_entityType);
      entity.setEntitySubType(roleName);
      entity.setEntityKey(this.getRoleKeyFromRoleName(roleName));

      return entity;
   }

   //----------------------------------------------------------------
   @Override
   protected void updateSubjectFromBeneficiaryEntities(RequestIF request, ComponentIF subject, List<RequestBeneficiaryEntity> entities) throws OperationException
   //----------------------------------------------------------------
   {
      String roleKey = null;
      String roleName = null;
      List<String> roles = null;
      List<RequestBeneficiaryEntityAttribute> entityAttrList = null;

      if (entities != null && !entities.isEmpty())
      {
         roles = new ArrayList<String>();

         for (RequestBeneficiaryEntity entity : entities)
         {
            if (entity != null)
            {
               roleKey = entity.getEntityKey();
               if (roleKey != null && roleKey.length() > 0)
               {
                  roleName = this.getRoleNameFromRoleKey(roleKey);
                  if (roleName != null && roleName.length() > 0)
                  {
                     roles.add(roleName);
                  }
               }
               entityAttrList = entity.getEntityData();
               if (entityAttrList != null && !entityAttrList.isEmpty())
               {
                  this.updateSubjectFromEntityAttributes(request, subject, entityAttrList);
               }
            }
         }

         if (roles.size() == 1)
         {
            this.addAttrToSubject(subject, _attrRoleName, roles.get(0));
         }
         else if (roles.size() > 1)
         {
            this.addAttrToSubject(subject, _attrRoleName, roles.toArray(new String[roles.size()]));
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
   private String getRoleNameFromAttributes(Map<String, AttrIF> attributes) throws OperationException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":" + Thread.currentThread().getStackTrace()[1].getMethodName() + ": ";
      String attrValueStr = null;
      AttrIF attribute = null;

      attribute = attributes.get(_attrRoleName);
      if (attribute == null)
      {
         this.handleError(METHOD_NAME + "Required attribute '"
            + _attrRoleName + "' is null");
      }

      attrValueStr = attribute.getValueAsString();
      if (attrValueStr == null || attrValueStr.length() < 1)
      {
         this.handleError(METHOD_NAME + "Required attribute '"
            + _attrRoleName + "' has no value");
      }

      attributes.remove(_attrRoleName); // take it out of the Map

      return attrValueStr;
   }
}
