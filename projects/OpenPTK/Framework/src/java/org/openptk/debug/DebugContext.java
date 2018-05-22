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

import org.openptk.api.Query;
import org.openptk.authenticate.AuthenticatorIF;
import org.openptk.common.ComponentIF;
import org.openptk.context.ContextIF;
import org.openptk.definition.DefinitionIF;
import org.openptk.logging.Logger;
import org.openptk.logging.LoggingIF;
import org.openptk.spi.ServiceIF;
import org.openptk.spi.operations.OperationsIF;

//===================================================================
public class DebugContext extends DebugComponent implements DebugIF
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
      Logger.logInfo(process((ContextIF) obj, callerId));

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
      return process((ContextIF) obj, callerId);
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
      return process((ContextIF) obj, callerId);
   }

   //----------------------------------------------------------------
   private String process(final ContextIF ctx, final String callerId)
   //----------------------------------------------------------------
   {
      StringBuffer buf = new StringBuffer();

      buf.append(_extIndent + CLASS_NAME + ", callerId=" + callerId + "\n");
      buf.append(dbg_level_1(ctx));
      buf.append(dbg_level_2(ctx));
      buf.append(dbg_level_3(ctx));
      buf.append(dbg_level_4(ctx));

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
      StringBuffer buf = new StringBuffer();
      LoggingIF ctxlogger = null;

      if (comp != null)
      {
         buf.append(super.dbg_level_1(comp));

         buf.append(_extIndent + DebugIF.INDENT_1 + "Logger: ");

         ctxlogger = Logger.getLogger();
         if (ctxlogger != null)
         {
            buf.append(Integer.toString(ctxlogger.hashCode()) + ": ");
            buf.append(ctxlogger.getClass().getName());
         }
         else
         {
            buf.append(DebugIF.NULL);
         }
         buf.append("\n");
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
      StringBuffer buf = new StringBuffer();
      ContextIF context = null;
      AuthenticatorIF authen = null;
      Query query = null;

      if (comp.isDebug())
      {
         context = (ContextIF) comp;
         buf.append(super.dbg_level_2(comp));

         /*
          * Authenticator
          */

         buf.append(_extIndent + DebugIF.INDENT_1 + "Authenticator: ");

         authen = context.getAuthenticator();
         if (authen != null)
         {
            buf.append(
               (authen.getUniqueId() != null ? authen.getUniqueId().toString() : "(null)")
               + ", type=" + authen.getType().toString()
               + ", " + authen.getClass().getName()
               + ", " + authen.getDescription() + "\n");
         }
         else
         {
            buf.append(DebugContext.NULL + "\n");
         }

         /* 
          * Query
          */

         query = context.getQuery();

         buf.append(_extIndent + DebugIF.INDENT_1 + "Query: ");
         if (query != null)
         {
            buf.append(query.toXML());
         }
         else
         {
            buf.append(DebugIF.NULL + "\n");
         }

      }
      else
      {
         buf.append(_extIndent + INDENT_1 + "NOTICE (DL2): Debug not enabled.\n");
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
      ContextIF context = null;
      DefinitionIF definition = null;

      context = (ContextIF) comp;
      definition = context.getDefinition();

      if (comp.isDebug())
      {
         buf.append(super.dbg_level_3(comp));

         /*
          *  Definition information
          */

         buf.append(_extIndent + INDENT_1 + "Definition:\n");

         if (definition != null)
         {
            buf.append(_extIndent + DebugIF.INDENT_2 + "uniqueId: "
               + ((definition.getUniqueId() != null) ? definition.getUniqueId().toString() : DebugIF.NULL) + "\n");
            buf.append(_extIndent + DebugIF.INDENT_2 + "description: "
               + ((definition.getDescription() != null) ? definition.getDescription() : DebugIF.NULL) + "\n");
            buf.append(_extIndent + DebugIF.INDENT_2 + "ClassName: "
               + definition.getDefinitionClassName() + "\n");
         }
         else
         {
            buf.append(_extIndent + INDENT_2 + DebugIF.NULL + "\n");
         }
      }
      else
      {
         buf.append(_extIndent + INDENT_1 + "NOTICE (DL3): Debug not enabled.\n");
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
      String[] operations = null;
      ContextIF context = null;
      ServiceIF service = null;
      OperationsIF operation = null;
      DebugService dbgSrvc = null;

      context = (ContextIF) comp;
      service = context.getService();

      if (comp.isDebug())
      {
         buf.append(super.dbg_level_4(comp));

         /*
          * Operation information
          */

         buf.append(_extIndent + DebugIF.INDENT_1 + "Operation(s): ");

         operations = context.getOperationNames();
         if (operations != null && operations.length > 0)
         {
            buf.append(operations.length + "\n");
            for (int i = 0; i < operations.length; i++)
            {
               buf.append(_extIndent + INDENT_3 + i + ": ");
               operation = context.getOperation(operations[i]);
               if (operation != null)
               {
                  buf.append(
                     (operation.getUniqueId() != null ? operation.getUniqueId().toString() : "(null)")
                     + ": "
                     + operations[i]
                     + "\n");
               }
               else
               {
                  buf.append(DebugIF.NULL + "\n");
               }
            }
         }
         else
         {
            buf.append(_extIndent + INDENT_2 + DebugIF.NULL + "\n");
         }


         /*
          * Service information
          */

         buf.append(_extIndent + DebugIF.INDENT_1 + "Service(s): ");

         if (service != null)
         {
            buf.append("\n");
            dbgSrvc = new DebugService();
            buf.append(dbgSrvc.getData(service, CLASS_NAME, DebugIF.INDENT_1));
         }
         else
         {
            buf.append(_extIndent + INDENT_2 + DebugIF.NULL + "\n");
         }
      }
      else
      {
         buf.append(_extIndent + INDENT_1 + "NOTICE (DL4): Debug not enabled.\n");
      }

      return buf;
   }
}
