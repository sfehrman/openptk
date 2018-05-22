/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2009-2010 Sun Microsystems, Inc.
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
import org.openptk.api.Opcode;
import org.openptk.api.Output;
import org.openptk.connection.ConnectionIF;
import org.openptk.structure.StructureIF;

/*
 *<tag>
 *   <name>doPasswordForgot</name>
 *   <tag-class>org.openptk.taglib.doPasswordForgotTag</tag-class>
 *   <body-content>scriptless</body-content>
 *   <attribute>
 *      <name>connection</name>
 *      <required>true</required>
 *      <rtexprvalue>true</rtexprvalue>
 *      <type>java.lang.String</type>
 *   </attribute>
 *   <attribute>
 *      <name>input</name>
 *      <required>true</required>
 *      <rtexprvalue>true</rtexprvalue>
 *      <type>java.lang.String</type>
 *   </attribute>
 *   <attribute>
 *      <name>output</name>
 *      <required>true</required>
 *      <rtexprvalue>true</rtexprvalue>
 *      <type>java.lang.String</type>
 *   </attribute>
 *</tag>
 */

//===================================================================
public class doPasswordForgotTag extends AbstractTag
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String _connName = null;
   private String _inputName = null;
   private String _outputName = null;

   /*
    * The "input" object shall contain the following Properties:
    *    mode =  [ "questions" | "answers" | "change" ]
    */


   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setConnection(final String arg) // REQUIRED
   //----------------------------------------------------------------
   {
      _connName = arg;
      return;
   }


   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setInput(final String arg) // REQUIRED
   //----------------------------------------------------------------
   {
      _inputName = arg;
      return;
   }


   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setOutput(final String arg) // REQURED
   //----------------------------------------------------------------
   {
      _outputName = arg;
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
      String mode = null;
      ConnectionIF conn = null;
      Input input = null;
      Output output = null;

      /*
       * make sure all the required arguments have been set
       */

      if (_connName == null || _connName.length() < 1)
      {
         throw new Exception(METHOD_NAME + "Connection argument is not set (required)");
      }

      if (_inputName == null || _inputName.length() < 1)
      {
         throw new Exception(METHOD_NAME + "Input argument is not set (required)");
      }

      if (_outputName == null || _outputName.length() < 1)
      {
         throw new Exception(METHOD_NAME + "Output argument is not set (required)");
      }

      /*
       * get the objects out of the Session
       */

      conn = this.getConnection(_connName);
      input = this.getInput(_inputName);

      /*
       * Get / check the properties in the Input
       */

      mode = input.getProperty(StructureIF.NAME_MODE);
      if (mode == null || mode.length() < 1)
      {
         throw new Exception(METHOD_NAME + "Input Property '" +
            StructureIF.NAME_MODE + "' is not set");
      }

      if (!mode.equalsIgnoreCase(StructureIF.NAME_QUESTIONS) &&
         !mode.equalsIgnoreCase(StructureIF.NAME_ANSWERS) &&
         !mode.equalsIgnoreCase(StructureIF.NAME_CHANGE))
      {
         throw new Exception(METHOD_NAME + "Property '" +
            StructureIF.NAME_MODE + "' has an invalid value: '" +
            mode + "'");
      }

      output = conn.execute(Opcode.PWDFORGOT, input); // throws exception

      if (output == null)
      {
         throw new Exception(METHOD_NAME + "Output is null");
      }

      this.setOutput(_outputName, output);

      return;
   }
}
