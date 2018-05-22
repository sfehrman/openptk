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
package org.openptk.taglib;

import org.openptk.api.AttributeIF;
import org.openptk.api.Input;

//===================================================================
public class addValueTag extends AbstractTag
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String _attributeName = null;
   private String _valName = null;
   private String _inputName = null;


   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setAttribute(final String arg)
   //----------------------------------------------------------------
   {
      _attributeName = arg;
      return;
   }


   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setValue(final String arg)
   //----------------------------------------------------------------
   {
      _valName = arg;
      return;
   }


   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setInput(final String arg)
   //----------------------------------------------------------------
   {
      _inputName = arg;
      return;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */

   /**
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected void process() throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":process(): ";
      String[] attrVals = null;
      String[] arrTmp = null;
      AttributeIF attr = null;
      Input input = null;
      int arraysize = 0;

      input = this.getInput(_inputName);

      attr = input.getAttribute(_attributeName);
      if (attr == null)
      {
         throw new Exception(METHOD_NAME + "Attribute '" + _attributeName + "' is null");
      }

      if (attr.getValue() instanceof String)
      {
         //convert this to a string array
         attrVals = new String[]
            {
               (String) attr.getValue(), _valName
            };
         attr.setValue(attrVals);
         input.addAttribute(_attributeName, attrVals);
      }
      else if (attr.getValue() instanceof String[])
      {
         //convert this to a larger string array
         arrTmp = (String[]) attr.getValue();
         arraysize = arrTmp.length;
         attrVals = new String[arraysize + 1];
         System.arraycopy(arrTmp, 0, attrVals, 0, arraysize);
         attrVals[arraysize] = _valName;
         input.addAttribute(_attributeName, attrVals);
      }
      else
      {
         input.addAttribute(_attributeName, _valName);
      }

      return;
   }
}
