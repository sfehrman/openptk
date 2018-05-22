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

import org.openptk.api.AttributeIF;
import org.openptk.api.ElementIF;
import org.openptk.api.Output;

/*
 * This tag can get a value from multiple object types
 * the type is determined by which tag arguments are sent:
 *
 * syntax: getValue [attribute | result | output ] && name  <var>
 *
 * - Attribute:  attribute, name
 *               the "named" attribute is obtained from the Session
 *
 * - Result:     result, name
 *               the specified result is obtained from the Session
 *               then the "named" attribute is obtained from the result
 *
 * - Output:     output, name
 *               the specified output is obtained from the Session
 *               then the first (0) result is obtained from the output
 *               then the "named" attribute is obtained from the result
 *
 * If "var" is set, then the value will get placed into that variable which
 * can be accessed via the JSTL <c:out ... /> tag.  Else the value is
 * displayed.
 */
//===================================================================
public class getValueTag extends AbstractTag
//===================================================================
{
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String _var = null;
   private String _name = null;
   private String _attribute = null;
   private String _result = null;
   private String _output = null;


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
   public void setAttribute(final String arg)
   //----------------------------------------------------------------
   {
      _attribute = arg;
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
      String name = null;
      AttributeIF attribute = null;
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
       * Process either a "Attribute", "Result" or "Output" object
       */

      if (_attribute != null && _attribute.length() > 0)
      {
         attribute = this.getAttribute(_attribute);
      }
      else if (_result != null && _result.length() > 0)
      {
         result = this.getElement(_result);

         if (_name != null && _name.length() > 0)
         {
            attribute = result.getAttribute(_name);
         }
         else
         {
            throw new Exception(METHOD_NAME + "Result: name is not set");
         }
      }
      else if (_output != null && _output.length() > 0)
      {
         output = this.getOutput(_output);

         results = output.getResults();
         if (results == null)
         {
            throw new Exception(METHOD_NAME + "Output Results are null, name='" + _name + "'");
         }

         result = results.get(0);
         if (result == null)
         {
            throw new Exception(METHOD_NAME + "Results[0] is null");
         }

         if (_name != null && _name.length() > 0)
         {
            attribute = result.getAttribute(_name);
         }
         else
         {
            throw new Exception(METHOD_NAME + "Result[0]: name is not set");
         }
      }
      else
      {
         throw new Exception(METHOD_NAME + ": Must set either 'attribute', 'result' or 'output'");
      }

      if (attribute == null)
      {
         throw new Exception(METHOD_NAME + "Attribute object is null.");
      }

      name = attribute.getValueAsString();
      if (_var != null && _var.length() > 0)
      {
         this.setString(_var, name);
      }
      else
      {
         try
         {
            _jspWriter.print(name);
         }
         catch (IOException ex)
         {
            throw new Exception(METHOD_NAME + ex.getMessage());
         }
      }
      
      return;
   }
}
