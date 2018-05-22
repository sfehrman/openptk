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
package org.openptk.definition.functions;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openptk.common.AttrIF;
import org.openptk.common.Operation;
import org.openptk.context.ContextIF;
import org.openptk.exception.FunctionException;

//===================================================================
public class PrefixFirstInitialLastname extends Function implements FunctionIF
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
      String lastname = null;
      String firstname = null;
      String prefix = null;
      String result = null;
      Iterator<ArgumentIF> iter = null;
      AttrIF attribute = null; //       OpenPTK
      AttrIF attributeSource = null; // OpenPTK
      ArgumentIF arg = null; //         OpenPTK

      //Get the Attribute we are transforming

      if (key != null)
      {
         attribute = attributes.get(key);
      }
      else
      {
         this.handleError("Key is Null");
      }

      //Get the execute object and associated arguments for this transformation

      if (attribute != null)
      {
         if (args == null)
         {
            this.handleError("No function arguments");
         }

         iter = args.iterator();

         while (iter.hasNext())
         {
            arg = iter.next();

            if (arg.getType() == ArgumentType.ATTRIBUTE)
            {
               attributeSource = attributes.get(arg.getValue());

               if (attributeSource == null)
               {
                  this.handleError("Attribute '" + arg.getValue() +
                     "' used in argument '" + arg.getName() + "' does not exist");
               }

               if (arg.getName().equalsIgnoreCase("first"))
               {
                  firstname = (String) attributeSource.getValue();
               }
               if (arg.getName().equalsIgnoreCase("last"))
               {
                  lastname = (String) attributeSource.getValue();
               }
            }
            else
            {
               if (arg.getName().equalsIgnoreCase("prefix"))
               {
                  prefix = arg.getValue();
               }
            }
         }

         /*
          * check to make sure we have everything
          */

         if (prefix == null)
         {
            this.handleError("Argument 'prefix' not found for Attribute '" + key + "'");
         }

         if (firstname == null)
         {
            this.handleError("Argument 'first' not found for Attribute '" + key + "'");
         }

         if (lastname == null)
         {
            this.handleError("Argument 'last' not found for Attribute '" + key + "'");
         }

         //Now get the firstinitial + lastname

         result = prefix + firstname.substring(0, 1) + lastname;

         //set value in the AttributeIF

         attribute.setValue(result.toLowerCase());
      }
      else
      {
         this.handleError("Attribute '" + key + "' is null");
      }
      return;
   }
}