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

import org.openptk.common.AttrIF;
import org.openptk.common.ComponentIF;
import org.openptk.logging.Logger;
import org.openptk.logging.LoggingLevel;

//===================================================================
public class DebugComponent extends Debug implements DebugIF
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
      LoggingLevel logLevel = LoggingLevel.CONFIG;

      logLevel = get_level((ComponentIF) obj);

      Logger.log(logLevel, process((ComponentIF) obj, callerId));

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
      return process((ComponentIF) obj, callerId);
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
      return process((ComponentIF) obj, callerId);
   }

   /**
    * @param comp
    * @param callerId
    * @return
    */
   //----------------------------------------------------------------
   protected String process(final ComponentIF comp, final String callerId)
   //----------------------------------------------------------------
   {
      StringBuilder buf = new StringBuilder();
      LoggingLevel logLevel = LoggingLevel.CONFIG;

      logLevel = get_level(comp);

      buf.append(_extIndent).append(CLASS_NAME).append(", callerId=")
         .append(callerId).append(", ")
         .append((comp != null) ? comp.getClass().getName() : DebugIF.NULL)
         .append("\n");

      // Logger.config
      buf.append(dbg_level_1(comp));

      switch (logLevel)
      {
         case FINE:
            buf.append(dbg_level_2(comp)); // Logger.fine
            break;
         case FINER:
            buf.append(dbg_level_2(comp)); // Logger.fine
            buf.append(dbg_level_3(comp)); // Logger.finer
            break;
         case FINEST:
            buf.append(dbg_level_2(comp)); // Logger.fine
            buf.append(dbg_level_3(comp)); // Logger.finer
            buf.append(dbg_level_4(comp)); // Logger.finest
            break;
      }

      return buf.toString();
   }

   /**
    * @param comp
    * @return
    */
   //----------------------------------------------------------------
   protected LoggingLevel get_level(final ComponentIF comp)
   //----------------------------------------------------------------
   {
      LoggingLevel level = LoggingLevel.CONFIG;

      switch (comp.getDebugLevel())
      {
         case CONFIG:
            level = LoggingLevel.CONFIG;
            break;
         case FINE:
            level = LoggingLevel.FINE;
            break;
         case FINER:
            level = LoggingLevel.FINER;
            break;
         case FINEST:
            level = LoggingLevel.FINEST;
            break;
      }

      return level;
   }

   /**
    * @param comp
    * @return
    */
   //----------------------------------------------------------------
   protected StringBuffer dbg_level_1(final ComponentIF comp)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();

      if (comp.isDebug())
      {
         buf.append(_extIndent)
            .append(DebugIF.INDENT_1 + "Metadata: ")
            .append("hashCode=")
            .append(Integer.toString(comp.hashCode()))
            .append(", uniqueId='")
            .append((comp.getUniqueId() != null) ? comp.getUniqueId().toString() : NULL)
            .append("', category=")
            .append(comp.getCategoryAsString())
            .append(", description='")
            .append((comp.getDescription() != null) ? comp.getDescription() : NULL)
            .append("', sortValue='")
            .append((comp.getSortValue() != null) ? comp.getSortValue() : NULL)
            .append("', status='")
            .append((comp.getStatus() != null) ? comp.getStatus() : NULL)
            .append("', state=")
            .append(comp.getStateAsString())
            .append(", error=")
            .append(comp.isError())
            .append(", debug=")
            .append(comp.isDebug())
            .append(", level=")
            .append(comp.getDebugLevelAsString());
      }
      else
      {
         buf.append(_extIndent).append(INDENT_1 + "NOTICE (DL1): Debug not enabled.\n");
      }
      return buf;
   }

   /**
    * @param comp
    * @return
    */
   //----------------------------------------------------------------
   protected StringBuffer dbg_level_2(final ComponentIF comp)
   //----------------------------------------------------------------
   {
      int iCnt = 0;
      StringBuffer buf = new StringBuffer();
      Properties props = null;
      SortedSet<String> propKeys = null;
      String val = null;

      if (comp.isDebug())
      {
         props = comp.getProperties();
         buf.append(_extIndent).append(INDENT_1 + "Properties: ");

         if (props != null)
         {
            if (props.isEmpty())
            {
               buf.append("(empty)\n");
            }
            else
            {
               buf.append(props.size()).append("\n");

               propKeys = getSortedKeys(props);

               for (String key : propKeys)
               {
                  buf.append(_extIndent).append(DebugIF.INDENT_2).append(iCnt++).append(": ");
                  val = null;
                  if (key != null)
                  {
                     if (key.length() > 0)
                     {
                        val = props.getProperty(key);
                        if (val != null)
                        {
                           if (val.length() > 0)
                           {
                              buf.append(key).append("=").append(val);
                           }
                           else
                           {
                              buf.append(key).append(": (EMPTY value)");
                           }
                        }
                        else
                        {
                           buf.append(key).append(": (NULL value)");
                        }
                     }
                     else
                     {
                        buf.append("key length is 0");
                     }
                  }
                  else
                  {
                     buf.append("key is NULL");
                  }
                  buf.append("\n");
               }
            }
         }
         else
         {
            buf.append(DebugIF.NULL + "\n");
         }

      }
      else
      {
         buf.append(_extIndent).append(INDENT_1 + "NOTICE (DL2): Debug not enabled.\n");
      }
      return buf;
   }

   /**
    * @param comp
    * @return
    */
   //----------------------------------------------------------------
   protected StringBuffer dbg_level_3(final ComponentIF comp)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();
      Set<String> set = comp.getTimeStampNames();
      Long time = null;

      if (comp.isDebug())
      {
         buf.append(_extIndent).append(INDENT_1 + "Timestamps: ");
         if (set != null)
         {
            buf.append(set.size()).append("\n");
            
            for (String key : set)
            {
               time = null;
               if (key != null)
               {
                  if (key.length() > 0)
                  {
                     time = comp.getTimeStamp(key);
                     if (time != null)
                     {
                        buf.append(_extIndent).append(DebugIF.INDENT_2).append(key).append("=").append(time).append("\n");
                     }
                     else
                     {
                        buf.append(_extIndent).append(DebugIF.INDENT_2).append(key).append(": time is NULL\n");
                     }
                  }
                  else
                  {
                     buf.append(_extIndent).append(DebugIF.INDENT_2 + "key length is 0\n");
                  }
               }
               else
               {
                  buf.append(_extIndent).append(DebugIF.INDENT_2 + "key is NULL\n");
               }
            }
         }
         else
         {
            buf.append(DebugIF.NULL + "\n");
         }
      }
      else
      {
         buf.append(_extIndent).append(INDENT_1 + "NOTICE (DL3): Debug not enabled.\n");
      }
      return buf;
   }

   /**
    * @param comp
    * @return
    */
   //----------------------------------------------------------------
   protected StringBuffer dbg_level_4(final ComponentIF comp)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();
      Map<String, AttrIF> attrs = null;
      AttrIF attr = null;
      int iCnt = 0;
      DebugAttr dbgAttr = null;

      if (comp.isDebug())
      {
         buf.append(_extIndent).append(DebugIF.INDENT_1 + "Attributes: ");
         attrs = comp.getAttributes();
         if (attrs != null)
         {
            if (attrs.size() > 0)
            {
               buf.append(attrs.size()).append("\n");

               dbgAttr = new DebugAttr();

               for (String key : attrs.keySet())
               {
                  buf.append(_extIndent).append(DebugIF.INDENT_2).append(iCnt++).append(": ");
                  if (key != null)
                  {
                     attr = attrs.get(key);
                     if (attr != null)
                     {
                        buf.append(dbgAttr.getData(attr, CLASS_NAME, DebugIF.INDENT_1));
                     }
                     else
                     {
                        buf.append("Attribute is NULL for key '").append(key).append("'\n");
                     }
                  }
                  else
                  {
                     buf.append("WARNING: Map Key is NULL\n");
                  }
               }
            }
            else
            {
               buf.append(_extIndent).append(DebugIF.INDENT_2 + "(empty)\n");
            }
         }
         else
         {
            buf.append(DebugIF.NULL + "\n");
         }
      }
      else
      {
         buf.append(_extIndent).append(DebugIF.INDENT_1 + "NOTICE (DL4): Debug not enabled.\n");
      }

      return buf;
   }
}
