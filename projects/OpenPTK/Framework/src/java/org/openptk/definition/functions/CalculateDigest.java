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
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.definition.functions;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openptk.common.AttrIF;
import org.openptk.common.Operation;
import org.openptk.context.ContextIF;
import org.openptk.exception.FunctionException;
import org.openptk.util.Digest;

//===================================================================
public class CalculateDigest extends Function implements FunctionIF
//===================================================================
{

   private static final String ARG_NAME = "data";


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
      byte[] bytes = null;
      Object objValue = null;
      String attrName = null;
      String strValue = null;
      String digestValue = null;
      Iterator<ArgumentIF> iter = null;
      AttrIF attrKey = null;
      AttrIF attrData = null;
      ArgumentIF arg = null;

      /*
       * Get the Attribute name (key) we are transforming
       */

      if (key == null)
      {
         this.handleError("Key is Null");
      }

      attrKey = attributes.get(key);

      if (attrKey == null)
      {
         this.handleError("Attribute '" + key + "' is null");
      }

      /*
       * We need one argument called "data"
       */

      if (args == null || args.isEmpty())
      {
         this.handleError("Arguments are null/empty, argument named '" + ARG_NAME +
            "' is required.");
      }

      iter = args.iterator();
      while (iter.hasNext())
      {
         arg = iter.next();
         if (arg.getName().equalsIgnoreCase(ARG_NAME))
         {
            if (arg.getType() != ArgumentType.ATTRIBUTE)
            {
               this.handleError("Argument '" + ARG_NAME + "' must be of Type: " +
                  ArgumentType.ATTRIBUTE.toString());
            }

            attrName = arg.getValue();
            if (attrName == null || attrName.length() < 1)
            {
               this.handleError("Argument value for '" + ARG_NAME + "' is null");
            }
         }
      }

      if (attrName == null || attrName.length() < 1)
      {
         this.handleError("Required argument '" + ARG_NAME +
            "' was not found.");
      }

      attrData = attributes.get(attrName);
      if (attrData == null)
      {
         this.handleError("Attribute '" + ARG_NAME + "' is null");
      }

      switch (attrData.getType())
      {
         case STRING:
            strValue = (String) attrData.getValue();
            digestValue = Digest.generate(strValue);
            break;
         case OBJECT:
            objValue = attrData.getValue();
            if ( objValue instanceof byte[])
            {
               bytes = (byte[])objValue;
               digestValue = Digest.generate(bytes);
            }
            break;
      }

      if ( digestValue != null && digestValue.length() > 0)
      {
         attrKey.setValue(digestValue);
      }
      
      return;
   }
}
