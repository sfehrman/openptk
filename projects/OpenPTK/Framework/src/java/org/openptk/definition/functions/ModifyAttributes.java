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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openptk.common.AttrIF;
import org.openptk.common.BasicAttr;
import org.openptk.common.Operation;
import org.openptk.context.ContextIF;
import org.openptk.exception.FunctionException;

//===================================================================
public class ModifyAttributes extends Function implements FunctionIF
//===================================================================
{

   private static final String NAME_REMOVE = "remove";
   private static final String NAME_INCLUDE = "include";
   

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
      ArgumentIF argument = null;
      Iterator<ArgumentIF> iter = null;
      String argumentName = null;
      String argumentValue = null;

      // Get the Attribute we are transforming

      if (key != null)
      {
         attribute = attributes.get(key);
      }
      else
      {
         this.handleError("Attributes name (key) is null");
      }

      /*
       * Get the execute object and the
       * associated arguments for this transformation
       */

      if (attribute != null)
      {
         if (args != null)
         {
            iter = args.iterator();

            while (iter.hasNext())
            {
               argument = iter.next();

               /*
                * We only care about the LITERAL which has the name of the
                * Attribute to either REMOVE or INCLUDE
                */

               if (argument.getType() == ArgumentType.LITERAL)
               {
                  argumentName = argument.getName();
                  argumentValue = argument.getValue();

                  if (argumentValue != null)
                  {
                     if (argumentName.equalsIgnoreCase(ModifyAttributes.NAME_REMOVE))
                     {
                        this.remove(argumentValue, attributes);
                     }
                     else if (argumentName.equalsIgnoreCase(ModifyAttributes.NAME_INCLUDE))
                     {
                        this.include(argumentValue, attributes);
                     }
                  }
               }
            }
         }
      }
      return;
   }
   //----------------------------------------------------------------
   private void remove(String name, Map<String, AttrIF> attrMap)
   //----------------------------------------------------------------
   {

      if (attrMap.containsKey(name))
      {
         attrMap.remove(name);
      }
      return;
   }
   //----------------------------------------------------------------
   private void include(String name, Map<String, AttrIF> attrMap)
   //----------------------------------------------------------------
   {
      AttrIF attr = null;

      attr = new BasicAttr(name);

      attrMap.put(name, attr);

      return;
   }
}
