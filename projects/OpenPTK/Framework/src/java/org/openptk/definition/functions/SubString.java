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

import org.openptk.api.DataType;
import org.openptk.common.AttrIF;
import org.openptk.common.Operation;
import org.openptk.context.ContextIF;
import org.openptk.exception.FunctionException;

//===================================================================
public class SubString extends Function implements FunctionIF
//===================================================================
{

   private static final String LITERAL_ARG_AFTER = "after";
   private static final String LITERAL_ARG_BEFORE = "before";

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
      boolean hasAfter = false;
      boolean hasBefore = false;
      Object attrValue = null;
      AttrIF attribute = null;
      ArgumentIF arg = null;
      Iterator<ArgumentIF> iter = null;
      String argName = null;
      String literalAfter = null;
      String literalBefore = null;

      // Get the Attribute we are transforming

      if (key != null)
      {
         attribute = attributes.get(key);
      }
      else
      {
         this.handleError("Attributes name (key) is null");
      }

      if (attribute != null && attribute.getType() == DataType.STRING)
      {
         attrValue = attribute.getValue();
         if (attrValue != null)
         {
            if (args == null)
            {
               this.handleError("No arguments were found");
            }

            iter = args.iterator();

            /*
             * get the "after" and "before" arguments to the Function
             */


            while (iter.hasNext())
            {
               arg = iter.next();

               argName = arg.getName();

               if (argName != null && argName.length() > 0)
               {
                  if (argName.equalsIgnoreCase(SubString.LITERAL_ARG_AFTER))
                  {
                     literalAfter = arg.getValue();
                     if (literalAfter != null && literalAfter.length() > 0)
                     {
                        hasAfter = true;
                     }
                  }
                  else if (argName.equalsIgnoreCase(SubString.LITERAL_ARG_BEFORE))
                  {
                     literalBefore = arg.getValue();
                     if (literalBefore != null && literalBefore.length() > 0)
                     {
                        hasBefore = true;
                     }
                  }
               }
            }

            if (!hasAfter)
            {
               this.handleError("The argument '" +
                  SubString.LITERAL_ARG_AFTER + "' is not set.");
            }

            if (!hasBefore)
            {
               this.handleError("The argument '" +
                  SubString.LITERAL_ARG_BEFORE + "' is not set.");
            }

            if (!attribute.isMultivalued())
            {
               attribute.setValue(this.getsubstring(literalAfter, literalBefore, (String) attrValue));
            }
         }
      }
      else
      {
         this.handleError("Attribute '" + key + "' is null");
      }

      return;
   }

   //----------------------------------------------------------------
   private String getsubstring(String after, String before, String string)
   //----------------------------------------------------------------
   {
      int begin = 0;
      int end = 0;
      String ret = null;

      /*         0         1         2         3         4
       *         01234567890123456789012345678901234567890
       *  value: uid=juser,ou=People,dc=openptk,dc=org
       *  after: uid=
       * before: ,
       */

      begin = string.indexOf(after);

      if (begin >= 0)
      {
         ret = string.substring(begin + after.length());

         /*         0         1         2         3         4
          *         01234567890123456789012345678901234567890
          *  value: juser,ou=People,dc=openptk,dc=org
          * before: ,
          */

         end = ret.indexOf(before);
         if (end > 0)
         {
            ret = ret.substring(0, end);
         }
      }
      else
      {
         ret = string;
      }
      return ret;
   }
}
