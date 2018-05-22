/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2008-2010 Sun Microsystems, Inc.
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

import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.logging.Logger;
import org.openptk.spi.operations.OperationsIF;

//===================================================================
public class DebugOperations extends DebugComponent implements DebugIF
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
      Logger.logInfo(process((OperationsIF) obj, callerId));

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
      return process((OperationsIF) obj, callerId);
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
      String tmp = _extIndent;
      String data = null;

      _extIndent = indent;
      data = process((OperationsIF) obj, callerId);
      _extIndent = tmp;

      return data;
   }

   //----------------------------------------------------------------
   private String process(final OperationsIF oper, final String callerId)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();

      buf.append(_extIndent + DebugIF.INDENT_1 + CLASS_NAME + ", callerId=" + callerId + "\n");
      buf.append(dbg_level_1(oper));
      buf.append(dbg_level_2(oper));
      buf.append(dbg_level_3(oper));
      buf.append(dbg_level_4(oper));

      /*
       * may add more debug levels in the future
       */

      return buf.toString();
   }

   /**
    * @param comp
    * @return
    */
   //----------------------------------------------------------------
   @Override
   protected StringBuffer dbg_level_1(final ComponentIF comp)
   //----------------------------------------------------------------
   {
      Object uid = null;
      String operName = null;
      OperationsIF oper = null;
      StringBuffer buf = new StringBuffer();
      Operation[] operArray = null;

      operArray = Operation.values();

      oper = (OperationsIF) comp;

      if (oper != null)
      {
         if (oper.isDebug())
         {
            buf.append(super.dbg_level_1(comp));

            buf.append(_extIndent + DebugIF.INDENT_1
               + "Type: " + oper.getTypeAsString() + "\n");

            // Show the implemented, enabled, timeout information

            uid = oper.getUniqueId();
            if (uid != null)
            {
               operName = uid.toString();
               for (int i = 0; i < operArray.length; i++)
               {
                  if (operArray[i].toString().equalsIgnoreCase(operName))
                  {
                     buf.append(_extIndent + DebugIF.INDENT_1
                        + "Implemented: " + oper.isImplemented(operArray[i]) + "\n"
                        + _extIndent + DebugIF.INDENT_1
                        + "Enabled: " + oper.isEnabled(operArray[i]) + "\n");
                  }
               }
            }
         }
         else
         {
            buf.append(_extIndent + DebugIF.INDENT_1 + "NOTICE (DL1): Debug not enabled.\n");
         }
      }
      else
      {
         buf.append(_extIndent + DebugIF.INDENT_1 + "WARNING: Operation is null\n");
      }

      return buf;
   }

   /**
    * @param comp
    * @return
    */
   //----------------------------------------------------------------
   @Override
   protected StringBuffer dbg_level_2(final ComponentIF comp)
   //----------------------------------------------------------------
   {
      OperationsIF oper = null;
      StringBuffer buf = new StringBuffer();

      oper = (OperationsIF) comp;

      if (oper != null)
      {
         if (oper.isDebug())
         {
            buf.append(super.dbg_level_2(comp));
         }
         else
         {
            buf.append(_extIndent + INDENT_1 + "NOTICE (DL2): Debug not enabled.\n");
         }
      }
      else
      {
         buf.append(_extIndent + DebugIF.INDENT_1 + "WARNING: Operation is null\n");
      }


      return buf;
   }

   /**
    * @param comp
    * @return
    */
   //----------------------------------------------------------------
   @Override
   protected StringBuffer dbg_level_3(final ComponentIF comp)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();
      OperationsIF oper = null;

      oper = (OperationsIF) comp;

      if (oper != null)
      {
         if (oper.isDebug())
         {
            buf.append(super.dbg_level_3(comp));

         }
         else
         {
            buf.append(_extIndent + INDENT_1 + "NOTICE (DL3): Debug not enabled.\n");
         }
      }
      else
      {
         buf.append(_extIndent + DebugIF.INDENT_1 + "WARNING: Operation is null\n");
      }


      return buf;
   }

   /**
    * @param comp
    * @return
    */
   //----------------------------------------------------------------
   @Override
   protected StringBuffer dbg_level_4(final ComponentIF comp)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();
      OperationsIF oper = null;

      oper = (OperationsIF) comp;

      if (oper != null)
      {
         if (oper.isDebug())
         {
            buf.append(super.dbg_level_4(comp));

         }
         else
         {
            buf.append(_extIndent + INDENT_1 + "NOTICE (DL4): Debug not enabled.\n");
         }
      }
      else
      {
         buf.append(_extIndent + DebugIF.INDENT_1 + "WARNING: Operation is null\n");
      }

      return buf;
   }
}
