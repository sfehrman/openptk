/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2007-2009 Sun Microsystems, Inc.
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
 * Portions Copyright 2011 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.debug;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;

import org.openptk.api.AttributeIF;
import org.openptk.api.ElementIF;
import org.openptk.logging.Logger;

//===================================================================
public class DebugElement extends Debug implements DebugIF
//===================================================================
{

   private final String CLASS_NAME = this.getClass().getSimpleName();

   /**
    * @param obj
    * @param callerId
    */
   //----------------------------------------------------------------
   @Override
   public void logData(final Object obj, final String callerId)
   //----------------------------------------------------------------
   {
      Logger.logInfo(process((ElementIF) obj, callerId));

      return;
   }

   /**
    * @param obj
    * @param callerId
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public String getData(final Object obj, final String callerId)
   //----------------------------------------------------------------
   {
      return process((ElementIF) obj, callerId);
   }

   /**
    * @param obj
    * @param callerId
    * @param indent
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public String getData(final Object obj, final String callerId, final String indent)
   //----------------------------------------------------------------
   {
      _extIndent = indent;
      return process((ElementIF) obj, callerId);
   }
   //----------------------------------------------------------------
   private String process(final ElementIF elem, final String callerId)
   //----------------------------------------------------------------
   {
      StringBuilder buf = new StringBuilder();

      buf.append(_extIndent + CLASS_NAME + ", callerId=" + callerId + "\n");
      buf.append(dbg_level_1(elem));

      /*
       * may add more debug levels in the future
       */

      return buf.toString();
   }

   /**
    * @param elem
    * @return
    */
   //----------------------------------------------------------------
   protected StringBuffer dbg_level_1(final ElementIF elem)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();
      int iCnt = 0;
      Object obj = null;
      String[] values = null;
      Map<String, AttributeIF> mapAttr = null;
      Properties props = null;
      SortedSet<String> propKeys = null;

      buf.append(_extIndent + INDENT_1 + elem.getClass().getName() + "\n");

      if (elem != null)
      {
         buf.append(_extIndent + DebugIF.INDENT_1 + "uniqueId: " 
            + (elem.getUniqueId() != null ? elem.getUniqueId().toString() : "(null)") + "\n");
         buf.append(_extIndent + DebugIF.INDENT_1 + "description: " 
            + (elem.getDescription() != null ? elem.getDescription().toString() : "(null)") + "\n");
         buf.append(_extIndent + DebugIF.INDENT_1 + "state: " + elem.getStateAsString() + "\n");
         buf.append(_extIndent + DebugIF.INDENT_1 + "status: " + elem.getStatus() + "\n");
         buf.append(_extIndent + DebugIF.INDENT_1 + "error: " + elem.isError() + "\n");

         // Get the Properties

         iCnt = 0;
         props = elem.getProperties();

         buf.append(_extIndent + INDENT_1 + "Properties: ");

         if (props != null)
         {
            buf.append(props.size() + "\n");
            
            propKeys = getSortedKeys(props);

            for (String propName : propKeys)
            {
               buf.append(_extIndent + DebugIF.INDENT_2 + iCnt++ + ": ");
               
               if (propName != null)
               {
                  buf.append(propName + "=" + ((props.getProperty(propName) != null) ? props.getProperty(propName) : DebugIF.NULL));
               }
               else
               {
                  buf.append("Name is NULL");
               }
               buf.append("\n");
            }
         }
         else
         {
            buf.append(DebugIF.NULL + "\n");
         }

         // Get the attributes

         iCnt = 0;
         mapAttr = elem.getAttributes();

         buf.append(_extIndent + DebugIF.INDENT_1 + "Attributes: ");

         if (mapAttr != null)
         {
            buf.append(mapAttr.size() + "\n");
            
            for (AttributeIF attribute : mapAttr.values())
            {
               buf.append(_extIndent + DebugIF.INDENT_2 + iCnt++ + ": ");
               buf.append("name=" + attribute.getName());
               buf.append(", type=" + attribute.getTypeAsString());
               buf.append(", required=" + attribute.isRequired());
               buf.append(", multivalued=" + attribute.isMultivalued());
               buf.append(", value=");
               
               obj = attribute.getValue();
               
               if (obj != null)
               {
                  switch (attribute.getType())
                  {
                     case STRING:
                     {
                        if (attribute.isMultivalued())
                        {
                           values = (String[]) obj;
                           buf.append("[");
                           for (int j = 0; j < values.length; j++)
                           {
                              buf.append(values[j]);
                              if (j < (values.length - 1))
                              {
                                 buf.append(", ");
                              }
                           }
                           buf.append("]");
                        }
                        else
                        {
                           buf.append((String) obj);
                        }
                        break;
                     }
                     case INTEGER:
                        break;
                     case LONG:
                        break;
                     case BOOLEAN:
                        break;
                     default: // Type = OBJECT, use toString()
                     {
                        buf.append(obj.toString());
                        break;
                     }
                  }
               }
               else
               {
                  buf.append(DebugIF.NULL);
               }
               buf.append("\n");
            }
         }
         else
         {
            buf.append(DebugIF.NULL + "\n");
         }
      }
      else
      {
         buf.append(_extIndent + DebugIF.INDENT_2 + "Element is NULL\n");
      }
      return buf;
   }
}
