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
package org.openptk.common;

import java.util.Map;

import org.openptk.api.AttributeIF;
import org.openptk.definition.functions.TaskIF;

//===================================================================
public interface AttrIF extends AttributeIF
//===================================================================
{
   /**
    * Make a copy.
    * @return
    */
   @Override
   public AttrIF copy();

   /**
    * @return
    */
   public AttrCategory getCategory();

   /**
    * @param category
    */
   public void setCategory(AttrCategory category);

   /**
    * Set the Attr's service name.
    * @param name String the service name
    */
   public void setServiceName(String name);

   /**
    * Get the Attr's framework name.
    * @return String the Attr's service name
    */
   public String getFrameworkName();

   /**
    * Get the Attr's service name.
    * @return String the Attr's service name
    */
   public String getServiceName();

   /**
    * @param oper
    * @param task
    */
   public void addTask(Operation oper, TaskIF task);

   /**
    * @param oper
    * @param tasks
    */
   public void setTasks(Operation oper, Map<String, TaskIF> tasks);

   /**
    * @param oper
    * @return
    */
   public Map<String, TaskIF> getTasks(Operation oper);

   /**
    * @return
    */
   public Map<String, TaskIF> getTasks();
}
