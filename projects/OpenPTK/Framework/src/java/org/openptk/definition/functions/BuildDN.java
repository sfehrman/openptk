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
/**
 * @author Scott Fehrman, Project OpenPTK
 */
package org.openptk.definition.functions;

import java.util.List;
import java.util.Map;

import org.openptk.common.AttrIF;
import org.openptk.common.Operation;
import org.openptk.context.ContextIF;
import org.openptk.exception.FunctionException;

//===================================================================
public class BuildDN extends Function implements FunctionIF
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
      boolean clear = false;
      String argValue = null;
      StringBuilder buf = new StringBuilder();
      AttrIF attribute = null;
      AttrIF attributeSource = null;

      // Get the Attribute we are transforming

      if (key == null)
      {
         this.handleError("Attributes name (key) is null");
      }

      attribute = attributes.get(key);

      if (attribute == null)
      {
         this.handleError("Attribute '" + key + "' is null");
      }

      if (args == null)
      {
         this.handleError("No arguments were found");
      }

      for (ArgumentIF arg : args)
      {
         argValue = null;
         if (arg != null)
         {
            if (arg.getType() == ArgumentType.ATTRIBUTE)
            {
               attributeSource = attributes.get(arg.getValue());

               if (attributeSource == null)
               {
                  this.handleError("Attribute '"
                     + arg.getValue() + "' used in argument '"
                     + arg.getName() + "' does not exist");
               }

               argValue = (String) attributeSource.getValue();

               if (argValue == null || argValue.length() < 1 && arg.isRequired())
               {
                  /*
                   * The attribute has an empty value and the attribute is
                   * marked a "required". Clear the entire value
                   */
                  clear = true;
               }
            }
            else
            {
               argValue = arg.getValue();
            }
         }

         if (argValue != null)
         {
            buf.append(argValue);
         }
      }

      /*
       * If the "clear" flag is true, then set the attribute value to ""
       */

      if (clear)
      {
         attribute.setValue("");
      }
      else
      {
         attribute.setValue(buf.toString());
      }

      return;
   }
}
