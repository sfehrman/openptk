/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2007-2010 Sun Microsystems, Inc.
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
 *      Portions Copyright 2011-2012 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.context;

import org.openptk.api.Query;
import org.openptk.authenticate.AuthenticatorIF;
import org.openptk.common.AssignmentIF;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.config.Configuration;
import org.openptk.context.actions.ActionIF;
import org.openptk.definition.DefinitionIF;
import org.openptk.definition.SubjectIF;
import org.openptk.exception.ProvisionException;
import org.openptk.model.ModelIF;
import org.openptk.model.RelationshipIF;
import org.openptk.spi.ServiceIF;
import org.openptk.spi.operations.OperationsIF;

//===================================================================
public interface ContextIF extends ComponentIF
//===================================================================
{

   public static final String ATTRIBUTE_HAS_AND = "hasAnd";
   public static final String ATTRIBUTE_HAS_OR = "hasOr";
   public static final String ATTRIBUTE_HAS_CONTAINS = "hasContains";
   public static final String ATTRIBUTE_SRCH_ATTRS = "searchAttrs";
   public static final String PROP_ATTRIBUTE_INVALID_REMOVE = "attribute.invalid.remove";

   /**
    * @param resp
    */
   public void logAudit(ResponseIF resp);

   /**
    * @param obj
    * @param callerId
    */
   public void logData(Object obj, String callerId);

   /**
    * @param begin
    * @param end
    * @param msg
    */
   public void logTime(Long begin, Long end, String msg);

   /**
    * @param definition
    */
   public void setDefinition(DefinitionIF definition);

   /**
    * @param query
    */
   public void setQuery(Query query);

   /**
    * @param service
    */
   public void setService(ServiceIF service);

   /**
    * @param audit
    */
   public void setAudit(boolean audit);

   /**
    * @param sort
    */
   public void setSort(boolean sort);

   /**
    * @return
    */
   public boolean isAudit();

   /**
    * @return
    */
   public boolean isSort();

   /**
    * @param obj
    * @param callerId
    * @return
    */
   public String getData(Object obj, String callerId);

   /**
    * @param request
    * @return
    * @throws ProvisionException
    */
   public ResponseIF execute(RequestIF request) throws ProvisionException;

   /**
    * @return
    */
   public DefinitionIF getDefinition();

   /**
    * @return
    */
   public Query getQuery();

   /**
    * @return
    */
   public ServiceIF getService();

   /**
    * @param id
    * @param operation
    */
   public void setOperation(String id, OperationsIF operation);

   /**
    * @param id
    * @return
    */
   public OperationsIF getOperation(String id);

   /**
    * @return
    */
   public String[] getOperationNames();

   /**
    * @param model
    */
   public void setModel(ModelIF model);

   /**
    * @return
    */
   public ModelIF getModel();

   /**
    * @param id
    * @param relationship
    */
   public void setRelationship(String id, RelationshipIF relationship);

   /**
    * @param id
    * @return
    */
   public RelationshipIF getRelationship(String id);

   /**
    * @return
    */
   public String[] getRelationshipNames();

   /**
    * @param id
    * @return
    */
   public boolean hasRelationship(String id);

   /**
    * @param id
    * @return
    */
   public boolean hasOperation(String id);

   /**
    * @return
    */
   public AuthenticatorIF getAuthenticator();

   /**
    * @param authen
    */
   public void setAuthenticator(AuthenticatorIF authen);

   /**
    * @param config
    */
   public void setConfiguration(Configuration config);

   /**
    * @return
    */
   public Configuration getConfiguration();

   /**
    * @return
    * @throws ProvisionException
    */
   public SubjectIF getSubject() throws ProvisionException;

   /**
    * @param operation
    * @param action
    */
   public void addAction(Operation operation, ActionIF action);

   /**
    * @param operation
    * @return
    */
   public ActionIF[] getActions(Operation operation);

   /**
    * Add an Assignment
    *
    * @param assignment
    */
   public void setAssignment(String id, AssignmentIF assignment);
   
   /**
    * 
    * @param id
    * @return AssignmentIF
    */
   public AssignmentIF getAssignment(String id);

   /**
    * Get an array of all the Assignments
    *
    * @return
    */
   public String[] getAssignmentNames();
}
