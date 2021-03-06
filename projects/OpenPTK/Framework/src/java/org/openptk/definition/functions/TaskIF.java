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
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.definition.functions;

import java.util.List;
import java.util.Map;

import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;

//===================================================================
public interface TaskIF extends ComponentIF
//===================================================================
{

   /**
    * @return
    */
   public TaskIF copy();

   /**
    * @param argument
    */
   public void addArgument(ArgumentIF argument);

   /**
    * @return
    */
   public List<ArgumentIF> getArguments();

   /**
    * @param classname
    */
   public void setFunctionClassname(String classname);

   /**
    * @return
    */
   public String getFunctionClassname();

   /**
    * @param oper
    * @param mode
    */
   public void setOperationMode(Operation oper, TaskMode mode);

   /**
    * @param oper
    * @return
    */
   public boolean hasOperation(Operation oper);

   /**
    * @param oper
    * @return
    */
   public boolean hasOperation(String oper);

   /**
    * @param oper
    * @return
    */
   public TaskMode getOperationMode(Operation oper);

   /**
    * @return
    */
   public Map<Operation, TaskMode> getOperationsMode();

   /**
    * @param bool
    */
   public void setUseExisting(boolean bool);

   /**
    * @return
    */
   public boolean useExisting();
}
