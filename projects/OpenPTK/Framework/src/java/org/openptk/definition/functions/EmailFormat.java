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

import org.openptk.common.AttrIF;
import org.openptk.common.Operation;
import org.openptk.context.ContextIF;
import org.openptk.exception.FunctionException;

//===================================================================
public class EmailFormat extends Function implements FunctionIF
//===================================================================
{
   /*
    * The "email" attribute to make sure it follows this pattern:
    * <string>"@"<string>
    */

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
      Object obj = null;
      int iLen = 0;
      int iIndex = 0;
      String str = null;
      AttrIF attribute = null;

      /*
       * Get the Attribute we are transforming
       * The "key" is the Attribute "id"
       */

      if (key != null)
      {
         attribute = attributes.get(key);
      }
      else
      {
         this.handleError("Attributes name (key) is null");
      }

      if (attribute != null)
      {

         /*
          * No arguments are used
          */

         obj = attribute.getValue();

         switch (attribute.getType())
         {
            case STRING:
               {
                  str = (String) obj;
                  if (str != null && str.length() > 2)
                  {
                     iLen = str.length();
                     iIndex = str.indexOf('@');
                     /*
                      * Index = -1 : it was not found
                      * Index =  0 : it's the first character, missing prefix
                      * Index = Len-1 : it's the last character, missing postfix
                      */
                     if (iIndex == -1 || iIndex == 0 || iIndex == (iLen - 1))
                     {
                        this.handleError("Attribute '" + key + "' is not formatted properly");
                     }
                  }
                  else
                  {
                     this.handleError("Attribute '" + key + "' has a null value");
                  }
               }
               break;
            default:
               break;
         }
      }
      else
      {
         this.handleError("Attribute '" + key + "' is null");
      }

      return;
   }
}
