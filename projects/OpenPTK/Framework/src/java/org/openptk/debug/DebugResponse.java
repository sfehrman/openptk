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

import org.openptk.common.ComponentIF;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.logging.LoggingLevel;

//===================================================================
public class DebugResponse extends DebugComponent
//===================================================================
{


   /**
    * @param comp
    * @return
    */
   //----------------------------------------------------------------
   @Override
   protected StringBuffer dbg_level_4(final ComponentIF comp)
   //----------------------------------------------------------------
   {
      ResponseIF response = null;
      StringBuffer buf = new StringBuffer();
      RequestIF request = null;
      LoggingLevel requestLevel = LoggingLevel.CONFIG;
      LoggingLevel resultLevel = LoggingLevel.CONFIG;

      response = (ResponseIF) comp;
      request = response.getRequest();

      if (comp.isDebug())
      {
         buf.append(super.dbg_level_4(comp));

         requestLevel = get_level(request);
         buf.append(INDENT_3 + "----- BEGIN: Request -----\n");

         DebugRequest dbgReq = new DebugRequest();

         buf.append(dbgReq.dbg_level_1(request));

         switch (requestLevel)
         {
            case FINE:
               buf.append(dbgReq.dbg_level_2(request)); // Logger.fine
               break;
            case FINER:
               buf.append(dbgReq.dbg_level_2(request)); // Logger.fine
               buf.append(dbgReq.dbg_level_3(request)); // Logger.finer
               break;
            case FINEST:
               buf.append(dbgReq.dbg_level_2(request)); // Logger.fine
               buf.append(dbgReq.dbg_level_3(request)); // Logger.finer
               buf.append(dbgReq.dbg_level_4(request)); // Logger.finest
               break;
         }

         buf.append(INDENT_3 + "----- END: Request -----\n");

         if (response.getResultsSize() > 0)
         {
            int iResult = 1;
            buf.append(INDENT_3 + "----- BEGIN: Results ----- Total = " + response.getResultsSize() + "\n");

            for (ComponentIF result : response.getResults())
            {
               buf.append(INDENT_3 + "===== Result: " + iResult + " =====\n");
               if (result.isDebug())
               {
                  buf.append(super.dbg_level_1(result));
                  resultLevel = get_level(result);

                  switch (resultLevel)
                  {
                     case FINE:
                        buf.append(super.dbg_level_2(result)); // Logger.fine
                        break;
                     case FINER:
                        buf.append(super.dbg_level_2(result)); // Logger.fine
                        buf.append(super.dbg_level_3(result)); // Logger.finer
                        break;
                     case FINEST:
                        buf.append(super.dbg_level_2(result)); // Logger.fine
                        buf.append(super.dbg_level_3(result)); // Logger.finer
                        buf.append(super.dbg_level_4(result)); // Logger.finest
                        break;
                  }
               }
               else
               {
                  buf.append(INDENT_3 + "NOTICE (DL4): Debug not enabled.\n");
               }
               iResult++;
            }
            buf.append(INDENT_3 + "----- END: Results -----\n");
         }
      }
      else
      {
         buf.append(INDENT_1 + "NOTICE (DL4): Debug not enabled.\n");
      }
      return buf;
   }
}
