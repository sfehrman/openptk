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

import org.openptk.api.Query;
import org.openptk.common.ComponentIF;
import org.openptk.common.RequestIF;
import org.openptk.logging.LoggingLevel;

//===================================================================
public class DebugRequest extends DebugComponent
//===================================================================
{
   /**
    * @param comp
    * @return
    */
   //----------------------------------------------------------------
   @Override
   protected StringBuffer dbg_level_1(final ComponentIF comp)
   //----------------------------------------------------------------
   {
      RequestIF request = null;
      StringBuffer buf = new StringBuffer();

      request = (RequestIF) comp;

      if (request.isDebug())
      {
         buf.append(super.dbg_level_1(request));
         buf.append(", operation=").append(request.getOperationAsString());
         buf.append(", attempts=").append(request.getAttempts());
      }
      else
      {
         buf.append(INDENT_1 + "NOTICE (DL1): Debug not enabled.\n");
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
      RequestIF request = null;
      Query query = null;

      request = (RequestIF) comp;

      if (request.isDebug())
      {
         buf.append(super.dbg_level_2(request));
         query = request.getQuery();

         buf.append(INDENT_1 + "Query: " + "\n");

         if (query != null)
         {
            buf.append(DebugIF.INDENT_2 + query.toXML() + "\n");
         }
         else
         {
            buf.append(DebugIF.INDENT_2 + "(No Query)\n");
         }

      }
      else
      {
         buf.append(INDENT_1 + "NOTICE (DL2): Debug not enabled.\n");
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
      RequestIF request = null;
      ComponentIF subject = null;
      StringBuffer buf = new StringBuffer();
      LoggingLevel subjectLevel = LoggingLevel.CONFIG;

      request = (RequestIF) comp;
      subject = request.getSubject();

      if (comp.isDebug())
      {
         buf.append(super.dbg_level_4(comp));

         if (subject != null)
         {
            subjectLevel = get_level(subject);
            buf.append(INDENT_3 + "----- BEGIN: Subject -----\n");
            buf.append(super.dbg_level_1(subject));

            switch (subjectLevel)
            {
               case FINE:
                  buf.append(super.dbg_level_2(subject)); // Logger.fine
                  break;
               case FINER:
                  buf.append(super.dbg_level_2(subject)); // Logger.fine
                  buf.append(super.dbg_level_3(subject)); // Logger.finer
                  break;
               case FINEST:
                  buf.append(super.dbg_level_2(subject)); // Logger.fine
                  buf.append(super.dbg_level_3(subject)); // Logger.finer
                  buf.append(super.dbg_level_4(subject)); // Logger.finest
                  break;
            }

            buf.append(INDENT_3 + "----- END:   Subject -----\n");
         }
         else
         {
            buf.append(INDENT_3 + "(No Subject)\n");
         }

      }
      else
      {
         buf.append(INDENT_1 + "NOTICE (DL4): Debug not enabled.\n");
      }

      return buf;
   }
}
