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
import org.openptk.api.DataType;

//===================================================================
public class getValuesListTag extends AbstractTag
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String _size = null;
   private String _var = null;
   private String _attribute = null;

   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setVar(final String arg)
   //----------------------------------------------------------------
   {
      _var = arg;
      return;
   }

   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setSizevar(final String arg)
   //----------------------------------------------------------------
   {
      _size = arg;
      return;
   }

   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setAttribute(final String arg)
   //----------------------------------------------------------------
   {
      _attribute = arg;
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
      Object obj = null;
      String METHOD_NAME = CLASS_NAME + ":process(): ";
      String[] value = null;
      AttributeIF attr = null;

      if (_attribute == null || _attribute.length() < 1)
      {
         throw new Exception(METHOD_NAME + "Attribute name is null.");
      }

      attr = this.getAttribute(_attribute);

      obj = attr.getValue();

      if (obj == null)
      {
         value = new String[0];
      }
      else
      {
         if (attr.getType() == DataType.STRING)
         {
            if (attr.isMultivalued())
            {
               value = (String[]) attr.getValue();
            }
            else
            {
               value = new String[1];
               value[0] = attr.getValueAsString();
            }
         }
         else
         {
            value = new String[0];
         }
      }

      if (_var != null && _var.length() > 0)
      {
         this.setStringArray(_var, value);
      }

      if (_size != null && _size.length() > 0)
      {
         this.setString(_size, Integer.toString(value.length));
      }

      return;
   }
}
