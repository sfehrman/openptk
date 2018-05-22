/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2011 Project OpenPTK
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

import org.openptk.common.AttrIF;
import org.openptk.common.Operation;
import org.openptk.context.ContextIF;
import org.openptk.exception.FunctionException;

/**
 *
 * @author Scott Fehrman, Project OpenPTK
 */
//===================================================================
public class Concat extends Function implements FunctionIF
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
      boolean cancel = false;
      Object obj = null;
      AttrIF attribute = null;
      AttrIF argAttr = null;
      StringBuilder buf = new StringBuilder();
      String argValue = null;

      /*
       * Check for required method arguments
       */

      if (key == null || key.length() < 1)
      {
         this.handleError("Attribute name (key) is null/empty");
      }

      if (args == null || args.size() < 1)
      {
         this.handleError("No arguments were found");
      }

      if ( attributes == null || attributes.size() < 1)
      {
         this.handleError("Map of Attributes is null/empty");
      }

      /*
       * Get the Attribute we are transforming
       */

      attribute = attributes.get(key);
      if (attribute == null)
      {
         this.handleError("Attribute '" + key + "' is null");
      }

      /*
       * process each argument
       */

      for (ArgumentIF arg : args)
      {
         argValue = null;
         if (arg != null)
         {
            switch (arg.getType())
            {
               case ATTRIBUTE:
                  argAttr = attributes.get(arg.getValue());

                  if (argAttr == null)
                  {
                     this.handleError("Attribute '"
                        + arg.getValue() + "' used in argument '"
                        + arg.getName() + "' does not exist");
                  }

                  obj = argAttr.getValue();
                  if (obj != null)
                  {
                     switch (argAttr.getType())
                     {
                        case STRING:
                           argValue = (String) obj;
                           break;
                        case BOOLEAN:
                           argValue = ((Boolean) obj).toString();
                           break;
                        case INTEGER:
                           argValue = ((Integer) obj).toString();
                           break;
                        case LONG:
                           argValue = ((Long) obj).toString();
                           break;
                        case OBJECT:
                           argValue = obj.toString();
                           break;
                     }

                     if (argValue == null || argValue.length() < 1 && arg.isRequired())
                     {
                        /*
                         * The attribute has an empty value and the attribute is
                         * marked a "required". stop/cancel the Function
                         */
                        cancel = true;
                     }
                  }
                  break;
               case LITERAL:
                  argValue = arg.getValue();
                  break;
            }
         }

         if (argValue != null && argValue.length() > 0)
         {
            buf.append(argValue);
         }
      }

      /*
       * Now put the concated value in the AttributeIF, If no errors
       */

      if (!cancel)
      {
         attribute.setValue(buf.toString());
      }

      return;
   }
}
