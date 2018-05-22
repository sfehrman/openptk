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

/********************************************************************
 * TLD:
 * <tag>
 *   <name>getInformation</name>
 *   <tag-class>org.openptk.taglib.getInformationTag</tag-class>
 *   <body-content>scriptless</body-content>
 *   <attribute>
 *     <name>type</name>
 *     <required>true</required>
 *     <rtexprvalue>true</rtexprvalue>
 *     <type>java.lang.String</type>
 *   </attribute>
 *   <attribute>
 *     <name>element</name>
 *     <required>true</required>
 *     <rtexprvalue>true</rtexprvalue>
 *     <type>java.lang.String</type>
 *   </attribute>
 *   <attribute>
 *     <name>var</name>
 *     <required>false</required>
 *     <rtexprvalue>true</rtexprvalue>
 *     <type>java.lang.String</type>
 *   </attribute>
 *   <attribute>
 *     <name>_scope</name>
 *     <required>false</required>
 *     <rtexprvalue>true</rtexprvalue>
 *     <type>java.lang.String</type>
 *   </attribute>
 *</tag>
 *
 ********************************************************************/
/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 */
//===================================================================
public class getInformationTag extends AbstractTag
//===================================================================
{

   /*
    * Get information about the specified "Element"
    * arguments:
    *   type="description","state","statestring","status","error" REQUIRED
    *   element=<variable_referencing_an_element> REQUIRED
    *   var="<variable_to_store_info> OPTIONAL
    * 
    * If a "var" argument is not specified, the info will be sent to the
    * JspWriter (standard out)
    */
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private String _type = null;
   private String _element = null;
   private String _var = null;


   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setType(final String arg) // REQUIRED
   //----------------------------------------------------------------
   {
      _type = arg;
      return;
   }


   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setElement(final String arg) // REQUIRED
   //----------------------------------------------------------------
   {
      _element = arg;
      return;
   }


   /**
    * @param arg
    */
   //----------------------------------------------------------------
   public void setVar(final String arg) // OPTIONAL
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
      String METHOD_NAME = CLASS_NAME + ":process(): ";
      String info = null;
      ElementIF elem = null;

      if (_element == null || _element.length() < 1)
      {
         throw new Exception(METHOD_NAME + "Element is not set.");
      }

      elem = this.getElement(_element);

      if (_type == null || _type.length() < 1)
      {
         throw new Exception(METHOD_NAME + "Type is not set, element='" + _element + "'");
      }

      if (_type.equalsIgnoreCase(AbstractTag.INFO_DESC))
      {
         info = elem.getDescription();
      }
      else if (_type.equalsIgnoreCase(AbstractTag.INFO_ERROR))
      {
         info = Boolean.toString(elem.isError());
      }
      else if (_type.equalsIgnoreCase(AbstractTag.INFO_STATE))
      {
         info = elem.getState().toString();
      }
      else if (_type.equalsIgnoreCase(AbstractTag.INFO_STATESTRING))
      {
         info = elem.getStateAsString();
      }
      else if (_type.equalsIgnoreCase(AbstractTag.INFO_STATUS))
      {
         info = elem.getStatus();
      }
      else
      {
         throw new Exception(METHOD_NAME +"Unknown type '" + _type + "'");
      }

      /*
       * If the var name is not set, send the information "inline"
       */

      if (_var != null && _var.length() > 0)
      {
         this.setString(_var, info);
      }
      else
      {
         try
         {
            _jspWriter.print(info);
         }
         catch (IOException ex)
         {
            throw new Exception(METHOD_NAME + ex.getMessage());
         }
      }

      return;
   }
}
