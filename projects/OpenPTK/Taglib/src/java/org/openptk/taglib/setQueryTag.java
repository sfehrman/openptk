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

import org.openptk.api.Input;
import org.openptk.api.Query;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public class setQueryTag extends AbstractTag
//===================================================================
{
   private String _var = null;
   private String _input = null;
   private String _name = null;
   private String _value = null;
   private Query.Type _queryType = Query.Type.EQ;

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
   public void setType(final String arg)
   //----------------------------------------------------------------
   {
      Query.Type[] typeArray = null;

      typeArray = Query.Type.values();

      for (int i = 0; i < typeArray.length; i++)
      {
         if (arg.equalsIgnoreCase(typeArray[i].toString()))
         {
            _queryType = typeArray[i];
            break;
         }
      }

      return;
   }


   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setName(final String arg)
   //----------------------------------------------------------------
   {
      _name = arg;
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
      Query query = null;
      Input input = null;

      input = getInput(_input);

      query = new Query(_queryType, _name, _value);
      input.setQuery(query);

      this.setQuery(_var, query);

      return;
   }
}
