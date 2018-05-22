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

import java.io.IOException;

import org.openptk.api.ElementIF;

//===================================================================
public class getPropertyTag extends AbstractTag
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String _var = null;
   private String _name = null;
   private String _output = null;
   private String _result = null;


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
   public void setResult(final String arg)
   //----------------------------------------------------------------
   {
      _result = arg;
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
      String METHOD_NAME = CLASS_NAME + ":process(): ";
      String elemName = null;
      String propValue = null;
      ElementIF element = null;

      if (_result == null)
      {
         _result = "";
      }
      if (_output == null)
      {
         _output = "";
      }

      if (_result.length() > 0)
      {
         elemName = _result;
      }
      else if (_output.length() > 0)
      {
         elemName = _output;
      }
      else
      {
         throw new Exception(METHOD_NAME + ": Must set either 'result' or 'output'.");
      }

      element = this.getElement(elemName);

      propValue = element.getProperty(_name);

      if (propValue == null)
      {
         throw new Exception(METHOD_NAME + ": Property is null for '" + elemName + "'");
      }

      if (_var != null && _var.length() > 0)
      {
         this.setString(_var, propValue);
      }
      else
      {
         try
         {
            _jspWriter.print(propValue);
         }
         catch (IOException ex)
         {
            throw new Exception(METHOD_NAME + ex.getMessage() + ": for '" + elemName + "'");
         }
      }

      return;
   }
}
