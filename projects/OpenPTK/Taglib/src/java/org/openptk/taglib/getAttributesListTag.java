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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openptk.api.AttributeIF;
import org.openptk.api.ElementIF;

//===================================================================
public class getAttributesListTag extends AbstractTag
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String _var = null;
   private String _size = null;
   private String _result = null;


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
   public void setResult(final String arg)
   //----------------------------------------------------------------
   {
      _result = arg;
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
      ElementIF result = null;
      List<AttributeIF> attrList = null;
      Map<String, AttributeIF> attrMap = null;
      Iterator<String> attrIter = null;

      result = this.getElement(_result);
      attrList = new LinkedList<AttributeIF>();

      if (result != null)
      {
         attrMap = result.getAttributes();

         if (attrMap != null)
         {
            attrIter = attrMap.keySet().iterator();
            while (attrIter.hasNext())
            {
               attrList.add(attrMap.get(attrIter.next()));
            }
         }
         else
         {
            throw new Exception(CLASS_NAME + "Result has no attributes.");
         }
      }
      else
      {
         throw new Exception(CLASS_NAME + "Result is null.");
      }

      this.setAttributeList(_var, attrList);
      this.setString(_size, Integer.toString(attrList.size()));

      return;
   }
}