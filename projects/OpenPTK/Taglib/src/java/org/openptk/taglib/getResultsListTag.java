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

import java.util.LinkedList;
import java.util.List;

import org.openptk.api.ElementIF;
import org.openptk.api.Output;

//===================================================================
public class getResultsListTag extends AbstractTag
//===================================================================
{
   private String _name = null;
   private String _size = null;
   private String _output = null;

   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setVar(final String arg)
   //----------------------------------------------------------------
   {
      _name = arg;
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
   public void setOutput(final String arg)
   //----------------------------------------------------------------
   {
      _output = arg;
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
      String listSize = "0";
      Output output = null;
      List<ElementIF> results = null;

      output = this.getOutput(_output);

      results = output.getResults();

      if (results != null)
      {
         listSize = Integer.toString(output.getResultsSize());
      }
      else
      {
         results = new LinkedList<ElementIF>();
      }

      this.setResultList(_name, results);
      this.setString(_size, listSize);

      return;
   }
}
