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
public class setAttributeTag extends AbstractTag
//===================================================================
{
   private String _input = null;
   private String _key = null;
   private String _value = null;
   private String _var = null;

   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setInput(final String arg)
   //----------------------------------------------------------------
   {
      _input = arg;
      return;
   }


   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setKey(final String arg)
   //----------------------------------------------------------------
   {
      _key = arg;
      return;
   }


   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setValue(final String arg)
   //----------------------------------------------------------------
   {
      _value = arg;
      return;
   }


   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setVar(final String arg)
   //----------------------------------------------------------------
   {
      /*
       * Name of an Attribute that is in the Session
       */
      _var = arg;
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
      AttributeIF attr = null;
      Input input = null;

      input = this.getInput(_input);

      if (_value != null && _value.length() > 1)
      {
         input.addAttribute(_key, _value);
      }
      else if (_var != null && _var.length() > 1)
      {
         attr = this.getAttribute(_var);
         input.addAttribute(attr);
      }
      else
      {
         input.addAttribute(_key);
      }

      return;
   }
}
