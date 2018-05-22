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

import org.openptk.api.DataType;
import org.openptk.common.AttrIF;
import org.openptk.common.Operation;
import org.openptk.context.ContextIF;
import org.openptk.exception.FunctionException;
import org.openptk.util.Digest;
import org.openptk.util.StringUtil;

/**
 *
 * @author Scott Fehrman, Project OpenPTK
 */
//===================================================================
public class ForgottenPasswordSecure extends ForgottenPassword
//===================================================================
{
   private String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * @param context
    * @param key
    * @param mode
    * @param oper
    * @param arguments
    * @param attributes
    * @throws FunctionException
    */
   //----------------------------------------------------------------
   @Override
   public synchronized void execute(ContextIF context, String key, TaskMode mode,
      Operation oper, List<ArgumentIF> arguments, Map<String, AttrIF> attributes)
      throws FunctionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      AttrIF attribute = null;

      /*
       * Check for required arguments
       */

      if (key == null || key.length() < 1)
      {
         this.handleError(METHOD_NAME + "Key is Null");
      }

      if (arguments == null || arguments.size() < 1)
      {
         this.handleError(METHOD_NAME + "Missing Function arguments");
      }

      /*
       * Get the Attribute we are transforming ...
       * key = name of the attribute that is being processed
       */

      attribute = attributes.get(key);

      /*
       * Get the execute object and associated arguments for this function
       */

      if (attribute != null)
      {
         switch (oper)
         {
            case READ:
            {
               /*
                * Decode the string (of questions and answers) into
                * multi-values attributes
                * Hash the attribute value(s)
                */

               if (mode == TaskMode.TOFRAMEWORK)
               {
                  this.decodeData(key, attributes, arguments);
                  this.convertToHash(attribute);
               }
               break;
            }
            case CREATE:
            case UPDATE:
            {
               /*
                * Encode the multi-valued attributes (answers and questions) into
                * a character encoded string
                */

               this.convertToHash(attribute);
               this.encodeData(key, attributes, arguments);
               break;
            }
            default:
            {
               this.handleError(METHOD_NAME + "Unsupported Operation code='" + oper.toString() + "'");
            }
         }
      }
      return;
   }

   //----------------------------------------------------------------
   private void convertToHash(final AttrIF attr) throws FunctionException
   //----------------------------------------------------------------
   {
      Object value = null;
      String before = null;
      String after = null;
      String[] strArray = null;

      /*
       * For each value (if multi-valued) in the attribute ...
       * Check to see if it's already a "hashed" value.
       *
       * If it's already a hashed value ... do nothing
       * Else, generate the hashed value and replace the clear-text value.
       *
       * Before generating a hash, "clean" the String (spaces, punctuation, etc.)
       * then convert to lowercase
       */

      value = attr.getValue();
      if (value != null)
      {
         if (attr.getType() == DataType.STRING)
         {
            if (attr.isMultivalued())
            {
               strArray = (String[]) value;
               if (strArray != null)
               {
                  for (int i = 0; i < strArray.length; i++)
                  {
                     before = strArray[i];
                     if (before != null && before.length() > 0)
                     {
                        if (!Digest.isHashed(before))
                        {
                           after = Digest.generate(StringUtil.clean(StringUtil.ALPHA_NUM, before).toLowerCase());
                           strArray[i] = after;
                        }
                     }
                  }
                  attr.setValue(strArray);
               }
            }
            else
            {
               before = (String) value;
               if (before != null && before.length() > 0)
               {
                  if (!Digest.isHashed(before))
                  {
                     after = Digest.generate(before.toLowerCase());
                     attr.setValue(after);
                  }
               }
            }
            attr.setEncrypted(true);
         }
      }

      return;
   }
}
