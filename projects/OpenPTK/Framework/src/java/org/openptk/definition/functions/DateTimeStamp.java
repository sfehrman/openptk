/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2009-2010 Sun Microsystems, Inc.
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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openptk.common.AttrIF;
import org.openptk.common.Operation;
import org.openptk.context.ContextIF;
import org.openptk.exception.FunctionException;

//===================================================================
public class DateTimeStamp extends Function implements FunctionIF
//===================================================================
{


   /**
    * @param context
    * @param key
    * @param mode
    * @param oper
    * @param args
    * @param attributes
    * @throws FunctionException
    */
   //----------------------------------------------------------------
   @Override
   public synchronized void execute(ContextIF context, String key, TaskMode mode,
      Operation oper, List<ArgumentIF> args, Map<String, AttrIF> attributes)
      throws FunctionException
   //----------------------------------------------------------------
   {
      AttrIF attribute = null;
      Date date = null;

      //Get the Attribute we are transforming

      if (key == null)
      {
         this.handleError("Key is Null");
      }

      attribute = attributes.get(key);

      if (attribute == null)
      {
         this.handleError("Attribute '" + key + "' is null");
      }

      // Get the current Date/Time stamp

      date = new Date(new Long(System.currentTimeMillis()));
      if (date == null)
      {
         this.handleError("Could not get current Time");
      }

      switch (attribute.getType())
      {
         case STRING:
            attribute.setValue(date.toString());
            break;
         default:
            this.handleError("Can not set Date for type: " + attribute.getType().toString());
      }

      return;
   }
}
