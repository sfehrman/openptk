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
 * Portions Copyright 2011 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.debug;

import org.openptk.api.AttributeIF;
import org.openptk.logging.Logger;

//===================================================================
public class DebugAttribute extends Debug implements DebugIF
//===================================================================
{

  /**
    * @param obj
    * @param callerId
    */
   //----------------------------------------------------------------
   @Override
   public void logData(final Object obj, final String callerId)
   //----------------------------------------------------------------
   {
      Logger.logInfo(process((AttributeIF) obj, callerId));

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
      return process((AttributeIF) obj, callerId);
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
      return process((AttributeIF) obj, callerId);
   }

   //----------------------------------------------------------------
   private String process(final AttributeIF attribute, final String callerId)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();

//      buf.append(this._extIndent + ClassName + ", callerId=" + callerId + "\n");
      buf.append(dbg_level_1(attribute));

      /*
       * may add more debug levels in the future
       */
      
      return buf.toString();
   }


   /**
    * @param attr
    * @return
    */
   //----------------------------------------------------------------
   protected StringBuffer dbg_level_1(final AttributeIF attr)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();
      String value = null;

      if (attr != null)
      {
         value = this.getValue(attr);
         buf.append(
            //            this._extIndent + DebugIF.INDENT_2 +
            "name=" + attr.getName() +
            ", type=" + attr.getTypeAsString() +
            ", state=" + attr.getState().toString() +
            ", access=" + attr.getAccessAsString() +
            ", readonly=" + attr.isReadOnly() +
            ", required=" + attr.isRequired() +
            ", virtual=" + attr.isVirtual() +
            ", encrypted=" + attr.isEncrypted());
         if (value != null)
         {
            buf.append(", value=" + value);
         }
      }
      else
      {
         buf.append(_extIndent + DebugIF.INDENT_2 + DebugIF.NULL);
      }

      return buf;
   }

   //----------------------------------------------------------------
   private String getValue(final AttributeIF attr)
   //----------------------------------------------------------------
   {
      Object obj = null;
      String val = null;
      StringBuffer buf = null;
      String[] values = null;

      obj = attr.getValue();
      if (obj != null)
      {
         buf = new StringBuffer();
         switch (attr.getType())
         {
            case STRING:
            {
               if (attr.isMultivalued())
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
      if (buf != null && buf.length() > 0)
      {
         val = buf.toString();
      }
      return val;
   }
}
