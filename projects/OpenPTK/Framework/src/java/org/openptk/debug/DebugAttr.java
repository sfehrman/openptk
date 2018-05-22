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

import java.util.List;
import java.util.Map;

import org.openptk.common.AttrIF;
import org.openptk.common.Operation;
import org.openptk.definition.functions.ArgumentIF;
import org.openptk.definition.functions.TaskIF;
import org.openptk.logging.Logger;

//===================================================================
public class DebugAttr extends Debug implements DebugIF
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
      Logger.logInfo(process((AttrIF) obj, callerId));

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
      return process((AttrIF) obj, callerId);
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
      return process((AttrIF) obj, callerId);
   }

   //----------------------------------------------------------------
   private String process(final AttrIF attr, final String callerId)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();

      buf.append(dbg_level_1(attr));
      buf.append(dbg_level_2(attr));

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
   protected StringBuffer dbg_level_1(final AttrIF attr)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();
      DebugAttribute dbgAttribute = null;

      if (attr != null)
      {
         dbgAttribute = new DebugAttribute();

         buf.append(dbgAttribute.getData(attr, CLASS_NAME, DebugIF.INDENT_1));

         if (attr.getServiceName() != null)
         {
            buf.append(", serviceName=" + attr.getServiceName());
         }

      }
      else
      {
         buf.append(_extIndent + DebugIF.INDENT_2 + DebugIF.NULL);
      }

      buf.append("\n");

      return buf;
   }

   /**
    * @param attr
    * @return
    */
   //----------------------------------------------------------------
   protected StringBuffer dbg_level_2(final AttrIF attr)
   //----------------------------------------------------------------
   {
      int cnt = 0;
      StringBuffer buf = new StringBuffer();
      Map<String, TaskIF> taskMap = null;
      Operation[] operations = null;

      if (attr != null)
      {
         operations = Operation.values();

         /*
          * Show details about all the Functions assigned to this Attribute
          */

         taskMap = attr.getTasks();
         if (taskMap != null && !taskMap.isEmpty())
         {
            buf.append(_extIndent + DebugIF.INDENT_2
               + "Functions: " + taskMap.size() + "\n");

            for (TaskIF task : taskMap.values())
            {
               if (task != null)
               {
                  buf.append(_extIndent + DebugIF.INDENT_3 + cnt++
                     + ": id="
                     + (task.getUniqueId() != null ? task.getUniqueId().toString() : "(null)")
                     + ", useexisting=" + task.useExisting()
                     + ", " + task.getFunctionClassname()
                     + "\n");

                  buf.append(this.getArgumentData(task.getArguments()));
               }
            }

            /*
             * Show the Operations and what Functions they have assigned
             * They will be listed in order
             */

            buf.append(_extIndent + DebugIF.INDENT_3 + "Operations:\n");

            for (int i = 0; i < operations.length; i++)
            {
               taskMap = attr.getTasks(operations[i]);
               if (taskMap != null && !taskMap.isEmpty())
               {
                  buf.append(_extIndent + DebugIF.INDENT_4
                     + operations[i].toString() + ":");

                  for (TaskIF task : taskMap.values())
                  {
                     if (task != null)
                     {
                        buf.append(" ");
                        buf.append((task.getUniqueId() != null ? task.getUniqueId().toString() : "(null)"));
                     }
                  }
                  buf.append("\n");
               }
            }
         }

      }
      return buf;
   }

   //----------------------------------------------------------------
   private StringBuffer getArgumentData(final List<ArgumentIF> args)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();

      for (ArgumentIF arg : args)
      {
         buf.append(_extIndent + DebugIF.INDENT_4
            + "name=" + arg.getName()
            + ", required=" + arg.isRequired()
            + ", type=" + arg.getType().toString()
            + ", value=");

         switch (arg.getType())
         {
            case ATTRIBUTE:
               buf.append(arg.getValue());
               break;
            case LITERAL:
               buf.append("'" + arg.getValue() + "'");
               break;
         }

         buf.append("\n");
      }
      return buf;
   }
}
