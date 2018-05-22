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
package org.openptk.definition.functions;

import java.util.List;
import java.util.Map;

import org.openptk.common.AttrIF;
import org.openptk.common.Operation;
import org.openptk.context.ContextIF;
import org.openptk.exception.FunctionException;

//===================================================================
public abstract class Function implements FunctionIF
//===================================================================
{

   //----------------------------------------------------------------
   public Function()
   //----------------------------------------------------------------
   {
      return;
   }


   /**
    * @param context
    * @param key
    * @param mode
    * @param oper
    * @param args
    * @param attrs
    * @throws FunctionException
    */
   //----------------------------------------------------------------
   @Override
   public abstract void execute(ContextIF context, String key, TaskMode mode, Operation oper, List<ArgumentIF> args, Map<String, AttrIF> attrs) throws FunctionException;
   //----------------------------------------------------------------


   /**
    * @param msg
    * @throws FunctionException
    */
   //----------------------------------------------------------------
   protected void handleError(final String msg) throws FunctionException
   //----------------------------------------------------------------
   {
      String str = null;

      if ( msg == null || msg.length() < 1)
      {
         str = "(null message)";
      }
      else
      {
         str = msg;
      }
      throw new FunctionException(str);
   }
}
