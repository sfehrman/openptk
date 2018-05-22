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
import java.util.Set;

import org.openptk.common.AttrIF;
import org.openptk.common.Operation;
import org.openptk.definition.DefinitionIF;
import org.openptk.definition.functions.ArgumentIF;
import org.openptk.definition.functions.ArgumentType;
import org.openptk.definition.functions.TaskIF;
import org.openptk.logging.Logger;

//===================================================================
public class DebugDefinition extends DebugComponent implements DebugIF
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
      Logger.logInfo(process((DefinitionIF) obj, callerId));

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
      return process((DefinitionIF) obj, callerId);
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
      return process((DefinitionIF) obj, callerId);
   }

   //----------------------------------------------------------------
   private String process(final DefinitionIF def, final String callerId)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();

      buf.append(_extIndent + this.CLASS_NAME +
         ", callerId=" + callerId +
         "\n");

      if (def != null)
      {
         if (def.isDebug())
         {
            buf.append(dbg_level_1(def));
            buf.append(dbg_level_2(def));
            buf.append(dbg_level_3(def));
         }
         else
         {
            buf.append(_extIndent + INDENT_1 + "NOTICE: Debug not enabled.\n");
         }
      }
      else
      {
         buf.append(_extIndent + INDENT_1 + "NOTICE: Definition is NULL.\n");
      }
      /*
       * may add more debug levels in the future
       */

      return buf.toString();
   }

   /**
    * @param def
    * @return
    */
   //----------------------------------------------------------------
   protected StringBuffer dbg_level_1(final DefinitionIF def)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();

      buf.append(_extIndent + DebugIF.INDENT_1 + "ClassName: " + def.getDefinitionClassName() + "\n");

      buf.append(super.dbg_level_1(def));

      buf.append(_extIndent + DebugIF.INDENT_1 + "Attributes: " + def.getAttributesSize() + "\n");

      return buf;
   }

   /**
    * @param def
    * @return
    */
   //----------------------------------------------------------------
   protected StringBuffer dbg_level_2(final DefinitionIF def)
   //----------------------------------------------------------------
   {
      int iCnt = 0;
      Object obj = null;
      StringBuffer buf = new StringBuffer();
      String[] values = null;
      Operation[] operations = Operation.values();
      Map<String, AttrIF> attrs = null;

      // Get the attributes

      attrs = def.getAttributes();

      if (attrs != null)
      {
         for (AttrIF attr : attrs.values())
         {
            buf.append(_extIndent + DebugIF.INDENT_2 + iCnt + ": ");
            buf.append("name=" + attr.getFrameworkName());
            buf.append(", serviceName=" + attr.getServiceName());
            buf.append(", type=" + attr.getTypeAsString());
            buf.append(", access=" + attr.getAccessAsString());
            buf.append(", required=" + attr.isRequired());
            buf.append(", encrypted=" + attr.isEncrypted());
            buf.append(", allowmultivalue=" + attr.allowMultivalue());
            obj = attr.getValue();
            if (obj != null)
            {
               buf.append(", value=");
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
            buf.append("\n");

            for (Operation operation : operations)
            {
               buf.append(this.getFunctionData(operation, attr));
            }
            
            iCnt++;
         }

      }
      else
      {
         buf.append(_extIndent + DebugIF.INDENT_2 + "no attributes\n");
      }

      return buf;
   }

   /**
    * @param def
    * @return
    */
   //----------------------------------------------------------------
   protected StringBuffer dbg_level_3(final DefinitionIF def)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();

      return buf;
   }
   //----------------------------------------------------------------
   private StringBuffer getFunctionData(final Operation operation, final AttrIF attr)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();
      Map<String, TaskIF> taskMap = null;
      TaskIF task = null;
      List<ArgumentIF> args = null;
      Operation[] operArray = null;

      if (attr != null)
      {
         operArray = Operation.values();

         taskMap = attr.getTasks(operation);

         if (taskMap != null && !taskMap.isEmpty())
         {
            for (String taskId : taskMap.keySet())
            {
               
               buf.append(_extIndent + DebugIF.INDENT_3 + operation.toString() + ": ");

               task = taskMap.get(taskId);

               if (task != null)
               {
                  buf.append("id=" + taskId + ", " +
                     task.getFunctionClassname() + ": useexisting=" + task.useExisting() + "\n");

                  args = task.getArguments();

                  if (args != null)
                  {
                     for (ArgumentIF arg : args)
                     {
                        if (arg != null)
                        {
                           buf.append(_extIndent + DebugIF.INDENT_4 +
                              "Arg: name=" + arg.getName());
                           buf.append(", type=" + arg.getTypeAsString());
                           if (arg.getType() == ArgumentType.ATTRIBUTE)
                           {
                              buf.append(", value=" + arg.getValue());
                           }
                           else
                           {
                              buf.append(", value='" + arg.getValue() + "'");
                           }
                           buf.append("\n");
                        }
                     }
                  }
                  else
                  {
                     buf.append(_extIndent + DebugIF.INDENT_4 +
                        "no task arguments" + "\n");
                  }
               }
               else
               {
                  buf.append(DebugIF.NULL + "\n");
               }
            }
         }
         else
         {
         }
      }
      else
      {
         buf.append("No Function data" + "\n");
      }

      return buf;
   }
}
