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

import java.util.List;

import org.openptk.api.ElementIF;
import org.openptk.api.Output;

//===================================================================
public class getUniqueIdTag extends AbstractTag
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String _output = null;
   private String _result = null;
   private String _var = null;

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
      Object uid = null;
      String METHOD_NAME = CLASS_NAME + ":process(): ";
      String uniqueId = null;
      ElementIF result = null;
      Output output = null;
      List<ElementIF> results = null;

      /*
       * initialize the "var" to an empty string, if var was passed in.
       */

      if (_var != null && _var.length() > 0)
      {
         this.setString(_var, "");
      }

      /*
       * check to see if there's a "result" or "subject"
       */

      if (_result != null && _result.length() > 0)
      {
         result = this.getElement(_result);
         if (result != null)
         {
            uid = result.getUniqueId();
            if (uid != null)
            {
               uniqueId = uid.toString();
            }
         }
      }
      else if (_output != null && _output.length() > 0)
      {
         output = this.getOutput(_output);
         if (output != null)
         {
            results = output.getResults();
            if (results != null && results.size() > 0)
            {
               result = results.get(0);
               if (result != null)
               {
                  uid = result.getUniqueId();
                  if (uid != null)
                  {
                     uniqueId = uid.toString();
                  }
               }
            }
            else
            {
               uid = output.getUniqueId();
               if (uid != null)
               {
                  uniqueId = uid.toString(); // from a create operation
               }
            }
         }
      }
      else
      {
         throw new Exception(METHOD_NAME + "Must set either 'result' or 'output'.");
      }

      if (uniqueId != null && uniqueId.length() > 0)
      {
         if (_var != null && _var.length() > 0)
         {
            this.setString(_var, uniqueId);
         }
         else
         {
            try
            {
               _jspWriter.print(uniqueId);
            }
            catch (IOException ex)
            {
               throw new Exception(METHOD_NAME + ex.getMessage()
                  + ": for '" + uniqueId + "'");
            }
         }
      }
      else
      {
         throw new Exception(METHOD_NAME + "UniqueID is null");
      }

      return;
   }
}
