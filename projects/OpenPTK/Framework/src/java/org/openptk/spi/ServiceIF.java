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
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.spi;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openptk.api.Query;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.context.ContextIF;
import org.openptk.exception.ServiceException;
import org.openptk.spi.operations.OperationsIF;

//===================================================================
public interface ServiceIF extends ComponentIF
//===================================================================
{

   public static final String PROP_URL = "url";

   public void shutdown();

   public void startup();

   /**
    * @param request
    * @return ResponseIF
    * @throws ServiceException
    */
   public ResponseIF execute(RequestIF request) throws ServiceException;

   /**
    * @param context
    */
   public void setContext(ContextIF context);

   /**
    * @return ContextIF
    */
   public ContextIF getContext();

   /**
    * @param operation
    * @param msec
    */
   public void setTimeout(Operation operation, int msec);

   /**
    * @param operation
    * @return int
    */
   public int getTimeout(Operation operation);

   /**
    * @param oper
    * @param operation
    */
   public void setOperation(Operation oper, OperationsIF operation);

   /**
    * @param operation
    * @return OperationsIF
    */
   public OperationsIF getOperation(Operation operation);

   /**
    * @return Map<Operation, OperationsIF>
    */
   public Map<Operation, OperationsIF> getOperations();

   /**
    * @param operation
    * @return boolean
    */
   public boolean hasOperation(Operation operation);

   /**
    * @param opstr
    * @return boolean
    */
   public boolean hasOperation(String opstr);

   /**
    * @param operation
    * @param comp
    */
   public void setAttrGroup(Operation operation, ComponentIF comp);

   /**
    * @param operation
    * @return ComponentIF
    */
   public ComponentIF getAttrGroup(Operation operation);

   /**
    * @param operation
    * @param comp
    */
   public void setAssociation(Operation operation, ComponentIF comp);

   /**
    * @param operation
    * @return ComponentIF
    */
   public ComponentIF getAssociation(Operation operation);

   /**
    * @param operation
    * @param comp
    */
   public void setOperAttr(Operation operation, ComponentIF comp);

   /**
    * @param operation
    * @return ComponentIF
    */
   public ComponentIF getOperAttr(Operation operation);

   /**
    * @param operation
    * @param map
    */
   public void setFw2SrvcNames(Operation operation, Map<String, String> map);

   /**
    * @param operation
    * @param map
    */
   public void setSrvc2FwNames(Operation operation, Map<String, String> map);

   /**
    * @param operation
    * @return Map<String, String>
    */
   public Map<String, String> getFw2SrvcNames(Operation operation);

   /**
    * @param operation
    * @return Map<String, String>
    */
   public Map<String, String> getSrvc2FwNames(Operation operation);

   /**
    * @param operation
    * @param name
    * @return String
    */
   public String getSrvcName(Operation operation, String name);

   /**
    * @param operation
    * @param name
    * @return String
    */
   public String getFwName(Operation operation, String name);

   /**
    * @param operation
    * @param fw
    * @return String[]
    */
   public String[] toSrvc(Operation operation, String[] fw);

   /**
    * @param operation
    * @param fw
    * @return String[]
    */
   public String[] toFw(Operation operation, String[] fw);

   /**
    * @param operation
    * @param query
    */
   public void setQuery(Operation operation, Query query);

   /**
    * @param operation
    * @return Query
    */
   public Query getQuery(Operation operation);

   /**
    * @param sortBy
    */
   public void setSortAttributes(List<String> sortBy);

   /**
    * @return List<String>
    */
   public List<String> getSortAttributes();

   /**
    * @param operation
    * @param key
    */
   public void setKey(Operation operation, String key);

   /**
    * @param operation
    * @return String
    */
   public String getKey(Operation operation);

   /**
    * @param operation
    * @param props
    */
   public void setOperProps(Operation operation, Properties props);

   /**
    * @param operation
    * @return Properties
    */
   public Properties getOperProps(Operation operation);
}
